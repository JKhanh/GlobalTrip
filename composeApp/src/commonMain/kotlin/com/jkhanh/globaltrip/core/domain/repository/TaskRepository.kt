package com.jkhanh.globaltrip.core.domain.repository

import com.jkhanh.globaltrip.core.domain.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Task operations
 */
interface TaskRepository {
    /**
     * Gets all tasks for a trip
     */
    fun getTasksForTrip(tripId: String): Flow<List<Task>>
    
    /**
     * Gets all incomplete tasks for a trip
     */
    fun getIncompleteTasksForTrip(tripId: String): Flow<List<Task>>
    
    /**
     * Gets a task by ID
     */
    suspend fun getTaskById(id: String): Task?
    
    /**
     * Creates a new task
     */
    suspend fun createTask(task: Task): String
    
    /**
     * Updates an existing task
     */
    suspend fun updateTask(task: Task)
    
    /**
     * Toggles task completion status
     */
    suspend fun toggleTaskCompletion(id: String, isCompleted: Boolean)
    
    /**
     * Deletes a task
     */
    suspend fun deleteTask(id: String)
}
