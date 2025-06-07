package com.jkhanh.globaltrip.di

import com.jkhanh.globaltrip.core.data.repository.impl.MockTripRepository
import com.jkhanh.globaltrip.core.data.repository.impl.SqlDelightTripRepository
import com.jkhanh.globaltrip.core.data.storage.MockSecureStorage
import com.jkhanh.globaltrip.core.database.DatabaseProvider
import com.jkhanh.globaltrip.core.domain.repository.SecureStorage
import com.jkhanh.globaltrip.core.domain.repository.TripRepository
import org.koin.dsl.module

/**
 * Core module for storage and basic services
 */
val coreModule = module {
    
    // Storage
    single<SecureStorage> { MockSecureStorage() }

     single<TripRepository> { SqlDelightTripRepository(get<DatabaseProvider>().database) }
    
}