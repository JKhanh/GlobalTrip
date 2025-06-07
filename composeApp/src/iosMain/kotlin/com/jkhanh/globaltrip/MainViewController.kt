package com.jkhanh.globaltrip

import androidx.compose.ui.window.ComposeUIViewController
import com.jkhanh.globaltrip.core.logging.Logger
import com.jkhanh.globaltrip.di.initKoin

private const val TAG = "MainViewController"

// Initialize once when the module is loaded
private val isInitialized by lazy {
    Logger.initialize()
    initKoin()
    Logger.i("iOS Application initialized", TAG)
    true
}

fun MainViewController() = ComposeUIViewController { 
    // Ensure initialization happens only once
    isInitialized
    App() 
}