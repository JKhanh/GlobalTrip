package com.jkhanh.globaltrip

import androidx.compose.ui.window.ComposeUIViewController
import com.jkhanh.globaltrip.core.logging.Logger
import com.jkhanh.globaltrip.di.initKoin

private const val TAG = "MainViewController"

fun MainViewController() = ComposeUIViewController { 
    Logger.initialize()
    initKoin()
    Logger.i("iOS Application started", TAG)
    App() 
}