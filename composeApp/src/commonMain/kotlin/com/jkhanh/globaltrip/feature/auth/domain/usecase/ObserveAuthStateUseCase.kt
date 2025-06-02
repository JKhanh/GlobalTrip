package com.jkhanh.globaltrip.feature.auth.domain.usecase

import com.jkhanh.globaltrip.core.domain.model.AuthState
import com.jkhanh.globaltrip.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing authentication state changes
 */
class ObserveAuthStateUseCase(
    private val authRepository: AuthRepository
) {
    
    /**
     * Execute observe auth state operation
     * @return Flow of AuthState changes
     */
    operator fun invoke(): Flow<AuthState> {
        return authRepository.observeAuthState()
    }
}