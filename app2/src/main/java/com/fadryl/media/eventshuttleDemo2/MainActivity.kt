package com.fadryl.media.eventshuttleDemo2

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fadryl.media.eventshuttle.EventShuttle
import com.fadryl.media.eventshuttleanno.EventStop

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
        interactiveTest()
    }

    @EventStop("testtest1")
    fun test1() {
        Log.i(TAG, "test1: invoked")
    }

    private fun interactiveTest() {
        findViewById<Button>(R.id.btn_on).setOnClickListener {
            EventShuttle.departure("on", "on_remote")
        }
        findViewById<Button>(R.id.btn_off).setOnClickListener {
            EventShuttle.departure("off", "off_remote")
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
}