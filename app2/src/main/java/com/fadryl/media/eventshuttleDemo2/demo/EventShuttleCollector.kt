package com.fadryl.media.eventshuttleDemo2.demo

import com.fadryl.media.eventshuttleanno.EventStopDetail

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/10/24
 */
class EventShuttleCollector {
    fun loadEventMap0(
        map: HashMap<String, ArrayList<EventStopDetail>>,
        asyncMap: HashMap<String, ArrayList<EventStopDetail>>
    ) {
        EventStopDetail("className", "functionName", "channel").let {
            map["eventName"]?.add(it) ?: run {
                map["eventName"] = arrayListOf(it)
            }
        }

        EventStopDetail("className", "functionName", "channel").let {
            asyncMap["eventName"]?.add(it) ?: run {
                asyncMap["eventName"] = arrayListOf(it)
            }
        }
    }

    fun loadEventMap1(map: HashMap<String, ArrayList<EventStopDetail>>) {
        EventStopDetail("className", "functionName", "channel").let {
            map["eventName"]?.add(it) ?: run {
                map["eventName"] = arrayListOf(it)
            }
        }
    }

    fun loadParamMap(map: HashMap<String, ArrayList<EventStopDetail>>) {
        EventStopDetail("className", "functionName", "channel").let {
            map["paramTypeName"]?.add(it) ?: run {
                map["paramTypeName"] = arrayListOf(it)
            }
        }
    }
}