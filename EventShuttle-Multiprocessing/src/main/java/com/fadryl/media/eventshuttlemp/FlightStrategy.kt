package com.fadryl.media.eventshuttlemp

import android.os.Bundle
import android.os.Parcelable

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/11/30
 */
abstract class FlightStrategy {
    /*
     * OUT
     */
    fun fireEvent(eventName: String? = null, data: Any? = null) {
        if (data != null && data !is Parcelable) return

        val bundle = data?.let {
            val b = Bundle()
            b.putString("class", it.javaClass.simpleName)
            b.putParcelable("data", it as Parcelable)
            b
        }

        fireEvent(eventName, bundle)
    }

    protected abstract fun fireEvent(eventName: String?, data: Bundle?)
}