package com.jkhanh.globaltrip.core.network

import com.jkhanh.globaltrip.core.logging.Logger
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
    private const val TAG = "SupabaseClient"
    
    private val config by lazy { 
        val config = getSupabaseConfig()
        Logger.d("Supabase config loaded - URL: ${config.url.take(30)}..., Key length: ${config.anonKey.length}", TAG)
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
            Logger.i("Supabase client created successfully", TAG)
            client
        } catch (e: Exception) {
            Logger.e("Failed to create Supabase client", TAG, e)
            throw e
        }
    }
    
    /**
     * Access to auth module
     */
    val auth: Auth
        get() = client.auth
}