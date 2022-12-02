package com.fadryl.media.eventshuttlemp

import android.util.Log
import com.fadryl.media.eventshuttlemp.base.IEventLandable

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/11/30
 */
object FlightManager: IEventLandable {
    @Volatile private var strategy: FlightStrategy? = null
    @Volatile private var eventLandable: IEventLandable? = null

    fun init(flightStrategy: FlightStrategy, eventLandable: IEventLandable) {
        if (strategy == null) {
            synchronized(this) {
                if (strategy == null) {
                    strategy = flightStrategy
                    this.eventLandable = eventLandable
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
        eventLandable = null
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

    override fun landEvent(eventName: String?, data: Any?) {
        eventLandable?.landEvent(eventName, data) ?: warnNotInit()
    }
}