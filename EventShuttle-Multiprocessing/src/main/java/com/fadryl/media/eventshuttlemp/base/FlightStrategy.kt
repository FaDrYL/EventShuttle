package com.fadryl.media.eventshuttlemp.base

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/11/30
 */
abstract class FlightStrategy {
    /*
     * OUT
     */
    abstract fun departure(eventName: String? = null, data: Any? = null)

    /**
     * IN
     */
    abstract fun land(eventName: String?, data: Any?)
}