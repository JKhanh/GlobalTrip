package com.jkhanh.globaltrip.feature.settings.data

import com.jkhanh.globaltrip.core.ui.theme.GlobalTripThemeOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for app settings
 */
interface SettingsRepository {
    val themeOption: StateFlow<GlobalTripThemeOption>
    
    fun setThemeOption(theme: GlobalTripThemeOption)
}

/**
 * In-memory implementation of SettingsRepository
 * In a real app, this would use a platform-specific preferences API
 */
class InMemorySettingsRepository : SettingsRepository {
    private val _themeOption = MutableStateFlow(GlobalTripThemeOption.DEFAULT)
    override val themeOption = _themeOption.asStateFlow()
    
    override fun setThemeOption(theme: GlobalTripThemeOption) {
        _themeOption.value = theme
    }
}