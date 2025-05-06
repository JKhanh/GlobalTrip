package com.jkhanh.globaltrip.feature.trips.domain.usecase

import com.jkhanh.globaltrip.core.domain.model.Trip
import com.jkhanh.globaltrip.core.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to get all trips for the current user
 */
class GetTripsUseCase(private val tripRepository: TripRepository) {
    
    /**
     * Gets all trips for the current user
     */
    operator fun invoke(): Flow<List<Trip>> {
        return tripRepository.getTrips()
    }
}
