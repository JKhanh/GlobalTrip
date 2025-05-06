package com.jkhanh.globaltrip.core.data.repository.impl

import com.jkhanh.globaltrip.core.domain.model.Trip
import com.jkhanh.globaltrip.core.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random
import kotlin.time.Duration.Companion.days

/**
 * Mock implementation of TripRepository for development
 */
class MockTripRepository : TripRepository {
    
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    
    init {
        // Add some mock data
        val mockTrips = generateMockTrips()
        _trips.value = mockTrips
    }
    
    override fun getTrips(): Flow<List<Trip>> {
        return _trips.asStateFlow()
    }
    
    override suspend fun getTripById(id: String): Trip? {
        return _trips.value.find { it.id == id }
    }
    
    override suspend fun createTrip(trip: Trip): String {
        val newId = Random.nextInt(1000, 9999).toString()
        val newTrip = trip.copy(id = newId)
        _trips.update { currentTrips ->
            currentTrips + newTrip
        }
        return newId
    }
    
    override suspend fun updateTrip(trip: Trip) {
        _trips.update { currentTrips ->
            currentTrips.map {
                if (it.id == trip.id) trip else it
            }
        }
    }
    
    override suspend fun deleteTrip(id: String) {
        _trips.update { currentTrips ->
            currentTrips.filter { it.id != id }
        }
    }
    
    override suspend fun archiveTrip(id: String) {
        _trips.update { currentTrips ->
            currentTrips.map {
                if (it.id == id) it.copy(isArchived = true) else it
            }
        }
    }
    
    override suspend fun unarchiveTrip(id: String) {
        _trips.update { currentTrips ->
            currentTrips.map {
                if (it.id == id) it.copy(isArchived = false) else it
            }
        }
    }
    
    /**
     * Generate mock trip data for development
     */
    private fun generateMockTrips(): List<Trip> {
        val now = Clock.System.now()
        val currentDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        return listOf(
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
                createdAt = now.minus(180.days),
                updatedAt = now.minus(160.days),
                ownerId = "user1"
            )
        )
    }
}
