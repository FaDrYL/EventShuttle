package com.fadryl.media.eventshuttle

import android.util.Log
import com.fadryl.media.eventshuttleanno.EventStop
import com.fadryl.media.eventshuttlemp.FlightManager
import com.fadryl.media.eventshuttlemp.FlightStrategy
import java.util.concurrent.Executors
import kotlin.reflect.KCallable
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/10/24
 */
object EventShuttle {
    private const val TAG = "EventShuttle"

    private val eventMap0: EventMap = hashMapOf()
    private val eventAsyncMap0: EventMap = hashMapOf()
    private val eventMap1: EventMap = hashMapOf()
    private val eventAsyncMap1: EventMap = hashMapOf()
    private val paramEventMap: EventMap = hashMapOf()
    private val paramEventAsyncMap: EventMap = hashMapOf()

    private val executorService by lazy {
        Executors.newCachedThreadPool()
    }
    private val objectMap: HashMap<String, ArrayList<Any>> = hashMapOf()
    private val methodCallableCache: HashMap<String, KCallable<*>> = hashMapOf()
    private var channelMap: HashMap<String, (EventCallable) -> Unit>? = null

    init {
        val clazz = Class.forName("com.fadryl.media.eventshuttle.autogen.EventShuttleCollector")
        val instance = clazz.newInstance()
        clazz.getDeclaredMethod("loadEventMap0", HashMap::class.java, HashMap::class.java)
            .invoke(instance, eventMap0, eventAsyncMap0)
        clazz.getDeclaredMethod("loadEventMap1", HashMap::class.java, HashMap::class.java)
            .invoke(instance, eventMap1, eventAsyncMap1)
        clazz.getDeclaredMethod("loadParamMap", HashMap::class.java, HashMap::class.java)
            .invoke(instance, paramEventMap, paramEventAsyncMap)
    }

    fun registerFlightStrategy(flightStrategy: FlightStrategy) {
        FlightManager.useStrategy(flightStrategy)
    }

    fun setChannelMap(channelMap: HashMap<String, (EventCallable) -> Unit>) {
        if (this.channelMap == null) {
            synchronized(EventShuttle::class.java) {
                if (this.channelMap == null) {
                    this.channelMap = channelMap
                }
            }
        }
    }

    fun register(obj: Any) {
        val objType = obj::class.qualifiedName ?: obj.javaClass.name
        objectMap[objType]?.add(obj) ?: run { objectMap[objType] = arrayListOf(obj) }
    }

    fun unregister(obj: Any) {
        val objType = obj::class.qualifiedName ?: obj.javaClass.name
        objectMap[objType]?.remove(obj)
        if (objectMap[objType]?.isEmpty() == true) {
            objectMap.remove(objType)
        }
    }

    fun fire(eventName: String) {
        FlightManager.fireEvent(eventName)
        fireLocally(eventName)
    }

    fun fire(eventName: String, data: Any) {
        FlightManager.fireEvent(eventName, data)
        fireLocally(eventName, data)
    }

    fun fire(data: Any) {
        FlightManager.fireEvent(data)
        fireLocally(data)
    }

    fun fireLocally(eventName: String) {
        fireAux(eventMap0[eventName], eventAsyncMap0[eventName], null)
    }

    fun fireLocally(eventName: String, data: Any) {
        fireAux(eventMap1[eventName], eventAsyncMap1[eventName], data)
    }

    fun fireLocally(data: Any) {
        fireAux(paramEventMap[data::class.qualifiedName], paramEventAsyncMap[data::class.qualifiedName], data)
    }

    private fun fireAux(eventArrayList: ArrayList<EventStop>?, asyncEventArrayList: ArrayList<EventStop>?, data: Any?) {
        Log.i(TAG, "fireAux: ------------------------------------------------------")
        if (eventArrayList.isNullOrEmpty() && asyncEventArrayList.isNullOrEmpty()) {
            Log.w(TAG, "Cannot fire the event since no subscriber found")
            return
        }

        // Async
        asyncEventArrayList?.let {
            fireAux(it, data) {
                executorService.execute {
                    it.invoke()
                }
            }
        }

        // Serial
        eventArrayList?.let {
            fireAux(it, data)
        }
    }

    private fun fireAux(
        eventStops: ArrayList<EventStop>,
        data: Any? = null,
        callable: (EventCallable) -> Unit = { it.invoke() }
    ) {
        eventStops.forEach { eventStop ->
            val objects = objectMap[eventStop.className] ?: return@forEach
            val channel = channelMap?.get(eventStop.channel)
            objects.forEach { obj ->
                // do some customization before calling the actual single fire()
                callable.invoke {
                    // call the fire() in the specified channel
                    channel?.invoke {  fireAux(eventStop, obj, data) } ?: fireAux(eventStop, obj, data)
                }
            }
        }
    }

    private fun fireAux(eventStop: EventStop, obj: Any, data: Any? = null) {
        val clazz = obj::class.java
        if (data == null) {
            clazz.getDeclaredMethod(eventStop.functionName).invoke(obj)
        } else {
            try {
                clazz.getDeclaredMethod(eventStop.functionName, data::class.java).invoke(obj, data)
            } catch (e: NoSuchMethodException) {
                // Some primitive data type in kotlin will have some issues when using above way.
                // e.g. Int declared in kotlin function is <kotlin.Integer>, Int::class.java will get <java.lang.Integer>.
                // Therefore, the parameter type is unmatched for such method.
                val dataType = data::class.java
                val dataObjectType = data::class.javaObjectType
                val key = clazz.name + "$" + eventStop.functionName + "${clazz.name}$${eventStop.functionName}(${dataType})"
                try {
                    synchronized(eventStop) {
                        methodCallableCache.getOrPut(key) {
                            Log.w(TAG, "Using fallback strategy and no cache, time consuming warning! " +
                                "data::class.java.name: ${data::class.java.name}, " +
                                "data::class.qualifiedName: ${data::class.qualifiedName}")
                            obj::class.declaredMemberFunctions.find {
                                it.name == eventStop.functionName && it.parameters.size == 2
                                        && (it.parameters[1].type::class == dataType || it.parameters[1].type.jvmErasure.javaObjectType == dataObjectType)
                            } ?: throw NoSuchMethodException(
                                "Method for such name is not found or " +
                                        "number of parameter is invalid for the Method (should be exactly 1 parameter)"
                            )
                        }.apply {
                            isAccessible = true
                        }
                    }.call(obj, data)
                } catch (t: Throwable) {
                    Log.e(TAG, "fireAux: ${t.message}")
                }
            }
        }
    }
}