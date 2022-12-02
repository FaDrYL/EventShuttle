package com.fadryl.media.eventshuttleanno

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/10/24
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class EventStop(
    val eventName: String = "",
    val isAsync: Boolean = false,
    val channel: String = ""
)