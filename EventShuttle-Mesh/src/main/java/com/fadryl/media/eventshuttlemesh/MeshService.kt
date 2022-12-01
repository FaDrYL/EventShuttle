package com.fadryl.media.eventshuttlemesh

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import com.fadryl.media.eventshuttle.EventShuttle
import com.fadryl.media.eventshuttlemp.IEventHandler

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/11/30
 */
class MeshService: Service() {
    override fun onCreate() {
        super.onCreate()
    }
    override fun onBind(intent: Intent?): IBinder {
        return AirportBinder()
    }

    class AirportBinder: IEventHandler.Stub() {
        companion object {
            private fun getActualData(bundle: Bundle): Parcelable? {
                bundle.classLoader = this::class.java.classLoader
                bundle.getString("class")?.let { className ->
                    return bundle.getParcelable("data")
                }
                return null
            }
        }

        private fun handleEventForName(name: String) {
            EventShuttle.fireLocally(name)
        }

        private fun handleEventForData(bundle: Bundle) {
            getActualData(bundle)?.let {
                EventShuttle.fireLocally(it)
            }
        }

        private fun handleEventForAll(name: String, bundle: Bundle) {
            getActualData(bundle)?.let {
                EventShuttle.fireLocally(name, it)
            }
        }

        override fun handleEvent(name: String?, bundle: Bundle?) {
            when {
                name != null && bundle != null -> handleEventForAll(name, bundle)
                name != null -> handleEventForName(name)
                bundle != null -> handleEventForData(bundle)
            }
        }
    }
}