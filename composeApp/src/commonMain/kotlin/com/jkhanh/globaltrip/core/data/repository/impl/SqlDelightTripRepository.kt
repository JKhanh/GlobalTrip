package com.jkhanh.globaltrip.core.data.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.jkhanh.globaltrip.core.database.GlobalTripDatabase
import com.jkhanh.globaltrip.core.database.TripModel
import com.jkhanh.globaltrip.core.domain.model.Trip
import com.jkhanh.globaltrip.core.domain.repository.TripRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * SQLDelight implementation of TripRepository
 */
class SqlDelightTripRepository(
    private val database: GlobalTripDatabase
) : TripRepository {
    
    /**
     * Generates a unique ID that doesn't already exist in the database
     */
    @OptIn(ExperimentalUuidApi::class)
    private suspend fun generateUniqueId(): String = withContext(Dispatchers.Default) {
        var candidateId: String
        do {
            candidateId = Uuid.random().toString()
        } while (isIdAlreadyInUse(candidateId))
        candidateId
    }
    
    /**
     * Checks if a trip ID is already in use in the database
     */
    private suspend fun isIdAlreadyInUse(id: String): Boolean = withContext(Dispatchers.Default) {
        queries.getById(id).executeAsOneOrNull() != null
    }
    
    private val queries = database.tripQueries
    
    override fun getTrips(): Flow<List<Trip>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { tripEntities ->
                tripEntities.map { it.toDomainModel() }
            }
    }
    
    override suspend fun getTripById(id: String): Trip? = withContext(Dispatchers.Default) {
        queries.getById(id)
            .executeAsOneOrNull()
            ?.toDomainModel()
    }
    
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createTrip(trip: Trip): String = withContext(Dispatchers.Default) {
        // Generate a unique ID if the provided one is empty
        val tripId = if (trip.id.isBlank()) {
            generateUniqueId()
        } else {
            // If an ID was provided, verify it's not already in use
            if (isIdAlreadyInUse(trip.id)) {
                throw IllegalArgumentException("Trip ID '${trip.id}' is already in use")
            }
            trip.id
        }
        
        val now = Clock.System.now()
        queries.insertOrReplace(
            id = tripId,
            title = trip.title,
            description = trip.description,
            start_date = trip.startDate?.toString(),
            end_date = trip.endDate?.toString(),
            destination = trip.destination,
            cover_image_url = trip.coverImageUrl,
            is_archived = trip.isArchived,
            created_at = now.toString(),
            updated_at = now.toString(),
            owner_id = trip.ownerId,
            collaborator_ids = trip.collaboratorIds.joinToString(",")
        )
        tripId
    }
    
    override suspend fun updateTrip(trip: Trip): Unit = withContext(Dispatchers.Default) {
        val now = Clock.System.now()
        queries.insertOrReplace(
            id = trip.id,
            title = trip.title,
            description = trip.description,
            start_date = trip.startDate?.toString(),
            end_date = trip.endDate?.toString(),
            destination = trip.destination,
            cover_image_url = trip.coverImageUrl,
            is_archived = trip.isArchived,
            created_at = trip.createdAt.toString(),
            updated_at = now.toString(),
            owner_id = trip.ownerId,
            collaborator_ids = trip.collaboratorIds.joinToString(",")
        )
    }
    
    override suspend fun deleteTrip(id: String): Unit = withContext(Dispatchers.Default) {
        queries.deleteTrip(id)
    }
    
    override suspend fun archiveTrip(id: String): Unit = withContext(Dispatchers.Default) {
        queries.archiveTrip(Clock.System.now().toString(), id)
    }
    
    override suspend fun unarchiveTrip(id: String): Unit = withContext(Dispatchers.Default) {
        queries.unarchiveTrip(Clock.System.now().toString(), id)
    }
    
    /**
     * Extension function to convert database entity to domain model
     */
    private fun TripModel.toDomainModel(): Trip {
        return Trip(
            id = id,
            title = title,
            description = description,
            startDate = start_date?.let { LocalDate.parse(it) },
            endDate = end_date?.let { LocalDate.parse(it) },
            destination = destination,
            coverImageUrl = cover_image_url,
            isArchived = is_archived,
            createdAt = Instant.parse(created_at),
            updatedAt = Instant.parse(updated_at),
            ownerId = owner_id,
            collaboratorIds = if (collaborator_ids.isBlank()) emptyList() else collaborator_ids.split(",")
        )
    }
}
