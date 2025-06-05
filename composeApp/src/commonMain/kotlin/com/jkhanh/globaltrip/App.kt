package com.jkhanh.globaltrip

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview  
fun App() {
    KoinContext {
        GlobalTripApp()
    }
}
