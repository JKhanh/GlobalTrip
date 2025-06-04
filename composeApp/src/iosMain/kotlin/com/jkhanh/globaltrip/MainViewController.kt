package com.jkhanh.globaltrip

import androidx.compose.ui.window.ComposeUIViewController
import com.jkhanh.globaltrip.di.initKoin

fun MainViewController() = ComposeUIViewController { 
    initKoin()
    App() 
}