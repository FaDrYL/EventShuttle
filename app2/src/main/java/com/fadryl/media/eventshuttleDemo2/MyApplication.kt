package com.fadryl.media.eventshuttleDemo2

import android.app.Application
import android.util.Log
import com.fadryl.media.data.TestData
import com.fadryl.media.eventshuttle.EventShuttle
import com.fadryl.media.eventshuttleanno.SubscribeEvent

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/11/30
 */
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        EventShuttle.register(this)
    }

    @SubscribeEvent("testtest1")
    fun test1() {
        Log.i("MyApplication", "testtest1 in application")
    }

    @SubscribeEvent("testtest1")
    fun test2(data: TestData) {
        Log.i("MyApplication", "testtest1 in application, data=$data")
    }
}