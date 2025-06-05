package com.jkhanh.globaltrip.core.domain.model

/**
 * Represents the current authentication state of the application
 */
sealed interface AuthState {
    /**
     * Initial state or when checking authentication status
     */
    data object Loading : AuthState
    
    /**
     * User is not authenticated
     */
    data object Unauthenticated : AuthState
    
    /**
     * User is authenticated with user details
     */
    data class Authenticated(val user: AuthUser) : AuthState
}