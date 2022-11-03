package com.fadryl.media.eventshuttleprocessor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/10/24
 */
class EventShuttleProcessorProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return EventShuttleProcessor(environment.codeGenerator, environment.logger)
    }
}