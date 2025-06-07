package com.jkhanh.globaltrip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.jkhanh.globaltrip.core.ui.theme.GlobalTripTheme
import com.jkhanh.globaltrip.feature.settings.presentation.SettingsViewModel
import com.jkhanh.globaltrip.navigation.AppNavHost
import org.koin.compose.koinInject

/**
 * Main application composable
 */
@Composable
fun GlobalTripApp() {
    // Get the theme settings from the SettingsViewModel using Koin
    val settingsViewModel: SettingsViewModel = koinInject()
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
