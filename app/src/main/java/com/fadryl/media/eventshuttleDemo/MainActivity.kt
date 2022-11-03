package com.fadryl.media.eventshuttleDemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.fadryl.media.eventshuttle.EventShuttle
import com.fadryl.media.eventshuttleanno.SubscribeEvent

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/10/24
 */
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        EventShuttle.register(this)
        EventShuttle.setChannelMap(hashMapOf(
            "ccTest" to {
                Log.d(TAG, "vvv============== ccTest channel ==============vvv")
                it.invoke()
                Log.d(TAG, "^^^============== ccTest channel ==============^^^")
            }
        ))
        ATestClass()
        val aTestClass = ATestClass()
        EventShuttle.fire("testtest1")
        EventShuttle.fire("testtest1", false)
        EventShuttle.fire("testtest2", "1234567890")
        EventShuttle.fire(data = "a string")
        EventShuttle.fire(data = TTT("TTT-Test4"))
        EventShuttle.fire(data = TTT2("TTT-Test5"))
        for (i in 0..5) {
            if (i == 3) {
                EventShuttle.unregister(aTestClass)
            }
            EventShuttle.fire(data = i)
        }
        EventShuttle.unregister(this)
    }

    @SubscribeEvent("testtest1")
    fun test1() {
        Log.i(TAG, "test1: invoked")
    }

    @SubscribeEvent("testtest2")
    fun test2(testParam2: String) {
        Log.i(TAG, "test2: invoked, testParam2=$testParam2")
    }

    @SubscribeEvent("testtest2")
    fun test22(testParam22: String) {
        Log.i(TAG, "test22: invoked, testParam22=$testParam22")
    }

    @SubscribeEvent()
    fun test3(testParam3: String) {
        Log.i(TAG, "test3: invoked, testParam3=$testParam3")
    }

    @SubscribeEvent()
    fun test4(testParam4: TTT) {
        Log.i(TAG, "test4: invoked, testParam4=$testParam4")
    }

    @SubscribeEvent()
    fun test5(testParam5: TTT2) {
        Log.i(TAG, "test5: invoked, testParam5=$testParam5")
    }

    @SubscribeEvent(channel = "ccTest")
    fun test6(testParam6: Int) {
        Log.i(TAG, "test6: invoked, testParam6=$testParam6")
    }

    data class TTT(val name: String)
}

data class TTT2(val name: String)