package com.fadryl.media.eventshuttlemesh

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.fadryl.media.eventshuttlemp.FlightStrategy
import com.fadryl.media.eventshuttlemp.IEventHandler

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/11/30
 */
class MeshStrategy: FlightStrategy() {
    private val receivers = HashMap<String, IEventHandler>()
    private val connections = arrayListOf<ServiceConnection>()

    companion object {
        private const val TAG = "MeshStrategy"
    }

    override fun fireEvent(eventName: String?, data: Bundle?) {
        receivers.forEach { entry ->
            entry.value.handleEvent(eventName, data)
        }
    }

    fun addRemoteSubscriber(context: Application, packageName: String) {
        val intent = Intent()
        intent.component = ComponentName(packageName, "com.fadryl.media.eventshuttlemesh.MeshService")
        context.bindService(
            intent,
            object: ServiceConnection {
                override fun onServiceConnected(componentName: ComponentName?, iBinder: IBinder?) {
                    Log.i(TAG, "Connected to ${componentName?.packageName}")
                    connections.add(this)
                    val eventHandler = IEventHandler.Stub.asInterface(iBinder)
                    val key = componentName?.packageName
                    if (key != null && eventHandler != null) {
                        receivers[key] = eventHandler
                    }
                }

                override fun onServiceDisconnected(componentName: ComponentName?) {
                    Log.i(TAG, "Disconnected with ${componentName?.packageName}")
                    connections.remove(this)
                    componentName?.packageName?.let {
                        receivers.remove(it)
                    }
                }
            },
            Context.BIND_AUTO_CREATE
        )
    }

    fun shutdown(context: Application) {
        connections.forEach {
            context.unbindService(it)
        }
        receivers.clear()
    }
}