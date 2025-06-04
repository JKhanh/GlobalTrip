package com.jkhanh.globaltrip.di

import com.jkhanh.globaltrip.feature.auth.presentation.AuthViewModel
import com.jkhanh.globaltrip.feature.auth.domain.usecase.SignInUseCase
import com.jkhanh.globaltrip.feature.auth.domain.usecase.SignUpUseCase
import com.jkhanh.globaltrip.feature.auth.domain.usecase.SignOutUseCase
import com.jkhanh.globaltrip.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.jkhanh.globaltrip.feature.auth.domain.usecase.ObserveAuthStateUseCase
import com.jkhanh.globaltrip.feature.auth.domain.usecase.ResetPasswordUseCase
import org.koin.dsl.module

/**
 * Auth module for authentication feature
 */
val authModule = module {
    
    // Auth use cases
    single { SignInUseCase(authRepository = get()) }
    single { SignUpUseCase(authRepository = get()) }
    single { SignOutUseCase(authRepository = get()) }
    single { GetCurrentUserUseCase(authRepository = get()) }
    single { ObserveAuthStateUseCase(authRepository = get()) }
    single { ResetPasswordUseCase(authRepository = get()) }
    
    // Auth ViewModel
    factory { 
        AuthViewModel(
            signInUseCase = get(),
            signUpUseCase = get(),
            signOutUseCase = get(),
            getCurrentUserUseCase = get(),
            observeAuthStateUseCase = get(),
            resetPasswordUseCase = get()
        )
    }
    
}