package com.jkhanh.globaltrip.core.domain.repository

import com.jkhanh.globaltrip.core.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for User operations
 */
interface UserRepository {
    /**
     * Gets the current logged-in user
     */
    fun getCurrentUser(): Flow<User?>
    
    /**
     * Gets a user by ID
     */
    suspend fun getUserById(id: String): User?
    
    /**
     * Gets users by IDs
     */
    suspend fun getUsersByIds(ids: List<String>): List<User>
    
    /**
     * Updates user profile information
     */
    suspend fun updateUserProfile(user: User)
}
