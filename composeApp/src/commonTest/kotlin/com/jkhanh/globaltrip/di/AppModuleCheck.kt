package com.jkhanh.globaltrip.di

import kotlin.test.Test
import org.koin.test.verify.verify

class AppModuleCheck {

    @Test
    fun checkKoinModule() {
        // Verify Koin configuration
        globalTripAppModule.verify()
    }
}