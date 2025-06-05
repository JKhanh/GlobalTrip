package com.jkhanh.globaltrip.feature.auth.domain.usecase

import com.jkhanh.globaltrip.core.domain.model.AuthError
import com.jkhanh.globaltrip.core.domain.model.AuthResult
import com.jkhanh.globaltrip.core.domain.model.AuthUser
import com.jkhanh.globaltrip.core.domain.repository.AuthRepository
import com.jkhanh.globaltrip.core.logging.Logger

/**
 * Use case for signing up a new user
 */
class SignUpUseCase(
    private val authRepository: AuthRepository
) {
    
    companion object {
        private const val TAG = "SignUpUseCase"
    }
    
    /**
     * Execute sign up operation
     * @param email User's email address
     * @param password User's password
     * @param name Optional user display name
     * @return AuthResult containing user data or error
     */
    suspend operator fun invoke(email: String, password: String, name: String? = null): AuthResult<AuthUser> {
        Logger.d("SignUpUseCase called with email: $email, name: $name", TAG)
        
        // Validate email
        if (email.isBlank() || !isValidEmail(email)) {
            Logger.w("SignUpUseCase - invalid email", TAG)
            return AuthResult.Error(AuthError.InvalidCredentials)
        }
        
        // Validate password
        if (password.length < 6) {
            Logger.w("SignUpUseCase - weak password", TAG)
            return AuthResult.Error(AuthError.WeakPassword)
        }
        
        // Validate name if provided
        val trimmedName = name?.trim()?.takeIf { it.isNotBlank() }
        
        Logger.d("SignUpUseCase - delegating to repository", TAG)
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