package com.jkhanh.globaltrip.core.domain.repository

import com.jkhanh.globaltrip.core.domain.model.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * Repository interface for Activity operations
 */
interface ActivityRepository {
    /**
     * Gets all activities for a specific trip
     */
    fun getActivitiesForTrip(tripId: String): Flow<List<Activity>>
    
    /**
     * Gets all activities for a specific trip day
     */
    fun getActivitiesForTripDay(tripId: String, tripDayId: String): Flow<List<Activity>>
    
    /**
     * Gets all activities for a specific date in a trip
     */
    fun getActivitiesForDate(tripId: String, date: LocalDate): Flow<List<Activity>>
    
    /**
     * Gets a specific activity by ID
     */
    suspend fun getActivityById(id: String): Activity?
    
    /**
     * Creates a new activity
     */
    suspend fun createActivity(activity: Activity): String
    
    /**
     * Updates an existing activity
     */
    suspend fun updateActivity(activity: Activity)
    
    /**
     * Deletes an activity
     */
    suspend fun deleteActivity(id: String)
}
