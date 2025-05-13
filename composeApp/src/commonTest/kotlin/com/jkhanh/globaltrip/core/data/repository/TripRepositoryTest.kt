package com.jkhanh.globaltrip.core.data.repository

import com.jkhanh.globaltrip.core.domain.model.Trip
import com.jkhanh.globaltrip.core.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Test for TripRepository using a mock implementation
 * 
 * This test uses a mock instead of actual SQLDelight to be compatible
 * with all platforms (Android, iOS, and WasmJS)
 */
class TripRepositoryTest {
    
    @Test
    fun createTrip_shouldAddTripToRepository() = runTest {
        // Given a mock repository and a new trip
        val repository = MockTripRepository()
        val trip = createTestTrip()
        
        // When creating the trip
        val tripId = repository.createTrip(trip)
        
        // Then the trip should be stored in the repository
        val storedTrip = repository.getTripById(tripId)
        assertNotNull(storedTrip)
        assertEquals(trip.title, storedTrip.title)
        assertEquals(trip.destination, storedTrip.destination)
    }
    
    @Test
    fun getTrips_shouldReturnAllTrips() = runTest {
        // Given multiple trips in the repository
        val repository = MockTripRepository()
        val trip1 = createTestTrip(id = "1", title = "Trip 1")
        val trip2 = createTestTrip(id = "2", title = "Trip 2")
        
        repository.createTrip(trip1)
        repository.createTrip(trip2)
        
        // When getting all trips
        val trips = repository.getTrips().first()
        
        // Then all trips should be returned
        assertEquals(2, trips.size)
        assertTrue(trips.any { it.id == "1" && it.title == "Trip 1" })
        assertTrue(trips.any { it.id == "2" && it.title == "Trip 2" })
    }
    
    @Test
    fun deleteTrip_shouldRemoveTripFromRepository() = runTest {
        // Given a trip in the repository
        val repository = MockTripRepository()
        val trip = createTestTrip()
        repository.createTrip(trip)
        
        // When deleting the trip
        repository.deleteTrip(trip.id)
        
        // Then the trip should be removed
        val deletedTrip = repository.getTripById(trip.id)
        assertNull(deletedTrip)
    }
    
    @Test
    fun archiveTrip_shouldSetIsArchivedToTrue() = runTest {
        // Given a trip in the repository
        val repository = MockTripRepository()
        val trip = createTestTrip(isArchived = false)
        repository.createTrip(trip)
        
        // When archiving the trip
        repository.archiveTrip(trip.id)
        
        // Then the trip should be archived
        val archivedTrip = repository.getTripById(trip.id)
        assertNotNull(archivedTrip)
        assertTrue(archivedTrip.isArchived)
    }
    
    @Test
    fun unarchiveTrip_shouldSetIsArchivedToFalse() = runTest {
        // Given an archived trip in the repository
        val repository = MockTripRepository()
        val trip = createTestTrip(isArchived = true)
        repository.createTrip(trip)
        
        // When unarchiving the trip
        repository.unarchiveTrip(trip.id)
        
        // Then the trip should be unarchived
        val unarchivedTrip = repository.getTripById(trip.id)
        assertNotNull(unarchivedTrip)
        assertFalse(unarchivedTrip.isArchived)
    }
    
    @Test
    fun updateTrip_shouldUpdateTripInRepository() = runTest {
        // Given a trip in the repository
        val repository = MockTripRepository()
        val trip = createTestTrip()
        repository.createTrip(trip)
        
        // When updating the trip
        val updatedTrip = trip.copy(
            title = "Updated Title",
            description = "Updated Description",
            destination = "Updated Destination"
        )
        repository.updateTrip(updatedTrip)
        
        // Then the trip should be updated in the repository
        val retrievedTrip = repository.getTripById(trip.id)
        assertNotNull(retrievedTrip)
        assertEquals("Updated Title", retrievedTrip.title)
        assertEquals("Updated Description", retrievedTrip.description)
        assertEquals("Updated Destination", retrievedTrip.destination)
    }
    
    // Helper method to create a test trip
    private fun createTestTrip(
        id: String = "test-id",
        title: String = "Test Trip",
        description: String = "Test Description",
        isArchived: Boolean = false
    ): Trip {
        val now = Clock.System.now()
        return Trip(
            id = id,
            title = title,
            description = description,
            startDate = LocalDate(2024, 6, 1),
            endDate = LocalDate(2024, 6, 10),
            destination = "Test Destination",
            coverImageUrl = null,
            isArchived = isArchived,
            createdAt = now,
            updatedAt = now,
            ownerId = "test-user"
        )
    }
    
    /**
     * Simple in-memory mock implementation of TripRepository for testing
     * This avoids using SQLDelight in tests, making it compatible with all platforms
     */
    private class MockTripRepository : TripRepository {
        private val trips = MutableStateFlow<Map<String, Trip>>(emptyMap())
        
        override fun getTrips(): Flow<List<Trip>> = MutableStateFlow(trips.value.values.toList())
        
        override suspend fun getTripById(id: String): Trip? = trips.value[id]
        
        override suspend fun createTrip(trip: Trip): String {
            val id = trip.id.ifEmpty { "generated-${trips.value.size + 1}" }
            val newTrip = trip.copy(id = id)
            trips.update { it + (id to newTrip) }
            return id
        }
        
        override suspend fun updateTrip(trip: Trip) {
            trips.update { it + (trip.id to trip) }
        }
        
        override suspend fun deleteTrip(id: String) {
            trips.update { it - id }
        }
        
        override suspend fun archiveTrip(id: String) {
            trips.value[id]?.let { trip ->
                trips.update { it + (id to trip.copy(isArchived = true)) }
            }
        }
        
        override suspend fun unarchiveTrip(id: String) {
            trips.value[id]?.let { trip ->
                trips.update { it + (id to trip.copy(isArchived = false)) }
            }
        }
    }
}
