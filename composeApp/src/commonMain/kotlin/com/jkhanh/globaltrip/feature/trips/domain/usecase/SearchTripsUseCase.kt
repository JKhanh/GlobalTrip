package com.jkhanh.globaltrip.feature.trips.domain.usecase

import com.jkhanh.globaltrip.core.domain.model.Trip
import com.jkhanh.globaltrip.core.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for searching trips by name and destination
 */
class SearchTripsUseCase(
    private val tripRepository: TripRepository
) {
    
    /**
     * Search trips by query string in title and destination
     * @param query The search query string
     * @return Flow of filtered trips
     */
    operator fun invoke(query: String): Flow<List<Trip>> {
        return tripRepository.getTrips().map { trips ->
            if (query.isBlank()) {
                trips
            } else {
                trips.filter { trip ->
                    trip.title.contains(query, ignoreCase = true) ||
                    trip.destination.contains(query, ignoreCase = true)
                }
            }
        }
    }
}