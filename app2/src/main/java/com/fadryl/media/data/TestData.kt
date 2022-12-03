package com.fadryl.media.data

import android.os.Parcelable

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/11/30
 */
@kotlinx.parcelize.Parcelize
data class TestData(val num: Int): Parcelable

@kotlinx.parcelize.Parcelize
data class TimeData(val time: Long): Parcelable
