package com.jkhanh.globaltrip.feature.auth.domain.usecase

import com.jkhanh.globaltrip.core.domain.model.AuthUser
import com.jkhanh.globaltrip.core.domain.repository.AuthRepository

/**
 * Use case for getting the current authenticated user
 */
class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    
    /**
     * Execute get current user operation
     * @return Current authenticated user or null if not authenticated
     */
    suspend operator fun invoke(): AuthUser? {
        return authRepository.getCurrentUser()
    }
}