package com.jkhanh.globaltrip.core.data.repository.impl

import com.jkhanh.globaltrip.core.domain.model.Trip
import com.jkhanh.globaltrip.core.domain.repository.TripRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

/**
 * A WASM JS-specific implementation of TripRepository that fetches data from a remote API.
 * 
 * Note: This is a mock implementation that simulates API calls with fake data.
 * In a real application, this would make actual API calls to a backend service.
 */
class RemoteApiTripRepository : TripRepository {
    
    // In-memory cache of trips for the current session
    private val cachedTrips = mutableListOf<Trip>()
    
    init {
        // Initialize with some mock data
        val now = Clock.System.now()
        val currentDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        // Add mock data
        cachedTrips.addAll(
            listOf(
                Trip(
                    id = "1",
                    title = "Summer Vacation in Italy",
                    description = "Exploring Rome, Florence and Venice",
                    startDate = LocalDate(currentDate.year, 7, 15),
                    endDate = LocalDate(currentDate.year, 7, 25),
                    destination = "Italy",
                    coverImageUrl = null,
                    isArchived = false,
                    createdAt = now,
                    updatedAt = now,
                    ownerId = "user1"
                ),
                Trip(
                    id = "2",
                    title = "Business Trip to New York",
                    description = "Conference and client meetings",
                    startDate = LocalDate(currentDate.year, currentDate.monthNumber + 1, 10),
                    endDate = LocalDate(currentDate.year, currentDate.monthNumber + 1, 15),
                    destination = "New York, USA",
                    coverImageUrl = null,
                    isArchived = false,
                    createdAt = now,
                    updatedAt = now,
                    ownerId = "user1"
                ),
                Trip(
                    id = "3",
                    title = "Weekend in Paris",
                    description = "Short getaway to Paris",
                    startDate = LocalDate(currentDate.year - 1, 11, 25),
                    endDate = LocalDate(currentDate.year - 1, 11, 27),
                    destination = "Paris, France",
                    coverImageUrl = null,
                    isArchived = true,
                    createdAt = now.minus(kotlinx.datetime.DateTimePeriod(months = 6)),
                    updatedAt = now.minus(kotlinx.datetime.DateTimePeriod(months = 5)),
                    ownerId = "user1"
                )
            )
        )
    }
    
    override fun getTrips(): Flow<List<Trip>> = flow {
        // Simulate network delay
        delay(500)
        
        // Emit cached trips (in a real app, this would make an API call)
        emit(cachedTrips.toList())
    }
    
    override suspend fun getTripById(id: String): Trip? {
        // Simulate network delay
        delay(300)
        
        // Return the trip with the specified ID
        return cachedTrips.find { it.id == id }
    }
    
    override suspend fun createTrip(trip: Trip): String {
        // Simulate network delay
        delay(700)
        
        // In a real app, this would make a POST request to create the trip
        // For this mock implementation, just add it to our cache
        val newTrip = trip.copy(
            id = (cachedTrips.size + 1).toString(),
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        cachedTrips.add(newTrip)
        
        return newTrip.id
    }
    
    override suspend fun updateTrip(trip: Trip) {
        // Simulate network delay
        delay(700)
        
        // In a real app, this would make a PUT/PATCH request to update the trip
        // For this mock implementation, just update it in our cache
        val index = cachedTrips.indexOfFirst { it.id == trip.id }
        if (index != -1) {
            cachedTrips[index] = trip.copy(updatedAt = Clock.System.now())
        }
    }
    
    override suspend fun deleteTrip(id: String) {
        // Simulate network delay
        delay(500)
        
        // In a real app, this would make a DELETE request
        // For this mock implementation, just remove it from our cache
        cachedTrips.removeIf { it.id == id }
    }
    
    override suspend fun archiveTrip(id: String) {
        // Simulate network delay
        delay(500)
        
        // In a real app, this would make a PATCH request to archive the trip
        // For this mock implementation, just update it in our cache
        val index = cachedTrips.indexOfFirst { it.id == id }
        if (index != -1) {
            cachedTrips[index] = cachedTrips[index].copy(
                isArchived = true,
                updatedAt = Clock.System.now()
            )
        }
    }
    
    override suspend fun unarchiveTrip(id: String) {
        // Simulate network delay
        delay(500)
        
        // In a real app, this would make a PATCH request to unarchive the trip
        // For this mock implementation, just update it in our cache
        val index = cachedTrips.indexOfFirst { it.id == id }
        if (index != -1) {
            cachedTrips[index] = cachedTrips[index].copy(
                isArchived = false,
                updatedAt = Clock.System.now()
            )
        }
    }
}
