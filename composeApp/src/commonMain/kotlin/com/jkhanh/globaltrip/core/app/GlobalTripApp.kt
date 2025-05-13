package com.jkhanh.globaltrip.core.app

import com.jkhanh.globaltrip.di.databaseModule
import com.jkhanh.globaltrip.di.platformModules
import com.jkhanh.globaltrip.di.tripModule
import org.koin.core.context.startKoin

/**
 * Initialize the GlobalTrip application
 */
object GlobalTripApp {
    
    private var isInitialized = false
    
    /**
     * Initialize the application components
     */
    fun initialize() {
        if (isInitialized) return
        
        initializeDependencyInjection()
        
        isInitialized = true
    }
    
    /**
     * Initialize Koin dependency injection
     */
    private fun initializeDependencyInjection() {
        startKoin {
            modules(databaseModule + tripModule + platformModules)
        }
    }
}
