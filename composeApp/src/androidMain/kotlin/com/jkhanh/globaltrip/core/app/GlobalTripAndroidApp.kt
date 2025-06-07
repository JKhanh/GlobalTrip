package com.jkhanh.globaltrip.core.app

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import com.jkhanh.globaltrip.di.initKoin

/**
 * Android Application class for GlobalTrip
 */
class GlobalTripAndroidApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize application context
        appContext = applicationContext
        
        // Initialize Koin
        initKoin {
            androidContext(this@GlobalTripAndroidApp)
        }
    }
    
    companion object {
        /**
         * Global application context
         */
        lateinit var appContext: Context
            private set
    }
}
