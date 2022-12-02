package com.fadryl.media.eventshuttlemp.base

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/12/02
 */
interface IEventLandable {
    fun landEvent(eventName: String?, data: Any?)
}