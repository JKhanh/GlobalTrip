package com.jkhanh.globaltrip.di

import com.jkhanh.globaltrip.feature.trips.domain.usecase.CreateTripUseCase
import com.jkhanh.globaltrip.feature.trips.domain.usecase.GetTripsUseCase
import com.jkhanh.globaltrip.feature.trips.presentation.TripCreateViewModel
import com.jkhanh.globaltrip.feature.trips.presentation.TripListViewModel
import org.koin.dsl.module

/**
 * Trips module for trip-related features
 */
val tripsModule = module {
    
    // Trip use cases
    single { GetTripsUseCase(tripRepository = get()) }
    single { CreateTripUseCase(tripRepository = get()) }
    
    // Trip ViewModels
    factory { TripListViewModel(getTripsUseCase = get()) }
    factory { TripCreateViewModel(createTripUseCase = get()) }
    
}