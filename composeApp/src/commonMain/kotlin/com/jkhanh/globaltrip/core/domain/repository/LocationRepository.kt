package com.jkhanh.globaltrip.core.domain.repository

import com.jkhanh.globaltrip.core.domain.model.Location
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Location operations
 */
interface LocationRepository {
    /**
     * Gets all favorite locations
     */
    fun getFavoriteLocations(): Flow<List<Location>>
    
    /**
     * Gets all locations for a trip
     */
    fun getLocationsForTrip(tripId: String): Flow<List<Location>>
    
    /**
     * Gets a location by ID
     */
    suspend fun getLocationById(id: String): Location?
    
    /**
     * Creates a new location
     */
    suspend fun createLocation(location: Location): String
    
    /**
     * Updates an existing location
     */
    suspend fun updateLocation(location: Location)
    
    /**
     * Toggles a location's favorite status
     */
    suspend fun toggleFavorite(id: String, isFavorite: Boolean)
    
    /**
     * Deletes a location
     */
    suspend fun deleteLocation(id: String)
}
