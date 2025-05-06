package com.jkhanh.globaltrip.di

import com.jkhanh.globaltrip.core.data.repository.impl.MockTripRepository
import com.jkhanh.globaltrip.core.domain.repository.TripRepository
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
}
