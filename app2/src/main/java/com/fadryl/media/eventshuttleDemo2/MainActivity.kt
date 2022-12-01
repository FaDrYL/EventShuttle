package com.fadryl.media.eventshuttleDemo2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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

        EventShuttle.setClassLoader(classLoader)
        EventShuttle.register(this)
    }

    @SubscribeEvent("testtest1")
    fun test1() {
        Log.i(TAG, "test1: invoked")
    }
}