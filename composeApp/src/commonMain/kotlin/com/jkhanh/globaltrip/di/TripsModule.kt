package com.jkhanh.globaltrip.di

import com.jkhanh.globaltrip.feature.trips.domain.usecase.CreateTripUseCase
import com.jkhanh.globaltrip.feature.trips.domain.usecase.GetTripByIdUseCase
import com.jkhanh.globaltrip.feature.trips.domain.usecase.GetTripsUseCase
import com.jkhanh.globaltrip.feature.trips.domain.usecase.SearchTripsUseCase
import com.jkhanh.globaltrip.feature.trips.domain.usecase.ValidateTripUseCase
import com.jkhanh.globaltrip.feature.trips.presentation.TripCreateViewModel
import com.jkhanh.globaltrip.feature.trips.presentation.TripDetailViewModel
import com.jkhanh.globaltrip.feature.trips.presentation.TripListViewModel
import org.koin.dsl.module

/**
 * Trips module for trip-related features
 */
val tripsModule = module {
    
    // Trip use cases
    single { GetTripsUseCase(tripRepository = get()) }
    single { GetTripByIdUseCase(tripRepository = get()) }
    single { SearchTripsUseCase(tripRepository = get()) }
    single { CreateTripUseCase(tripRepository = get()) }
    single { ValidateTripUseCase() }
    
    // Trip ViewModels
    factory { TripListViewModel(getTripsUseCase = get(), searchTripsUseCase = get()) }
    factory { TripDetailViewModel(getTripByIdUseCase = get()) }
    factory { TripCreateViewModel(createTripUseCase = get(), validateTripUseCase = get()) }
    
}