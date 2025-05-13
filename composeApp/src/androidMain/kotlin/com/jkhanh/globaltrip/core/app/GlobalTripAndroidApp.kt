package com.jkhanh.globaltrip.core.app

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.jkhanh.globaltrip.di.databaseModule
import com.jkhanh.globaltrip.di.platformModules
import com.jkhanh.globaltrip.di.settingsModule
import com.jkhanh.globaltrip.di.tripModule

/**
 * Android Application class for GlobalTrip
 */
class GlobalTripAndroidApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize application context
        appContext = applicationContext
        
        // Initialize Koin
        startKoin {
            androidContext(this@GlobalTripAndroidApp)
            modules(databaseModule + tripModule + settingsModule + platformModules)
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
