package com.jkhanh.globaltrip.core.domain.model

/**
 * Represents different types of authentication errors
 */
sealed interface AuthError {
    /**
     * Network connectivity issues
     */
    data object NetworkError : AuthError
    
    /**
     * Invalid email or password
     */
    data object InvalidCredentials : AuthError
    
    /**
     * Email already exists during registration
     */
    data object EmailAlreadyExists : AuthError
    
    /**
     * User account not found
     */
    data object UserNotFound : AuthError
    
    /**
     * Weak password (doesn't meet requirements)
     */
    data object WeakPassword : AuthError
    
    /**
     * Email verification required
     */
    data object EmailNotVerified : AuthError
    
    /**
     * Session expired, need to re-authenticate
     */
    data object SessionExpired : AuthError
    
    /**
     * OAuth provider error (Google, Facebook, etc.)
     */
    data class OAuthError(val provider: String, val message: String) : AuthError
    
    /**
     * Server-side error with custom message
     */
    data class ServerError(val message: String) : AuthError
    
    /**
     * Unknown error with optional message
     */
    data class Unknown(val message: String? = null) : AuthError
}

/**
 * Extension function to get user-friendly error message
 */
fun AuthError.getUserMessage(): String = when (this) {
    is AuthError.NetworkError -> "Please check your internet connection and try again"
    is AuthError.InvalidCredentials -> "Invalid email or password"
    is AuthError.EmailAlreadyExists -> "An account with this email already exists"
    is AuthError.UserNotFound -> "No account found with this email"
    is AuthError.WeakPassword -> "Password must be at least 8 characters long"
    is AuthError.EmailNotVerified -> "Please verify your email before signing in"
    is AuthError.SessionExpired -> "Your session has expired. Please sign in again"
    is AuthError.OAuthError -> "Failed to sign in with $provider: $message"
    is AuthError.ServerError -> message
    is AuthError.Unknown -> message ?: "An unexpected error occurred"
}