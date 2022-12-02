package com.fadryl.media.eventshuttleDemo2

import android.app.Application
import android.util.Log
import com.fadryl.media.data.TestData
import com.fadryl.media.eventshuttle.EventShuttle
import com.fadryl.media.eventshuttleanno.EventStop
import com.fadryl.media.eventshuttlemesh.MeshStrategy
import com.fadryl.media.eventshuttlemp.base.IRemoteConnectionCallback

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/11/30
 */
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()


        val remoteCallback = object: IRemoteConnectionCallback {
            override fun onConnected() {
                Log.i("DEMO2", "IRemoteConnectionCallback onConnected")
            }

            override fun onDisconnected() {
                Log.i("DEMO2", "IRemoteConnectionCallback onDisconnected")
            }

            override fun onBindingDied() {
                Log.i("DEMO2", "IRemoteConnectionCallback onBindingDied")
            }

            override fun onNullBinding() {
                Log.i("DEMO2", "IRemoteConnectionCallback onNullBinding")
            }

        }
        EventShuttle.registerFlightStrategy(MeshStrategy().apply {
            addRemoteSubscriber(this@MyApplication, "com.fadryl.media.eventshuttleDemo", remoteCallback)
        })
        EventShuttle.register(this)
    }

    @EventStop("testtest1")
    fun test1() {
        Log.i("MyApplication", "testtest1 in application")
    }

    @EventStop("testtest1")
    fun test2(data: TestData) {
        Log.i("MyApplication", "testtest1 in application, data=$data")
    }
}