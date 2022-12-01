package com.fadryl.media.eventshuttlemp

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/11/30
 */
object FlightManager {
    @Volatile var strategy: FlightStrategy? = null
        private set

    fun useStrategy(flightStrategy: FlightStrategy) {
        if (strategy == null) {
            synchronized(this) {
                if (strategy == null) {
                    strategy = flightStrategy
                }
            }
        }
    }

    fun fireEvent(eventName: String) {
        strategy?.fireEvent(eventName=eventName)
    }

    fun fireEvent(data: Any) {
        strategy?.fireEvent(data=data)
    }

    fun fireEvent(eventName: String, data: Any) {
        strategy?.fireEvent(eventName=eventName, data=data)
    }
}