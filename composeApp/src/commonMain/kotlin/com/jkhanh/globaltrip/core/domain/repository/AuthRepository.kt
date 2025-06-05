package com.jkhanh.globaltrip.core.domain.repository

import com.jkhanh.globaltrip.core.domain.model.AuthResult
import com.jkhanh.globaltrip.core.domain.model.AuthState
import com.jkhanh.globaltrip.core.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations
 * Defines contracts for all authentication-related data operations
 */
interface AuthRepository {
    
    /**
     * Sign in with email and password
     * @param email User's email address
     * @param password User's password
     * @return AuthResult containing user data or error
     */
    suspend fun signIn(email: String, password: String): AuthResult<AuthUser>
    
    /**
     * Sign up with email, password and optional name
     * @param email User's email address
     * @param password User's password
     * @param name Optional user display name
     * @return AuthResult containing user data or error
     */
    suspend fun signUp(email: String, password: String, name: String? = null): AuthResult<AuthUser>
    
    /**
     * Sign in with Google OAuth
     * @return AuthResult containing user data or error
     */
    suspend fun signInWithGoogle(): AuthResult<AuthUser>
    
    /**
     * Sign in with Facebook OAuth
     * @return AuthResult containing user data or error
     */
    suspend fun signInWithFacebook(): AuthResult<AuthUser>
    
    /**
     * Sign out current user
     * @return AuthResult indicating success or error
     */
    suspend fun signOut(): AuthResult<Unit>
    
    /**
     * Get current authenticated user if any
     * @return Current user or null if not authenticated
     */
    suspend fun getCurrentUser(): AuthUser?
    
    /**
     * Observe authentication state changes
     * @return Flow of AuthState changes
     */
    fun observeAuthState(): Flow<AuthState>
    
    /**
     * Reset password via email
     * @param email User's email address
     * @return AuthResult indicating success or error
     */
    suspend fun resetPassword(email: String): AuthResult<Unit>
    
    /**
     * Refresh current session/token
     * @return AuthResult containing updated user data or error
     */
    suspend fun refreshSession(): AuthResult<AuthUser>
    
    /**
     * Check if user session is valid
     * @return true if session is valid, false otherwise
     */
    suspend fun isSessionValid(): Boolean
}