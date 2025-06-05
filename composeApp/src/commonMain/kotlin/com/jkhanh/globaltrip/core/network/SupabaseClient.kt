package com.jkhanh.globaltrip.core.network

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth

/**
 * Supabase client configuration for GlobalTrip app
 * 
 * Configuration is loaded from platform-specific sources:
 * - Android: BuildConfig (from local.properties)
 * - iOS: Bundle/Info.plist (fallback to hardcoded for now)
 * - WASM: Environment variables (fallback to hardcoded for now)
 */
object SupabaseClient {
    
    private val config by lazy { 
        val config = getSupabaseConfig()
        println("🔧 DEBUG: Supabase config loaded - URL: ${config.url.take(30)}..., Key length: ${config.anonKey.length}")
        config
    }
    
    val client: SupabaseClient by lazy {
        try {
            val client = createSupabaseClient(
                supabaseUrl = config.url,
                supabaseKey = config.anonKey
            ) {
                install(Auth) {
                    // Auth configuration will be added here
                }
            }
            println("🔧 DEBUG: Supabase client created successfully")
            client
        } catch (e: Exception) {
            println("🔧 DEBUG: Failed to create Supabase client: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
    
    /**
     * Access to auth module
     */
    val auth: Auth
        get() = client.auth
}