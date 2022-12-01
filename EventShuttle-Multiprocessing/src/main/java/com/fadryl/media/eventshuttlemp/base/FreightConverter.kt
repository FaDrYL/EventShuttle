package com.fadryl.media.eventshuttlemp.base

import android.os.Parcelable

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/11/30
 */
interface FreightConverter {
    fun convert(className: String, parcelable: Parcelable): Parcelable?
}