package com.jkhanh.globaltrip.di

import com.jkhanh.globaltrip.core.data.storage.MockSecureStorage
import com.jkhanh.globaltrip.core.domain.repository.SecureStorage
import org.koin.dsl.module

/**
 * Core module for storage and basic services
 */
val coreModule = module {
    
    // Storage
    single<SecureStorage> { MockSecureStorage() }
    
}