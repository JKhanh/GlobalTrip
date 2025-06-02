package com.jkhanh.globaltrip.di

import com.jkhanh.globaltrip.core.data.repository.impl.MockTripRepository
import com.jkhanh.globaltrip.core.domain.repository.TripRepository
import com.jkhanh.globaltrip.feature.auth.di.AuthModule
import com.jkhanh.globaltrip.feature.auth.presentation.AuthViewModel
import com.jkhanh.globaltrip.feature.settings.data.InMemorySettingsRepository
import com.jkhanh.globaltrip.feature.settings.data.SettingsRepository
import com.jkhanh.globaltrip.feature.settings.presentation.SettingsViewModel
import com.jkhanh.globaltrip.feature.trips.domain.usecase.CreateTripUseCase
import com.jkhanh.globaltrip.feature.trips.domain.usecase.GetTripsUseCase
import com.jkhanh.globaltrip.feature.trips.presentation.TripCreateViewModel
import com.jkhanh.globaltrip.feature.trips.presentation.TripListViewModel

/**
 * Simple dependency injection container for the app
 */
object AppModule {
    
    // Repositories
    private val tripRepository: TripRepository by lazy { MockTripRepository() }
    private val settingsRepository: SettingsRepository by lazy { InMemorySettingsRepository() }
    
    // Use cases
    private val getTripsUseCase by lazy { GetTripsUseCase(tripRepository) }
    private val createTripUseCase by lazy { CreateTripUseCase(tripRepository) }
    
    // ViewModels
    fun provideTripListViewModel(): TripListViewModel {
        return TripListViewModel(getTripsUseCase)
    }
    
    fun provideTripCreateViewModel(): TripCreateViewModel {
        return TripCreateViewModel(createTripUseCase)
    }
    
    fun provideSettingsViewModel(): SettingsViewModel {
        return SettingsViewModel(settingsRepository)
    }
    
    fun provideAuthViewModel(): AuthViewModel {
        return AuthModule.provideAuthViewModel()
    }
    
    // Auth-related providers for other modules
    fun provideCurrentUserUseCase() = AuthModule.provideGetCurrentUserUseCase()
    fun provideObserveAuthStateUseCase() = AuthModule.provideObserveAuthStateUseCase()
}
