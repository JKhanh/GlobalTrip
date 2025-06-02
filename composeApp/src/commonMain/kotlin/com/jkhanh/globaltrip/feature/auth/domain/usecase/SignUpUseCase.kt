package com.jkhanh.globaltrip.feature.auth.domain.usecase

import com.jkhanh.globaltrip.core.domain.model.AuthError
import com.jkhanh.globaltrip.core.domain.model.AuthResult
import com.jkhanh.globaltrip.core.domain.model.AuthUser
import com.jkhanh.globaltrip.core.domain.repository.AuthRepository

/**
 * Use case for signing up a new user
 */
class SignUpUseCase(
    private val authRepository: AuthRepository
) {
    
    /**
     * Execute sign up operation
     * @param email User's email address
     * @param password User's password
     * @param name Optional user display name
     * @return AuthResult containing user data or error
     */
    suspend operator fun invoke(email: String, password: String, name: String? = null): AuthResult<AuthUser> {
        // Validate email
        if (email.isBlank() || !isValidEmail(email)) {
            return AuthResult.Error(AuthError.InvalidCredentials)
        }
        
        // Validate password
        if (password.length < 6) {
            return AuthResult.Error(AuthError.WeakPassword)
        }
        
        // Validate name if provided
        val trimmedName = name?.trim()?.takeIf { it.isNotBlank() }
        
        // Delegate to repository
        return authRepository.signUp(email.trim(), password, trimmedName)
    }
    
    /**
     * Basic email validation
     */
    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".") && email.length > 5
    }
}