package com.jkhanh.globaltrip.feature.trips.domain.usecase

import com.jkhanh.globaltrip.core.domain.model.Trip
import com.jkhanh.globaltrip.core.domain.repository.TripRepository

/**
 * Use case for getting a trip by its ID
 */
class GetTripByIdUseCase(
    private val tripRepository: TripRepository
) {
    
    /**
     * Get a trip by ID
     */
    suspend operator fun invoke(tripId: String): Trip? {
        return tripRepository.getTripById(tripId)
    }
}