package com.jkhanh.globaltrip.di

import com.jkhanh.globaltrip.core.data.repository.impl.RemoteApiTripRepository
import com.jkhanh.globaltrip.core.database.DatabaseDriverFactory
import com.jkhanh.globaltrip.core.database.DatabaseProvider
import com.jkhanh.globaltrip.core.domain.repository.TripRepository
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Web/WASM-specific dependencies
 * 
 * For WASM JS, we're using a remote API repository instead of a local database.
 */
actual val platformModules: List<Module> = listOf(
    module {
        // Provide database driver factory and provider
        single { DatabaseDriverFactory() }
        single { DatabaseProvider(get()) }
        
        // Override the repository with the remote API implementation
        single<TripRepository> { RemoteApiTripRepository() }
    }
)
