package com.jkhanh.globaltrip.feature.settings.presentation

import androidx.lifecycle.ViewModel
import com.jkhanh.globaltrip.core.ui.theme.GlobalTripThemeOption
import com.jkhanh.globaltrip.feature.settings.data.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    val themeOption: StateFlow<GlobalTripThemeOption> = settingsRepository.themeOption
    
    fun setThemeOption(theme: GlobalTripThemeOption) {
        settingsRepository.setThemeOption(theme)
    }
}