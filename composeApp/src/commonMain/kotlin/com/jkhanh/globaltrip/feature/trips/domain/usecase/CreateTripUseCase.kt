package com.jkhanh.globaltrip.feature.trips.domain.usecase

import com.jkhanh.globaltrip.core.domain.model.Trip
import com.jkhanh.globaltrip.core.domain.repository.TripRepository

/**
 * Use case to create a new trip
 */
class CreateTripUseCase(private val tripRepository: TripRepository) {
    
    /**
     * Creates a new trip
     * 
     * @param trip The trip to create
     * @return The ID of the created trip
     */
    suspend operator fun invoke(trip: Trip): String {
        return tripRepository.createTrip(trip)
    }
}
