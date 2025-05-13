package com.jkhanh.globaltrip.core.data.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.jkhanh.globaltrip.core.database.GlobalTripDatabase
import com.jkhanh.globaltrip.core.domain.model.Trip
import com.jkhanh.globaltrip.core.domain.repository.TripRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * SQLDelight implementation of TripRepository
 */
class SqlDelightTripRepository(
    private val database: GlobalTripDatabase
) : TripRepository {
    
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
    
    override suspend fun createTrip(trip: Trip): String = withContext(Dispatchers.Default) {
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
            created_at = now.toString(),
            updated_at = now.toString(),
            owner_id = trip.ownerId
        )
        trip.id
    }
    
    override suspend fun updateTrip(trip: Trip) = withContext(Dispatchers.Default) {
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
            owner_id = trip.ownerId
        )
    }
    
    override suspend fun deleteTrip(id: String) = withContext(Dispatchers.Default) {
        queries.deleteTrip(id)
    }
    
    override suspend fun archiveTrip(id: String) = withContext(Dispatchers.Default) {
        queries.archiveTrip(Clock.System.now().toString(), id)
    }
    
    override suspend fun unarchiveTrip(id: String) = withContext(Dispatchers.Default) {
        queries.unarchiveTrip(Clock.System.now().toString(), id)
    }
    
    /**
     * Extension function to convert database entity to domain model
     */
    private fun com.jkhanh.globaltrip.core.database.Trip.toDomainModel(): Trip {
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
            ownerId = owner_id
        )
    }
}
