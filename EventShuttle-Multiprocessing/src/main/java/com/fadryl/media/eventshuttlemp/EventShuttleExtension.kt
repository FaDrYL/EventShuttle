package com.fadryl.media.eventshuttlemp

import com.fadryl.media.eventshuttle.EventShuttle
import com.fadryl.media.eventshuttlemp.base.FlightStrategy

/**
 * Created by Hoi Lung Lam (FaDr_YL) on 2022/12/03
 */

/**
 * register the FlightStrategy (Air Traffic Control)
 * to starting the journey of inter-Apps event distribution
 */
fun EventShuttle.registerFlightStrategy(flightStrategy: FlightStrategy) {
    FlightManager.init(flightStrategy)
}

/**
 * fire the event with multiprocessing feature 🛫.
 * Remember to registerFlightStrategy() before use.
 */
fun EventShuttle.departure(eventName: String) {
    departure(eventName, eventName)
}

/**
 * fire the event with multiprocessing feature 🛫.
 * Remember to registerFlightStrategy() before use.
 */
fun EventShuttle.departure(localEventName: String, remoteEventName: String) {
    FlightManager.departureEvent(remoteEventName)
    fire(localEventName)
}

/**
 * fire the event with multiprocessing feature 🛫.
 * Remember to registerFlightStrategy() before use.
 */
fun EventShuttle.departure(eventName: String, data: Any) {
    departure(eventName, eventName, data)
}

/**
 * fire the event with multiprocessing feature 🛫.
 * Remember to registerFlightStrategy() before use.
 */
fun EventShuttle.departure(localEventName: String, remoteEventName: String, data: Any) {
    FlightManager.departureEvent(remoteEventName, data)
    fire(localEventName, data)
}

/**
 * fire the event with multiprocessing feature 🛫.
 * Remember to registerFlightStrategy() before use.
 */
fun EventShuttle.departure(data: Any) {
    FlightManager.departureEvent(data)
    fire(data)
}