package com.jkhanh.globaltrip

import android.app.Application
import com.jkhanh.globaltrip.core.logging.Logger
import com.jkhanh.globaltrip.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class GlobalTripApplication : Application() {
    
    companion object {
        private const val TAG = "GlobalTripApplication"
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize logging first
        Logger.initialize()
        
        initKoin {
            androidLogger()
            androidContext(this@GlobalTripApplication)
        }
        
        Logger.i("Android Application started", TAG)
    }
}