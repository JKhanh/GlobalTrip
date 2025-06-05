package com.jkhanh.globaltrip.di

import com.jkhanh.globaltrip.core.network.SupabaseClient
import io.github.jan.supabase.SupabaseClient as SupabaseClientType
import org.koin.dsl.module

/**
 * Network module for Supabase and HTTP clients
 */
val networkModule = module {
    
    // Supabase client (singleton)
    single<SupabaseClientType> { SupabaseClient.client }
    
}