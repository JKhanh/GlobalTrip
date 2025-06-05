package com.jkhanh.globaltrip.feature.auth.presentation

/**
 * One-time side effects for authentication feature
 */
sealed interface AuthEffect {
    
    /**
     * Navigate to main app after successful authentication
     */
    data object NavigateToMain : AuthEffect
    
    /**
     * Navigate to login screen
     */
    data object NavigateToLogin : AuthEffect
    
    /**
     * Show success message
     */
    data class ShowSuccessMessage(val message: String) : AuthEffect
    
    /**
     * Show error message
     */
    data class ShowErrorMessage(val message: String) : AuthEffect
    
    /**
     * Show password reset confirmation
     */
    data class ShowPasswordResetConfirmation(val email: String) : AuthEffect
}