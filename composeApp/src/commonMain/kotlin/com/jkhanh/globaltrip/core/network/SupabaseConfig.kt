package com.jkhanh.globaltrip.core.network

/**
 * Configuration data for Supabase client
 */
data class SupabaseConfig(
    val url: String,
    val anonKey: String
)

/**
 * Platform-specific configuration provider
 * Implementations read from platform-specific sources (BuildConfig, Bundle, etc.)
 */
expect fun getSupabaseConfig(): SupabaseConfig