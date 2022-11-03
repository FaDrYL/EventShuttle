package com.fadryl.media.eventshuttle

import com.fadryl.media.eventshuttleanno.EventStop

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/10/24
 */
typealias EventCallable = () -> Unit

internal typealias EventMap = Map<String, ArrayList<EventStop>>
