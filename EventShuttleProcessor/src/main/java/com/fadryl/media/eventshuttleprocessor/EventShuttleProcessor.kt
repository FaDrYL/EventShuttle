package com.fadryl.media.eventshuttleprocessor

import com.fadryl.media.eventshuttleanno.SubscribeEvent
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/10/24
 */
class EventShuttleProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger
): SymbolProcessor {
    companion object {
        private const val TAG = "EventShuttleProcessor"
        private const val PACKAGE_NAME = "com.fadryl.media.eventshuttle.autogen"
        private const val FILENAME_COLLECTOR = "EventShuttleCollector"
    }

    private lateinit var funBuilderLoadEventMap0: FunSpec.Builder
    private lateinit var funBuilderLoadEventMap1: FunSpec.Builder
    private lateinit var funBuilderLoadParamMap: FunSpec.Builder

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logInfo("Start to Process!")
        val symbols = resolver.getSymbolsWithAnnotation(SubscribeEvent::class.java.name)

        val hashMapK = ClassName("java.util", "HashMap")
        val stringK = ClassName("kotlin", "String")
        val arrayListK = ClassName("kotlin.collections", "ArrayList")
        val eventStopK = ClassName("com.fadryl.media.eventshuttleanno", "EventStop")
        val arrayListEsK = arrayListK.parameterizedBy(eventStopK)               // ArrayList<EventStop>
        val hashMapSAlEsK = hashMapK.parameterizedBy(stringK, arrayListEsK)     // HashMap<String, ArrayList<EventStop>>

        // loadEventMap0(map: HashMap<String, ArrayList<EventStop>>, asyncMap: HashMap<String, ArrayList<EventStop>>)
        funBuilderLoadEventMap0 = FunSpec.builder("loadEventMap0")
            .addParameter(ParameterSpec.builder("map", hashMapSAlEsK).build())
            .addParameter(ParameterSpec.builder("asyncMap", hashMapSAlEsK).build())
        funBuilderLoadEventMap1 = FunSpec.builder("loadEventMap1")
            .addParameter(ParameterSpec.builder("map", hashMapSAlEsK).build())
            .addParameter(ParameterSpec.builder("asyncMap", hashMapSAlEsK).build())
        funBuilderLoadParamMap = FunSpec.builder("loadParamMap")
            .addParameter(ParameterSpec.builder("map", hashMapSAlEsK).build())
            .addParameter(ParameterSpec.builder("asyncMap", hashMapSAlEsK).build())

        symbols.filter { it is KSFunctionDeclaration && it.validate() }
            .forEach {
                logInfo("-----")
                it as KSFunctionDeclaration
                val _parent = it.parentDeclaration
                if (_parent !is KSClassDeclaration || it.functionKind != FunctionKind.MEMBER || it.isAbstract) {
                    logger.error("Annotation should be added to a member function and also is not a abstract method", it)
                }
                val parameters = it.parameters
                if (parameters.size > 1) {
                    logger.error("Annotation should be added to the function with only 0 or 1 parameter")
                }

                val subscribeEvent = it.getAnnotationsByType(SubscribeEvent::class).first()
                val eventName = subscribeEvent.eventName
                logInfo("eventName: $eventName")

                if (parameters.isEmpty() && eventName.isEmpty()) {
                    logger.error("Annotation should be added to the function with 0 parameter only if eventName is specified as a non-empty string")
                }

                val parent = _parent as KSClassDeclaration
                val fullClassName = parent.qualifiedName?.asString() ?:
                    ClassName(parent.packageName.asString(), parent.simpleName.asString()).reflectionName()
                val funcName = it.simpleName.asString()
                logInfo("fullClassName: $fullClassName")
                logInfo("funcName: $funcName")

                val isAsync = subscribeEvent.isAsync
                val subscribeChannel = subscribeEvent.channel
                when {
                    parameters.isEmpty() -> {
                        // @SubscribeEvent(eventName="xxx")
                        // fun onXxxEvent() {}
                        funBuilderLoadEventMap0.addDefinedMapStatement(eventName, fullClassName, funcName, isAsync, subscribeChannel)
                    }
                    eventName.isNotEmpty() -> {
                        // @SubscribeEvent(eventName="xxx")
                        // fun onXxxEvent(param: String) {}
                        funBuilderLoadEventMap1.addDefinedMapStatement(eventName, fullClassName, funcName, isAsync, subscribeChannel)
                    }
                    else -> {
                        // @SubscribeEvent     or     @SubscribeEvent(eventName="")
                        // fun onXxxEvent(param: String) {}
                        val paramDeclaration = parameters.first().type.resolve().declaration
                        val parameterType = paramDeclaration.qualifiedName?.asString() ?:
                                ClassName(paramDeclaration.packageName.asString(), paramDeclaration.simpleName.asString()).reflectionName()
                        funBuilderLoadParamMap.addDefinedMapStatement(parameterType, fullClassName, funcName, isAsync, subscribeChannel)
                    }
                }
            }

        return symbols.filter { !it.validate() }.toList()
    }

    private fun FunSpec.Builder.addDefinedMapStatement(
        key: String,
        className: String,
        funcName: String,
        isAsync: Boolean,
        subscribeChannel: String
    ): FunSpec.Builder = this.apply {
        val mapName = if (isAsync) "asyncMap" else "map"
        addStatement("EventStop(\"$className\", \"$funcName\", \"$subscribeChannel\").let {")
        addStatement("    $mapName[\"$key\"]?.add(it) ?: run {")
        addStatement("        $mapName[\"$key\"] = arrayListOf(it)")
        addStatement("    }")
        addStatement("}")
    }

    override fun finish() {
        super.finish()
        logInfo("Start to write into file!")
        val funSpec = createFileSpecByFunSpec(funBuilderLoadEventMap0, funBuilderLoadEventMap1, funBuilderLoadParamMap)
        funSpec?.let {
            writeToFile(it)
        }
        logInfo("Process finished!")
    }

    private fun createFileSpecByFunSpec(vararg funBuilders: FunSpec.Builder?): FileSpec? {
        val validFunSpecs = funBuilders.filterNotNull()
        if (validFunSpecs.isEmpty()) return null

        val fileSpec = FileSpec.builder(PACKAGE_NAME, FILENAME_COLLECTOR)
        val typeSpec = TypeSpec.classBuilder(FILENAME_COLLECTOR)
        validFunSpecs.forEach {
            typeSpec.addFunction(it.build())
        }
        fileSpec.addType(typeSpec.build())
        return fileSpec.build()
    }

    private fun writeToFile(fileSpec: FileSpec) {
        val file = codeGenerator.createNewFile(
            Dependencies.ALL_FILES,
            fileSpec.packageName,
            fileSpec.name
        )
        file.use {
            val content = fileSpec.toString().toByteArray()
            it.write(content)
        }
    }

    private fun logInfo(msg: String, symbol: KSNode? = null) {
        logger.info("<$TAG> $msg", symbol)
    }
}