package com.fadryl.media.eventshuttlemp.base

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/12/02
 */
interface IRemoteConnectionCallback {
    fun onConnected()
    fun onDisconnected()
    fun onBindingDied()
    fun onNullBinding()
}