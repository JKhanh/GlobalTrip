package com.jkhanh.globaltrip.feature.auth.domain.usecase

import com.jkhanh.globaltrip.core.domain.model.AuthResult
import com.jkhanh.globaltrip.core.domain.model.AuthUser
import com.jkhanh.globaltrip.core.domain.repository.AuthRepository

/**
 * Use case for signing in a user with email and password
 */
class SignInUseCase(
    private val authRepository: AuthRepository
) {
    
    /**
     * Execute sign in operation
     * @param email User's email address
     * @param password User's password
     * @return AuthResult containing user data or error
     */
    suspend operator fun invoke(email: String, password: String): AuthResult<AuthUser> {
        // Validate input
        if (email.isBlank()) {
            return AuthResult.Error(com.jkhanh.globaltrip.core.domain.model.AuthError.InvalidCredentials)
        }
        
        if (password.isBlank()) {
            return AuthResult.Error(com.jkhanh.globaltrip.core.domain.model.AuthError.InvalidCredentials)
        }
        
        // Delegate to repository
        return authRepository.signIn(email.trim(), password)
    }
}