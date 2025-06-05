package com.jkhanh.globaltrip.di

import com.jkhanh.globaltrip.core.data.repository.impl.SqlDelightTripRepository
import com.jkhanh.globaltrip.core.database.DatabaseProvider
import com.jkhanh.globaltrip.core.domain.repository.TripRepository
import com.jkhanh.globaltrip.feature.trips.domain.usecase.CreateTripUseCase
import com.jkhanh.globaltrip.feature.trips.domain.usecase.GetTripByIdUseCase
import com.jkhanh.globaltrip.feature.trips.domain.usecase.GetTripsUseCase
import com.jkhanh.globaltrip.feature.trips.presentation.TripCreateViewModel
import com.jkhanh.globaltrip.feature.trips.presentation.TripDetailViewModel
import com.jkhanh.globaltrip.feature.trips.presentation.TripListViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Database-related dependencies
 */
val databaseModule = module {
    single<TripRepository> { SqlDelightTripRepository(get<DatabaseProvider>().database) }
}


/**
 * Trip feature-related dependencies
 */
val tripModule = module {
    factory { GetTripsUseCase(get()) }
    factory { GetTripByIdUseCase(get()) }
    factory { CreateTripUseCase(get()) }
    factory { TripListViewModel(get()) }
    factory { TripDetailViewModel(get()) }
    factory { TripCreateViewModel(get()) }
}

/**
 * Get platform-specific modules
 */
expect val platformModules: List<Module>
