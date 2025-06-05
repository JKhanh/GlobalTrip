package com.jkhanh.globaltrip.core.domain.repository

/**
 * Interface for secure storage of sensitive data like tokens
 * Platform-specific implementations will handle encryption and secure storage
 */
interface SecureStorage {
    
    /**
     * Save a secure token with associated key
     * @param key Unique identifier for the token
     * @param token The token value to store securely
     */
    suspend fun saveToken(key: String, token: String)
    
    /**
     * Retrieve a token by key
     * @param key Unique identifier for the token
     * @return The token value or null if not found
     */
    suspend fun getToken(key: String): String?
    
    /**
     * Delete a specific token
     * @param key Unique identifier for the token to delete
     */
    suspend fun deleteToken(key: String)
    
    /**
     * Clear all stored tokens (useful for logout)
     */
    suspend fun clearAll()
    
    /**
     * Check if a token exists
     * @param key Unique identifier for the token
     * @return true if token exists, false otherwise
     */
    suspend fun hasToken(key: String): Boolean
    
    companion object {
        const val ACCESS_TOKEN_KEY = "access_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"
        const val USER_SESSION_KEY = "user_session"
    }
}