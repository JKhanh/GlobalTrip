package com.jkhanh.globaltrip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.jkhanh.globaltrip.core.ui.theme.GlobalTripTheme
import com.jkhanh.globaltrip.di.AppModule
import com.jkhanh.globaltrip.navigation.AppNavHost

/**
 * Main application composable
 */
@Composable
fun GlobalTripApp() {
    // Get the theme settings from the SettingsViewModel
    val settingsViewModel = remember { AppModule.provideSettingsViewModel() }
    val currentTheme by settingsViewModel.themeOption.collectAsState()
    
    GlobalTripTheme(
        themeOption = currentTheme
    ) {
        AppNavHost(
            themeOption = currentTheme,
            onThemeSelected = { theme ->
                settingsViewModel.setThemeOption(theme)
            }
        )
    }
}
