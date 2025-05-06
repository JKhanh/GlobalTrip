package com.jkhanh.globaltrip.core.domain.repository

import com.jkhanh.globaltrip.core.domain.model.Trip
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Trip operations
 */
interface TripRepository {
    /**
     * Gets a stream of all trips for the current user
     */
    fun getTrips(): Flow<List<Trip>>
    
    /**
     * Gets a specific trip by ID
     */
    suspend fun getTripById(id: String): Trip?
    
    /**
     * Creates a new trip
     */
    suspend fun createTrip(trip: Trip): String
    
    /**
     * Updates an existing trip
     */
    suspend fun updateTrip(trip: Trip)
    
    /**
     * Deletes a trip by ID
     */
    suspend fun deleteTrip(id: String)
    
    /**
     * Archives a trip
     */
    suspend fun archiveTrip(id: String)
    
    /**
     * Unarchives a trip
     */
    suspend fun unarchiveTrip(id: String)
}
