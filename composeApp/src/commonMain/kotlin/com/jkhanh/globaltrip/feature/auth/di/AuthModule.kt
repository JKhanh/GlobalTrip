package com.jkhanh.globaltrip.feature.auth.di

import com.jkhanh.globaltrip.core.data.repository.impl.SupabaseAuthRepository
import com.jkhanh.globaltrip.core.data.storage.MockSecureStorage
import com.jkhanh.globaltrip.core.domain.repository.AuthRepository
import com.jkhanh.globaltrip.core.domain.repository.SecureStorage
import com.jkhanh.globaltrip.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.jkhanh.globaltrip.feature.auth.domain.usecase.ObserveAuthStateUseCase
import com.jkhanh.globaltrip.feature.auth.domain.usecase.ResetPasswordUseCase
import com.jkhanh.globaltrip.feature.auth.domain.usecase.SignInUseCase
import com.jkhanh.globaltrip.feature.auth.domain.usecase.SignOutUseCase
import com.jkhanh.globaltrip.feature.auth.domain.usecase.SignUpUseCase
import com.jkhanh.globaltrip.feature.auth.presentation.AuthViewModel

/**
 * Dependency injection module for authentication feature
 */
object AuthModule {
    
    // Storage
    private val secureStorage: SecureStorage by lazy { 
        // TODO: Replace with platform-specific secure storage in Task 14
        MockSecureStorage() 
    }
    
    // Repositories
    private val authRepository: AuthRepository by lazy { 
        SupabaseAuthRepository(secureStorage) 
    }
    
    // Use Cases
    private val signInUseCase by lazy { SignInUseCase(authRepository) }
    private val signUpUseCase by lazy { SignUpUseCase(authRepository) }
    private val signOutUseCase by lazy { SignOutUseCase(authRepository) }
    private val getCurrentUserUseCase by lazy { GetCurrentUserUseCase(authRepository) }
    private val observeAuthStateUseCase by lazy { ObserveAuthStateUseCase(authRepository) }
    private val resetPasswordUseCase by lazy { ResetPasswordUseCase(authRepository) }
    
    // ViewModels
    fun provideAuthViewModel(): AuthViewModel {
        return AuthViewModel(
            signInUseCase = signInUseCase,
            signUpUseCase = signUpUseCase,
            signOutUseCase = signOutUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            observeAuthStateUseCase = observeAuthStateUseCase,
            resetPasswordUseCase = resetPasswordUseCase
        )
    }
    
    // Public access to repository for other modules if needed
    fun provideAuthRepository(): AuthRepository = authRepository
    
    // Public access to current user use case for other modules
    fun provideGetCurrentUserUseCase(): GetCurrentUserUseCase = getCurrentUserUseCase
    
    // Public access to auth state use case for other modules
    fun provideObserveAuthStateUseCase(): ObserveAuthStateUseCase = observeAuthStateUseCase
}