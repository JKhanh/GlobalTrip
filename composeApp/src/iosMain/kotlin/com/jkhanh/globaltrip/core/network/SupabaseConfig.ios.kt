package com.jkhanh.globaltrip.core.network

import platform.Foundation.NSBundle
import platform.Foundation.valueForKey

/**
 * iOS implementation reading from Info.plist
 * Credentials should be set in Info.plist or through environment variables
 */
actual fun getSupabaseConfig(): SupabaseConfig {
    val bundle = NSBundle.mainBundle
    
    // Try to read from Info.plist first
    val supabaseUrl = bundle.valueForKey("SUPABASE_URL") as? String
    val supabaseAnonKey = bundle.valueForKey("SUPABASE_ANON_KEY") as? String
    
    // Validate that credentials are available
    if (supabaseUrl.isNullOrBlank() || supabaseAnonKey.isNullOrBlank()) {
        error(
            "Supabase credentials not found in Info.plist. " +
            "Please add SUPABASE_URL and SUPABASE_ANON_KEY to your Info.plist file."
        )
    }
    
    return SupabaseConfig(
        url = supabaseUrl,
        anonKey = supabaseAnonKey
    )
}