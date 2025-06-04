package com.jkhanh.globaltrip

import android.app.Application
import com.jkhanh.globaltrip.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class GlobalTripApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        initKoin {
            androidLogger()
            androidContext(this@GlobalTripApplication)
        }
    }
}