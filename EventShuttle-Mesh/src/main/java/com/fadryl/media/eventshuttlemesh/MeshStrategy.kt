package com.fadryl.media.eventshuttlemesh

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import com.fadryl.media.eventshuttle.EventShuttle
import com.fadryl.media.eventshuttlemp.IEventHandler
import com.fadryl.media.eventshuttlemp.base.FlightStrategy
import com.fadryl.media.eventshuttlemp.base.IRemoteConnectionCallback
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/11/30
 */
class MeshStrategy: FlightStrategy() {
    private val receivers = ConcurrentHashMap<String, IEventHandler>()
    private val connections = ConcurrentHashMap<String, ServiceConnection>()

    companion object {
        private const val TAG = "MeshStrategy"
    }

    override fun departure(eventName: String?, data: Any?) {
        if (data != null && data !is Parcelable) {
            Log.e(TAG, "departure: data should be a Parcelable")
            return
        }

        val bundle = data?.let {
            val b = Bundle()
            b.putString("class", it.javaClass.simpleName)
            b.putParcelable("data", it as Parcelable)
            b
        }

        departure(eventName, bundle)
    }

    private fun departure(eventName: String?, data: Bundle?) {
        receivers.forEach { entry ->
            entry.value.handleEvent(eventName, data)
        }
    }

    override fun land(eventName: String?, data: Any?) {
        when {
            eventName != null && data != null -> EventShuttle.fire(eventName, data)
            eventName != null -> EventShuttle.fire(eventName)
            data != null -> EventShuttle.fire(data)
            else -> Log.e("EventShuttle-Mesh", "land: both eventName and data are null")
        }
    }

    /**
     * For Api level 30+, please remember to add this to AndroidManifest:
     * <pre>{@code
     * <queries>
     *     <package android:name="targetPackageName" />
     * </queries>
     * }</pre>
     */
    fun addRemoteSubscriber(
        context: Application,
        packageName: String,
        callback: IRemoteConnectionCallback? = null
    ) {
        val intent = Intent()
        intent.component = ComponentName(packageName, "com.fadryl.media.eventshuttlemesh.MeshService")
        context.bindService(
            intent,
            object: ServiceConnection {
                override fun onServiceConnected(componentName: ComponentName?, iBinder: IBinder?) {
                    callback?.onConnected()
                    connections[packageName] = this
                    val eventHandler = IEventHandler.Stub.asInterface(iBinder)
                    val key = componentName?.packageName
                    if (key != null && eventHandler != null) {
                        receivers[packageName] = eventHandler
                    }
                }

                override fun onServiceDisconnected(componentName: ComponentName?) {
                    callback?.onDisconnected()
                    connections.remove(packageName)
                    componentName?.packageName?.let {
                        receivers.remove(it)
                    }
                }

                override fun onBindingDied(name: ComponentName?) {
                    super.onBindingDied(name)
                    callback?.onBindingDied()
                }

                override fun onNullBinding(name: ComponentName?) {
                    super.onNullBinding(name)
                    callback?.onNullBinding()
                }
            },
            Context.BIND_AUTO_CREATE
        )
    }

    fun removeRemoteSubscriber(context: Application, packageName: String) {
        connections[packageName]?.let {
            context.unbindService(it)
        }
        connections.remove(packageName)
        receivers.remove(packageName)
    }

    fun clear(context: Application) {
        connections.forEach {
            context.unbindService(it.value)
        }
        connections.clear()
        receivers.clear()
    }
}