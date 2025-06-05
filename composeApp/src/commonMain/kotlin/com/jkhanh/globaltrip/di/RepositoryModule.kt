package com.jkhanh.globaltrip.di

import com.jkhanh.globaltrip.core.domain.repository.AuthRepository
import com.jkhanh.globaltrip.core.data.repository.impl.SupabaseAuthRepository
import org.koin.dsl.module

/**
 * Repository module for data access layer
 */
val repositoryModule = module {
    
    // Auth repository
    single<AuthRepository> { 
        SupabaseAuthRepository(
            supabaseClient = get(),
            secureStorage = get()
        )
    }
    
}