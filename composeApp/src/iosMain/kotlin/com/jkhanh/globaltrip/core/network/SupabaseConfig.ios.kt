package com.jkhanh.globaltrip.core.network

import platform.Foundation.NSBundle
import com.jkhanh.globaltrip.core.logging.Logger

private const val TAG = "SupabaseConfig"

/**
 * iOS implementation reading from Info.plist
 * Credentials should be set in Info.plist or through environment variables
 */
actual fun getSupabaseConfig(): SupabaseConfig {
    val bundle = NSBundle.mainBundle
    
    Logger.d("Attempting to read Supabase config from iOS bundle", TAG)
    
    // Try to read from Info.plist using objectForInfoDictionaryKey
    val supabaseUrl = try {
        bundle.objectForInfoDictionaryKey("SUPABASE_URL") as? String
    } catch (e: Exception) {
        Logger.w("Failed to read SUPABASE_URL from bundle: ${e.message}", TAG)
        null
    }
    
    val supabaseAnonKey = try {
        bundle.objectForInfoDictionaryKey("SUPABASE_ANON_KEY") as? String
    } catch (e: Exception) {
        Logger.w("Failed to read SUPABASE_ANON_KEY from bundle: ${e.message}", TAG)
        null
    }
    
    Logger.d("Read config - URL: '${if (supabaseUrl.isNullOrBlank()) "empty" else "present"}', Key: '${if (supabaseAnonKey.isNullOrBlank()) "empty" else "present"}'", TAG)
    
    // Validate that credentials are available
    if (supabaseUrl.isNullOrBlank() || supabaseAnonKey.isNullOrBlank()) {
        error(
            "Supabase credentials not found in Info.plist. " +
            "Please add SUPABASE_URL and SUPABASE_ANON_KEY to your Info.plist file. " +
            "Found URL: '$supabaseUrl', Key: '${if (supabaseAnonKey.isNullOrBlank()) "empty" else "present"}'"
        )
    }
    
    Logger.i("Successfully loaded Supabase configuration", TAG)
    
    return SupabaseConfig(
        url = supabaseUrl,
        anonKey = supabaseAnonKey
    )
}