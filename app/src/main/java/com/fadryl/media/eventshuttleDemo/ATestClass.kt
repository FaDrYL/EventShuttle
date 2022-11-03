package com.fadryl.media.eventshuttleDemo

import android.util.Log
import com.fadryl.media.eventshuttle.EventShuttle
import com.fadryl.media.eventshuttleanno.SubscribeEvent

class ATestClass {
    init {
        EventShuttle.register(this)
    }

    @SubscribeEvent("testtest1", isAsync = true)
    fun test1() {
        Thread.sleep(4000L)
        Log.i("ATestClass", "test1, testtest1: invoked, $this")
    }

    @SubscribeEvent("testtest1", isAsync = true)
    fun test2(data: Boolean) {
        Thread.sleep(3000L)
        Log.i("ATestClass", "test2, testtest1: invoked, data=$data, $this")
    }

    @SubscribeEvent("testtest1")
    fun test3(data: Boolean) {
        Log.i("ATestClass", "test3, testtest1: invoked, data=$data, $this")
    }

    @SubscribeEvent()
    fun test5(testParam5: TTT2) {
        Log.i("ATestClass", "test5: invoked, testParam5=$testParam5, $this")
    }

    @SubscribeEvent()
    fun test6(testParam6: Int) {
        Log.i("ATestClass", "test6: invoked, testParam6=$testParam6, $this")
    }
}