package com.jkhanh.globaltrip.core.logging

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

/**
 * Centralized logging utility for GlobalTrip
 * Wrapper around Napier for consistent logging across all platforms
 */
object Logger {
    
    private const val TAG = "Logger"
    
    /**
     * Initialize logging for all platforms
     * Should be called early in application lifecycle
     */
    fun initialize() {
        // Use DebugAntilog for all platforms:
        // - Android: outputs to android.util.Log
        // - iOS: outputs to print() function (visible in Xcode console)
        // - WASM/JS: outputs to console.log (visible in browser dev tools)
        Napier.base(DebugAntilog())
        
        Napier.i("GlobalTrip Logger initialized", tag = TAG)
    }
    
    // Verbose logs - detailed information for debugging
    fun v(message: String, tag: String? = null, throwable: Throwable? = null) {
        Napier.v(message, throwable, tag ?: "GlobalTrip")
    }
    
    // Debug logs - information useful for debugging
    fun d(message: String, tag: String? = null, throwable: Throwable? = null) {
        Napier.d(message, throwable, tag ?: "GlobalTrip")
    }
    
    // Info logs - general information
    fun i(message: String, tag: String? = null, throwable: Throwable? = null) {
        Napier.i(message, throwable, tag ?: "GlobalTrip")
    }
    
    // Warning logs - something unexpected but not critical
    fun w(message: String, tag: String? = null, throwable: Throwable? = null) {
        Napier.w(message, throwable, tag ?: "GlobalTrip")
    }
    
    // Error logs - errors that might cause issues
    fun e(message: String, tag: String? = null, throwable: Throwable? = null) {
        Napier.e(message, throwable, tag ?: "GlobalTrip")
    }
}