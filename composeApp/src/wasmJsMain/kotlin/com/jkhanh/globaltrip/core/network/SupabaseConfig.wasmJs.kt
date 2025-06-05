package com.jkhanh.globaltrip.core.network

/**
 * WASM JS implementation - requires runtime configuration
 * Credentials must be provided by the hosting web application
 */
actual fun getSupabaseConfig(): SupabaseConfig {
    // For WASM, credentials must be injected at runtime by the hosting page
    // This implementation requires the hosting application to call setSupabaseConfig()
    
    error(
        "Supabase credentials not configured for WASM target. " +
        "The hosting web application must provide credentials at runtime. " +
        "This prevents hardcoded credentials in the WASM bundle."
    )
}