package com.fadryl.media.eventshuttlemp

import android.util.Log
import com.fadryl.media.eventshuttlemp.base.FlightStrategy

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/11/30
 */
object FlightManager {
    @Volatile var strategy: FlightStrategy? = null
        private set

    fun init(flightStrategy: FlightStrategy) {
        if (strategy == null) {
            synchronized(this) {
                if (strategy == null) {
                    strategy = flightStrategy
                }
            }
        }
    }

    private fun warnNotInit() {
        reset()
        Log.e("EventShuttle", "Please register a FlightStrategy (should be the same for all apps) to use cross-app event distribution feature " +
                "by 'EventShuttle.registerFlightStrategy({FlightStrategy})'")
    }

    private fun reset() {
        strategy = null
    }

    fun departureEvent(eventName: String) {
        strategy?.departure(eventName=eventName) ?: warnNotInit()
    }

    fun departureEvent(data: Any) {
        strategy?.departure(data=data) ?: warnNotInit()
    }

    fun departureEvent(eventName: String, data: Any) {
        strategy?.departure(eventName=eventName, data=data) ?: warnNotInit()
    }

    fun landEvent(eventName: String?, data: Any?) {
        strategy?.land(eventName, data)
    }
}