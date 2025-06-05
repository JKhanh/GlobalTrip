package com.jkhanh.globaltrip.feature.auth.domain.usecase

import com.jkhanh.globaltrip.core.domain.model.AuthResult
import com.jkhanh.globaltrip.core.domain.model.AuthUser
import com.jkhanh.globaltrip.core.domain.repository.AuthRepository
import com.jkhanh.globaltrip.core.logging.Logger

/**
 * Use case for signing in a user with email and password
 */
class SignInUseCase(
    private val authRepository: AuthRepository
) {
    
    companion object {
        private const val TAG = "SignInUseCase"
    }
    
    /**
     * Execute sign in operation
     * @param email User's email address
     * @param password User's password
     * @return AuthResult containing user data or error
     */
    suspend operator fun invoke(email: String, password: String): AuthResult<AuthUser> {
        Logger.d("SignInUseCase called with email: $email", TAG)
        
        // Validate input
        if (email.isBlank()) {
            Logger.w("SignInUseCase - email is blank", TAG)
            return AuthResult.Error(com.jkhanh.globaltrip.core.domain.model.AuthError.InvalidCredentials)
        }
        
        if (password.isBlank()) {
            Logger.w("SignInUseCase - password is blank", TAG)
            return AuthResult.Error(com.jkhanh.globaltrip.core.domain.model.AuthError.InvalidCredentials)
        }
        
        Logger.d("SignInUseCase - delegating to repository", TAG)
        // Delegate to repository
        return authRepository.signIn(email.trim(), password)
    }
}