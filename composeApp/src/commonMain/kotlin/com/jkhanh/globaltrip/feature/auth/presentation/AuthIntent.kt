package com.jkhanh.globaltrip.feature.auth.presentation

/**
 * Represents user actions/intents in the authentication feature
 */
sealed interface AuthIntent {
    
    /**
     * User wants to sign in with email and password
     */
    data class SignIn(val email: String, val password: String) : AuthIntent
    
    /**
     * User wants to sign up with email, password and optional name
     */
    data class SignUp(val email: String, val password: String, val name: String? = null) : AuthIntent
    
    /**
     * User wants to sign in with Google
     */
    data object SignInWithGoogle : AuthIntent
    
    /**
     * User wants to sign in with Facebook
     */
    data object SignInWithFacebook : AuthIntent
    
    /**
     * User wants to sign out
     */
    data object SignOut : AuthIntent
    
    /**
     * User wants to reset their password
     */
    data class ResetPassword(val email: String) : AuthIntent
    
    /**
     * Clear any displayed error
     */
    data object ClearError : AuthIntent
    
    /**
     * Toggle between sign in and sign up modes
     */
    data object ToggleAuthMode : AuthIntent
    
    /**
     * Toggle password visibility
     */
    data object TogglePasswordVisibility : AuthIntent
}