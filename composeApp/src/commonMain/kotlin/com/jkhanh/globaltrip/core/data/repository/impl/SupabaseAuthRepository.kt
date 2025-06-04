package com.jkhanh.globaltrip.core.data.repository.impl

import com.jkhanh.globaltrip.core.domain.model.AuthError
import com.jkhanh.globaltrip.core.domain.model.AuthResult
import com.jkhanh.globaltrip.core.domain.model.AuthState
import com.jkhanh.globaltrip.core.domain.model.AuthUser
import com.jkhanh.globaltrip.core.domain.repository.AuthRepository
import com.jkhanh.globaltrip.core.domain.repository.SecureStorage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Supabase implementation of AuthRepository
 * Handles all authentication operations using Supabase Auth
 */
class SupabaseAuthRepository(
    private val supabaseClient: SupabaseClient,
    private val secureStorage: SecureStorage
) : AuthRepository {
    
    override suspend fun signIn(email: String, password: String): AuthResult<AuthUser> {
        return try {
            println("🔐 DEBUG: Starting sign in for email: $email")
            
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            
            val currentUser = supabaseClient.auth.currentUserOrNull()
            val currentSession = supabaseClient.auth.currentSessionOrNull()
            
            if (currentUser != null) {
                val authUser = AuthUser(
                    id = currentUser.id,
                    email = currentUser.email ?: email,
                    name = currentUser.userMetadata?.get("name")?.toString(),
                    avatarUrl = currentUser.userMetadata?.get("avatar_url")?.toString(),
                    createdAt = currentUser.createdAt?.toString(),
                    lastSignInAt = currentUser.lastSignInAt?.toString()
                )
                
                // Store tokens securely
                currentSession?.let { session ->
                    secureStorage.saveToken(SecureStorage.ACCESS_TOKEN_KEY, session.accessToken)
                    session.refreshToken?.let {
                        secureStorage.saveToken(SecureStorage.REFRESH_TOKEN_KEY, it)
                    }
                }
                
                println("🔐 DEBUG: Sign in successful")
                AuthResult.Success(authUser)
            } else {
                println("🔐 DEBUG: Sign in failed - no current user")
                AuthResult.Error(AuthError.InvalidCredentials)
            }
        } catch (e: Exception) {
            println("🔐 DEBUG: Sign in exception: ${e.message}")
            println("🔐 DEBUG: Exception type: ${e::class.simpleName}")
            e.printStackTrace()
            AuthResult.Error(mapExceptionToAuthError(e))
        }
    }
    
    override suspend fun signUp(email: String, password: String, name: String?): AuthResult<AuthUser> {
        return try {
            println("🔐 DEBUG: Starting sign up for email: $email, name: $name")
            
            // Check if Supabase client is initialized
            try {
                supabaseClient
            } catch (clientError: Exception) {
                println("🔐 DEBUG: Failed to get Supabase client: ${clientError.message}")
                return AuthResult.Error(AuthError.Unknown("Supabase client initialization failed: ${clientError.message}"))
            }
            
            val signUpResult = try {
                supabaseClient.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    if (name != null) {
                        data = buildJsonObject {
                            put("name", name)
                        }
                    }
                }
            } catch (signUpError: Exception) {
                println("🔐 DEBUG: Sign up API call failed: ${signUpError.message}")
                throw signUpError
            }
            
            println("🔐 DEBUG: Supabase signUp call completed")
            
            val currentUser = supabaseClient.auth.currentUserOrNull()
            val currentSession = supabaseClient.auth.currentSessionOrNull()
            
            println("🔐 DEBUG: Current user: ${currentUser?.id}, session exists: ${currentSession != null}")
            
            if (currentUser != null) {
                // User is immediately signed in (email confirmation disabled)
                val authUser = AuthUser(
                    id = currentUser.id,
                    email = currentUser.email ?: email,
                    name = currentUser.userMetadata?.get("name")?.toString() ?: name,
                    avatarUrl = currentUser.userMetadata?.get("avatar_url")?.toString(),
                    createdAt = currentUser.createdAt?.toString(),
                    lastSignInAt = currentUser.lastSignInAt?.toString()
                )
                
                // Store tokens securely
                currentSession?.let { session ->
                    secureStorage.saveToken(SecureStorage.ACCESS_TOKEN_KEY, session.accessToken)
                    session.refreshToken?.let { refreshToken ->
                        secureStorage.saveToken(SecureStorage.REFRESH_TOKEN_KEY, refreshToken)
                    }
                }
                
                println("🔐 DEBUG: Sign up successful with immediate sign in")
                AuthResult.Success(authUser)
            } else {
                // User was created but needs email confirmation (typical case)
                println("🔐 DEBUG: Sign up successful but no current user - checking email confirmation")
                
                // In Supabase, signUp returns success even when email confirmation is required
                // The user account is created but not authenticated until email is confirmed
                if (signUpResult != null) {
                    println("🔐 DEBUG: Sign up successful - user created, email confirmation required")
                    
                    // Return a special error that indicates successful signup but needs verification
                    AuthResult.Error(AuthError.EmailNotVerified)
                } else {
                    println("🔐 DEBUG: Sign up failed - no result returned")
                    AuthResult.Error(AuthError.Unknown("Failed to create user - no response from server"))
                }
            }
        } catch (e: Exception) {
            println("🔐 DEBUG: Sign up exception: ${e.message}")
            println("🔐 DEBUG: Exception type: ${e::class.simpleName}")
            println("🔐 DEBUG: Full exception: $e")
            e.printStackTrace()
            
            // Log the cause chain
            var cause = e.cause
            var depth = 1
            while (cause != null && depth < 5) {
                println("🔐 DEBUG: Cause $depth: ${cause::class.simpleName} - ${cause.message}")
                cause = cause.cause
                depth++
            }
            
            AuthResult.Error(mapExceptionToAuthError(e))
        }
    }
    
    override suspend fun signInWithGoogle(): AuthResult<AuthUser> {
        // TODO: Implement Google OAuth in Tasks 5 & 10
        return AuthResult.Error(AuthError.Unknown("Google sign-in not yet implemented"))
    }
    
    override suspend fun signInWithFacebook(): AuthResult<AuthUser> {
        // TODO: Implement Facebook OAuth in Tasks 6 & 10
        return AuthResult.Error(AuthError.Unknown("Facebook sign-in not yet implemented"))
    }
    
    override suspend fun signOut(): AuthResult<Unit> {
        return try {
            supabaseClient.auth.signOut()
            secureStorage.clearAll()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(mapExceptionToAuthError(e))
        }
    }
    
    override suspend fun getCurrentUser(): AuthUser? {
        return try {
            val currentUser = supabaseClient.auth.currentUserOrNull()
            currentUser?.let {
                AuthUser(
                    id = it.id,
                    email = it.email ?: "",
                    name = it.userMetadata?.get("name")?.toString(),
                    avatarUrl = it.userMetadata?.get("avatar_url")?.toString(),
                    createdAt = it.createdAt?.toString(),
                    lastSignInAt = it.lastSignInAt?.toString()
                )
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override fun observeAuthState(): Flow<AuthState> = flow {
        emit(AuthState.Loading)
        
        try {
            // Check if we have a valid session
            val currentUser = getCurrentUser()
            
            if (currentUser != null) {
                emit(AuthState.Authenticated(currentUser))
            } else {
                emit(AuthState.Unauthenticated)
            }
        } catch (e: Exception) {
            println("🔐 DEBUG: observeAuthState error: ${e.message}")
            emit(AuthState.Unauthenticated)
        }
        
        // TODO: Implement real-time auth state changes listener
        // This would typically use Supabase real-time subscriptions
    }
    
    override suspend fun resetPassword(email: String): AuthResult<Unit> {
        return try {
            supabaseClient.auth.resetPasswordForEmail(email)
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(mapExceptionToAuthError(e))
        }
    }
    
    override suspend fun refreshSession(): AuthResult<AuthUser> {
        return try {
            supabaseClient.auth.refreshCurrentSession()
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                AuthResult.Success(currentUser)
            } else {
                AuthResult.Error(AuthError.SessionExpired)
            }
        } catch (e: Exception) {
            AuthResult.Error(mapExceptionToAuthError(e))
        }
    }
    
    override suspend fun isSessionValid(): Boolean {
        return try {
            getCurrentUser() != null
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Maps exceptions to appropriate AuthError types
     */
    private fun mapExceptionToAuthError(exception: Exception): AuthError {
        println("🔐 DEBUG: Mapping exception to AuthError")
        println("🔐 DEBUG: Exception message: ${exception.message}")
        println("🔐 DEBUG: Exception class: ${exception::class.simpleName}")
        
        return when {
            exception.message?.contains("Invalid login credentials", ignoreCase = true) == true -> {
                println("🔐 DEBUG: Mapped to InvalidCredentials")
                AuthError.InvalidCredentials
            }
            exception.message?.contains("User already registered", ignoreCase = true) == true ||
            exception.message?.contains("already been registered", ignoreCase = true) == true -> {
                println("🔐 DEBUG: Mapped to EmailAlreadyExists")
                AuthError.EmailAlreadyExists
            }
            exception.message?.contains("Email not confirmed", ignoreCase = true) == true ||
            exception.message?.contains("email address needs to be confirmed", ignoreCase = true) == true -> {
                println("🔐 DEBUG: Mapped to EmailNotVerified")
                AuthError.EmailNotVerified
            }
            exception.message?.contains("Password should be at least", ignoreCase = true) == true ||
            exception.message?.contains("password is too weak", ignoreCase = true) == true -> {
                println("🔐 DEBUG: Mapped to WeakPassword")
                AuthError.WeakPassword
            }
            exception.message?.contains("network", ignoreCase = true) == true ||
            exception.message?.contains("connection", ignoreCase = true) == true ||
            exception.message?.contains("timeout", ignoreCase = true) == true ||
            exception.message?.contains("unreachable", ignoreCase = true) == true -> {
                println("🔐 DEBUG: Mapped to NetworkError")
                AuthError.NetworkError
            }
            exception.message?.contains("Invalid API key", ignoreCase = true) == true ||
            exception.message?.contains("Unauthorized", ignoreCase = true) == true ||
            exception.message?.contains("401", ignoreCase = true) == true -> {
                println("🔐 DEBUG: Mapped to InvalidCredentials (API key issue)")
                AuthError.Unknown("Configuration error: Invalid API key or unauthorized access")
            }
            exception.message?.contains("Project paused", ignoreCase = true) == true -> {
                println("🔐 DEBUG: Project is paused")
                AuthError.Unknown("Supabase project is paused")
            }
            exception.message?.contains("Rate limit", ignoreCase = true) == true -> {
                println("🔐 DEBUG: Rate limited")
                AuthError.Unknown("Too many requests. Please try again later.")
            }
            else -> {
                println("🔐 DEBUG: Mapped to Unknown error")
                AuthError.Unknown(exception.message ?: "Unknown authentication error")
            }
        }
    }
}