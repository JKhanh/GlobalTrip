package com.jkhanh.globaltrip.feature.auth.presentation

import com.jkhanh.globaltrip.core.domain.model.AuthUser

/**
 * UI state for authentication screens
 */
data class AuthUiState(
    val currentUser: AuthUser? = null,
    val isLoading: Boolean = false,
    val isSignInMode: Boolean = true, // true for sign in, false for sign up
    val isPasswordVisible: Boolean = false,
    val error: String? = null,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isNameValid: Boolean = true,
    val isSignInSuccessful: Boolean = false,
    val isSignUpSuccessful: Boolean = false,
    val isResetPasswordSuccessful: Boolean = false
) {
    
    /**
     * Check if the form is valid for sign in
     */
    val isSignInFormValid: Boolean
        get() = isEmailValid && isPasswordValid
    
    /**
     * Check if the form is valid for sign up
     */
    val isSignUpFormValid: Boolean
        get() = isEmailValid && isPasswordValid && isNameValid
    
    /**
     * Check if user is authenticated
     */
    val isAuthenticated: Boolean
        get() = currentUser != null
    
    /**
     * Get current form validation status
     */
    val isCurrentFormValid: Boolean
        get() = if (isSignInMode) isSignInFormValid else isSignUpFormValid
}