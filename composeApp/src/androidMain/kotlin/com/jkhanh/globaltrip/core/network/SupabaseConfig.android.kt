package com.jkhanh.globaltrip.core.network

import com.jkhanh.globaltrip.BuildConfig

/**
 * Android implementation using BuildConfig
 */
actual fun getSupabaseConfig(): SupabaseConfig = SupabaseConfig(
    url = BuildConfig.SUPABASE_URL,
    anonKey = BuildConfig.SUPABASE_ANON_KEY
)