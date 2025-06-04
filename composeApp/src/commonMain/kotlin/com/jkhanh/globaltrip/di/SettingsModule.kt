package com.jkhanh.globaltrip.di

import com.jkhanh.globaltrip.feature.settings.data.InMemorySettingsRepository
import com.jkhanh.globaltrip.feature.settings.data.SettingsRepository
import com.jkhanh.globaltrip.feature.settings.presentation.SettingsViewModel
import org.koin.dsl.module

/**
 * Settings module for app settings and preferences
 */
val settingsModule = module {
    
    // Settings repository
    single<SettingsRepository> { InMemorySettingsRepository() }
    
    // Settings ViewModel
    factory { SettingsViewModel(settingsRepository = get()) }
    
}