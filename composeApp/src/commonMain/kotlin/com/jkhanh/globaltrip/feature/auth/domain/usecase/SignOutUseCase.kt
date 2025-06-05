package com.jkhanh.globaltrip.feature.auth.domain.usecase

import com.jkhanh.globaltrip.core.domain.model.AuthResult
import com.jkhanh.globaltrip.core.domain.repository.AuthRepository

/**
 * Use case for signing out the current user
 */
class SignOutUseCase(
    private val authRepository: AuthRepository
) {
    
    /**
     * Execute sign out operation
     * @return AuthResult indicating success or error
     */
    suspend operator fun invoke(): AuthResult<Unit> {
        return authRepository.signOut()
    }
}