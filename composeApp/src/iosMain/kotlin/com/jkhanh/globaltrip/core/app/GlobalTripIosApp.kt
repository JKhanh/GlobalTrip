package com.jkhanh.globaltrip.core.app

import com.jkhanh.globaltrip.di.databaseModule
import com.jkhanh.globaltrip.di.platformModules
import com.jkhanh.globaltrip.di.settingsModule
import com.jkhanh.globaltrip.di.tripModule
import org.koin.core.context.startKoin

/**
 * iOS application initialization class for GlobalTrip
 */
object GlobalTripIosApp {
    /**
     * Initialize the application's dependencies
     */
    fun initialize() {
        // Initialize Koin
        startKoin {
            modules(databaseModule + tripModule + settingsModule + platformModules)
        }
    }
}

/**
 * Top-level function to initialize the app from Swift
 */
fun doInitialize() {
    GlobalTripIosApp.initialize()
}