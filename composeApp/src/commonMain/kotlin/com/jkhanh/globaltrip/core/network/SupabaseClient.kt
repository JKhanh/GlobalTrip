package com.jkhanh.globaltrip.core.network

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth

/**
 * Supabase client configuration for GlobalTrip app
 * 
 * Configuration is loaded from local.properties or environment variables
 * - Set SUPABASE_URL and SUPABASE_ANON_KEY in local.properties
 * - Or copy gradle.properties.template to gradle.properties with your values
 */
object SupabaseClient {
    
    // TODO: Read from BuildConfig or local.properties in production
    // For now using placeholder values - update local.properties with real values
    private const val SUPABASE_URL = "your_supabase_url_here"
    private const val SUPABASE_ANON_KEY = "your_supabase_anon_key_here"
    
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Auth) {
                // Auth configuration will be added here
            }
        }
    }
    
    /**
     * Access to auth module
     */
    val auth: Auth
        get() = client.auth
}