package com.jkhanh.globaltrip

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.jkhanh.globaltrip.core.logging.Logger
import com.jkhanh.globaltrip.di.initKoin
import kotlinx.browser.document

private const val TAG = "Main"

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    Logger.initialize()
    initKoin()
    Logger.i("WASM Application started", TAG)
    
    ComposeViewport(document.body!!) {
        App()
    }
}