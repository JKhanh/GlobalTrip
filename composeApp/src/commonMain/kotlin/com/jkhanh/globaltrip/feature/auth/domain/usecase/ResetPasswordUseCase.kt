package com.jkhanh.globaltrip.feature.auth.domain.usecase

import com.jkhanh.globaltrip.core.domain.model.AuthError
import com.jkhanh.globaltrip.core.domain.model.AuthResult
import com.jkhanh.globaltrip.core.domain.repository.AuthRepository

/**
 * Use case for resetting user password
 */
class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {
    
    /**
     * Execute password reset operation
     * @param email User's email address
     * @return AuthResult indicating success or error
     */
    suspend operator fun invoke(email: String): AuthResult<Unit> {
        // Validate email
        if (email.isBlank() || !isValidEmail(email)) {
            return AuthResult.Error(AuthError.InvalidCredentials)
        }
        
        // Delegate to repository
        return authRepository.resetPassword(email.trim())
    }
    
    /**
     * Basic email validation
     */
    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".") && email.length > 5
    }
}