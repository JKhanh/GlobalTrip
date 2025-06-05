package com.jkhanh.globaltrip.di

import com.jkhanh.globaltrip.core.data.storage.MockSecureStorage
import com.jkhanh.globaltrip.core.domain.repository.SecureStorage
import com.jkhanh.globaltrip.core.domain.repository.TripRepository
import com.jkhanh.globaltrip.core.data.repository.impl.MockTripRepository
import org.koin.dsl.module

/**
 * Core module for storage and basic services
 */
val coreModule = module {
    
    // Storage
    single<SecureStorage> { MockSecureStorage() }
    
    // Trip repository (for now using mock, will be replaced with SQLDelight)
    single<TripRepository> { MockTripRepository() }
    
}