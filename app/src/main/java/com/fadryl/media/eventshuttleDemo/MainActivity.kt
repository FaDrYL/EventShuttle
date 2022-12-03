package com.fadryl.media.eventshuttleDemo

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fadryl.media.data.TestData
import com.fadryl.media.data.TimeData
import com.fadryl.media.eventshuttle.EventShuttle
import com.fadryl.media.eventshuttleanno.EventStop
import com.fadryl.media.eventshuttlemesh.MeshStrategy
import com.fadryl.media.eventshuttlemp.base.IRemoteConnectionCallback
import com.fadryl.media.eventshuttlemp.departure
import com.fadryl.media.eventshuttlemp.registerFlightStrategy

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/10/24
 */
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity-1"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val remoteCallback = object: IRemoteConnectionCallback{
            override fun onConnected() {
                Log.i("DEMO1", "IRemoteConnectionCallback onConnected")
            }

            override fun onDisconnected() {
                Log.i("DEMO1", "IRemoteConnectionCallback onDisconnected")
            }

            override fun onBindingDied() {
                Log.i("DEMO1", "IRemoteConnectionCallback onBindingDied")
            }

            override fun onNullBinding() {
                Log.i("DEMO1", "IRemoteConnectionCallback onNullBinding")
            }

        }
        val meshStrategy = MeshStrategy().apply {
            addRemoteSubscriber(application, "com.fadryl.media.eventshuttleDemo2", remoteCallback)
        }
        EventShuttle.registerFlightStrategy(meshStrategy)

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

        Handler(mainLooper).postDelayed({
            EventShuttle.departure("testtest1")
            EventShuttle.departure("testtest1", TestData(20))
        }, 3000L)

        interactiveTest()
    }

    @EventStop("testtest1")
    fun test1() {
        Log.i(TAG, "test1: invoked")
    }

    @EventStop("testtest2")
    fun test2(testParam2: String) {
        Log.i(TAG, "test2: invoked, testParam2=$testParam2")
    }

    @EventStop("testtest2")
    fun test22(testParam22: String) {
        Log.i(TAG, "test22: invoked, testParam22=$testParam22")
    }

    @EventStop()
    fun test3(testParam3: String) {
        Log.i(TAG, "test3: invoked, testParam3=$testParam3")
    }

    @EventStop()
    fun test4(testParam4: TTT) {
        Log.i(TAG, "test4: invoked, testParam4=$testParam4")
    }

    @EventStop()
    fun test5(testParam5: TTT2) {
        Log.i(TAG, "test5: invoked, testParam5=$testParam5")
    }

    @EventStop(channel = "ccTest")
    fun test6(testParam6: Int) {
        Log.i(TAG, "test6: invoked, testParam6=$testParam6")
    }

    private fun interactiveTest() {
        findViewById<Button>(R.id.btn_on).setOnClickListener {
            EventShuttle.departure("on", "on_remote")
            EventShuttle.departure("", "time_count", TimeData(System.currentTimeMillis()))
        }
        findViewById<Button>(R.id.btn_off).setOnClickListener {
            EventShuttle.departure("off", "off_remote")
            EventShuttle.departure("", "time_count", TimeData(System.currentTimeMillis()))
        }
    }

    @EventStop("on")
    fun set2On() {
        runOnUiThread {
            findViewById<TextView>(R.id.local_val).apply {
                text = "ON"
                setTextColor(Color.GREEN)
            }
        }
    }

    @EventStop("off")
    fun set2Off() {
        runOnUiThread {
            findViewById<TextView>(R.id.local_val).apply {
                text = "OFF"
                setTextColor(Color.RED)
            }
        }
    }

    @EventStop("on_remote")
    fun set2OnR() {
        runOnUiThread {
            findViewById<TextView>(R.id.remote_val).apply {
                text = "ON"
                setTextColor(Color.GREEN)
            }
        }
    }

    @EventStop("off_remote")
    fun set2OffR() {
        runOnUiThread {
            findViewById<TextView>(R.id.remote_val).apply {
                text = "OFF"
                setTextColor(Color.RED)
            }
        }
    }

    @EventStop("time_count")
    fun receiveTime(timeData: TimeData) {
        Log.i(TAG, "communication time: ${System.currentTimeMillis() - timeData.time}")
    }

    data class TTT(val name: String)
}

data class TTT2(val name: String)