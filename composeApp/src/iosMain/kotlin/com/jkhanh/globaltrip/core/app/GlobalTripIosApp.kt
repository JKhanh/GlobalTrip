package com.jkhanh.globaltrip.core.app

import com.jkhanh.globaltrip.di.initKoin

/**
 * iOS application initialization class for GlobalTrip
 */
object GlobalTripIosApp {
    /**
     * Initialize the application's dependencies
     */
    fun initialize() {
        initKoin()
    }
}

/**
 * Top-level function to initialize the app from Swift
 */
fun doInitialize() {
    GlobalTripIosApp.initialize()
}