package com.jkhanh.globaltrip.core.app

import com.jkhanh.globaltrip.di.initKoin

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
        
        initKoin()
        
        isInitialized = true
    }
}
