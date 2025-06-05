package com.jkhanh.globaltrip.core.data.repository.impl

import com.jkhanh.globaltrip.core.domain.model.AuthError
import com.jkhanh.globaltrip.core.domain.model.AuthResult
import com.jkhanh.globaltrip.core.domain.model.AuthState
import com.jkhanh.globaltrip.core.domain.model.AuthUser
import com.jkhanh.globaltrip.core.domain.repository.AuthRepository
import com.jkhanh.globaltrip.core.domain.repository.SecureStorage
import com.jkhanh.globaltrip.core.logging.Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
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
    
    companion object {
        private const val TAG = "SupabaseAuthRepository"
    }
    
    // Store temporary unverified user for immediate access
    private var temporaryUser: AuthUser? = null
    
    // Reactive auth state flow
    private val _authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    private val authStateFlow = _authStateFlow.asStateFlow()
    
    override suspend fun signIn(email: String, password: String): AuthResult<AuthUser> {
        return try {
            Logger.d("Starting sign in for email: $email", TAG)
            
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
                    lastSignInAt = currentUser.lastSignInAt?.toString(),
                    isEmailVerified = currentUser.emailConfirmedAt != null
                )
                
                // Store tokens securely
                currentSession?.let { session ->
                    secureStorage.saveToken(SecureStorage.ACCESS_TOKEN_KEY, session.accessToken)
                    session.refreshToken?.let {
                        secureStorage.saveToken(SecureStorage.REFRESH_TOKEN_KEY, it)
                    }
                }
                
                // Update auth state to authenticated
                _authStateFlow.value = AuthState.Authenticated(authUser)
                
                Logger.i("Sign in successful", TAG)
                AuthResult.Success(authUser)
            } else {
                Logger.w("Sign in failed - no current user", TAG)
                AuthResult.Error(AuthError.InvalidCredentials)
            }
        } catch (e: Exception) {
            Logger.e("Sign in exception: ${e::class.simpleName}", TAG, e)
            
            // Handle email not confirmed case specially for sign in
            if (e.message?.contains("email_not_confirmed", ignoreCase = true) == true ||
                e.message?.contains("Email not confirmed", ignoreCase = true) == true) {
                Logger.i("Email not confirmed - creating unverified user session", TAG)
                
                // Create an unverified user object for immediate access
                // This allows users to use the app while encouraging email verification
                val unverifiedUser = AuthUser(
                    id = "unverified_${email.hashCode()}", // Temporary ID based on email
                    email = email,
                    name = null,
                    avatarUrl = null,
                    createdAt = null,
                    lastSignInAt = Clock.System.now().toString(),
                    isEmailVerified = false
                )
                
                // Store the temporary user for auth state tracking
                temporaryUser = unverifiedUser
                
                // Update auth state to authenticated
                _authStateFlow.value = AuthState.Authenticated(unverifiedUser)
                
                Logger.i("Created unverified user session for immediate access", TAG)
                return AuthResult.Success(unverifiedUser)
            }
            
            AuthResult.Error(mapExceptionToAuthError(e))
        }
    }
    
    override suspend fun signUp(email: String, password: String, name: String?): AuthResult<AuthUser> {
        return try {
            Logger.d("Starting sign up for email: $email, name: $name", TAG)
            
            // Check if Supabase client is initialized
            try {
                supabaseClient
            } catch (clientError: Exception) {
                Logger.e("Failed to get Supabase client", TAG, clientError)
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
                    // Note: Email confirmation behavior is controlled by Supabase project settings
                    // For immediate access, ensure "Enable email confirmations" is disabled in Supabase Auth settings
                }
            } catch (signUpError: Exception) {
                Logger.e("Sign up API call failed", TAG, signUpError)
                throw signUpError
            }
            
            Logger.d("Supabase signUp call completed", TAG)
            
            val currentUser = supabaseClient.auth.currentUserOrNull()
            val currentSession = supabaseClient.auth.currentSessionOrNull()
            
            Logger.d("Current user: ${currentUser?.id}, session exists: ${currentSession != null}", TAG)
            
            if (currentUser != null) {
                // User is immediately signed in (email confirmation disabled)
                val authUser = AuthUser(
                    id = currentUser.id,
                    email = currentUser.email ?: email,
                    name = currentUser.userMetadata?.get("name")?.toString() ?: name,
                    avatarUrl = currentUser.userMetadata?.get("avatar_url")?.toString(),
                    createdAt = currentUser.createdAt?.toString(),
                    lastSignInAt = currentUser.lastSignInAt?.toString(),
                    isEmailVerified = currentUser.emailConfirmedAt != null
                )
                
                // Store tokens securely
                currentSession?.let { session ->
                    secureStorage.saveToken(SecureStorage.ACCESS_TOKEN_KEY, session.accessToken)
                    session.refreshToken?.let { refreshToken ->
                        secureStorage.saveToken(SecureStorage.REFRESH_TOKEN_KEY, refreshToken)
                    }
                }
                
                // Update auth state to authenticated
                _authStateFlow.value = AuthState.Authenticated(authUser)
                
                Logger.i("Sign up successful with immediate sign in, email verified: ${authUser.isEmailVerified}", TAG)
                AuthResult.Success(authUser)
            } else {
                // User was created but needs to be signed in manually
                Logger.d("Sign up successful but no current user - attempting automatic sign in", TAG)
                
                if (signUpResult != null) {
                    // Try to sign in immediately after signup
                    try {
                        val signInResult = signIn(email, password)
                        when (signInResult) {
                            is AuthResult.Success -> {
                                // Update the user with unverified email status
                                val updatedUser = signInResult.data.copy(isEmailVerified = false)
                                
                                // Update auth state to authenticated
                                _authStateFlow.value = AuthState.Authenticated(updatedUser)
                                
                                Logger.i("Auto sign-in after signup successful, email unverified", TAG)
                                AuthResult.Success(updatedUser)
                            }
                            is AuthResult.Error -> {
                                Logger.w("Auto sign-in failed, user needs to verify email first", TAG)
                                AuthResult.Error(AuthError.EmailNotVerified)
                            }
                        }
                    } catch (e: Exception) {
                        Logger.e("Auto sign-in exception", TAG, e)
                        AuthResult.Error(AuthError.EmailNotVerified)
                    }
                } else {
                    Logger.w("Sign up failed - no result returned", TAG)
                    AuthResult.Error(AuthError.Unknown("Failed to create user - no response from server"))
                }
            }
        } catch (e: Exception) {
            Logger.e("Sign up exception: ${e::class.simpleName}", TAG, e)
            
            // Log the cause chain
            var cause = e.cause
            var depth = 1
            while (cause != null && depth < 5) {
                Logger.d("Cause $depth: ${cause::class.simpleName} - ${cause.message}", TAG)
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
            // Clear temporary user
            temporaryUser = null
            
            // Update auth state to unauthenticated
            _authStateFlow.value = AuthState.Unauthenticated
            
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(mapExceptionToAuthError(e))
        }
    }
    
    override suspend fun getCurrentUser(): AuthUser? {
        return try {
            // Check for temporary unverified user first
            temporaryUser?.let { return it }
            
            val currentUser = supabaseClient.auth.currentUserOrNull()
            currentUser?.let {
                AuthUser(
                    id = it.id,
                    email = it.email ?: "",
                    name = it.userMetadata?.get("name")?.toString(),
                    avatarUrl = it.userMetadata?.get("avatar_url")?.toString(),
                    createdAt = it.createdAt?.toString(),
                    lastSignInAt = it.lastSignInAt?.toString(),
                    isEmailVerified = it.emailConfirmedAt != null
                )
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override fun observeAuthState(): Flow<AuthState> = flow {
        // Emit initial loading state
        emit(AuthState.Loading)
        
        // Initialize auth state based on current session
        try {
            val currentUser = getCurrentUser()
            val initialState = if (currentUser != null) {
                AuthState.Authenticated(currentUser)
            } else {
                AuthState.Unauthenticated
            }
            _authStateFlow.value = initialState
            emit(initialState)
        } catch (e: Exception) {
            Logger.e("observeAuthState error", TAG, e)
            _authStateFlow.value = AuthState.Unauthenticated
            emit(AuthState.Unauthenticated)
        }
        
        // Then emit all subsequent changes from the reactive flow
        authStateFlow.collect { state ->
            if (state != AuthState.Loading) {
                emit(state)
            }
        }
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
        Logger.d("Mapping exception to AuthError: ${exception::class.simpleName} - ${exception.message}", TAG)
        
        return when {
            exception.message?.contains("Invalid login credentials", ignoreCase = true) == true -> {
                Logger.d("Mapped to InvalidCredentials", TAG)
                AuthError.InvalidCredentials
            }
            exception.message?.contains("User already registered", ignoreCase = true) == true ||
            exception.message?.contains("already been registered", ignoreCase = true) == true -> {
                Logger.d("Mapped to EmailAlreadyExists", TAG)
                AuthError.EmailAlreadyExists
            }
            exception.message?.contains("Email not confirmed", ignoreCase = true) == true ||
            exception.message?.contains("email address needs to be confirmed", ignoreCase = true) == true -> {
                Logger.d("Mapped to EmailNotVerified", TAG)
                AuthError.EmailNotVerified
            }
            exception.message?.contains("Password should be at least", ignoreCase = true) == true ||
            exception.message?.contains("password is too weak", ignoreCase = true) == true -> {
                Logger.d("Mapped to WeakPassword", TAG)
                AuthError.WeakPassword
            }
            exception.message?.contains("network", ignoreCase = true) == true ||
            exception.message?.contains("connection", ignoreCase = true) == true ||
            exception.message?.contains("timeout", ignoreCase = true) == true ||
            exception.message?.contains("unreachable", ignoreCase = true) == true -> {
                Logger.d("Mapped to NetworkError", TAG)
                AuthError.NetworkError
            }
            exception.message?.contains("Invalid API key", ignoreCase = true) == true ||
            exception.message?.contains("Unauthorized", ignoreCase = true) == true ||
            exception.message?.contains("401", ignoreCase = true) == true -> {
                Logger.d("Mapped to InvalidCredentials (API key issue)", TAG)
                AuthError.Unknown("Configuration error: Invalid API key or unauthorized access")
            }
            exception.message?.contains("Project paused", ignoreCase = true) == true -> {
                Logger.d("Project is paused", TAG)
                AuthError.Unknown("Supabase project is paused")
            }
            exception.message?.contains("Rate limit", ignoreCase = true) == true -> {
                Logger.d("Rate limited", TAG)
                AuthError.Unknown("Too many requests. Please try again later.")
            }
            else -> {
                Logger.d("Mapped to Unknown error", TAG)
                AuthError.Unknown(exception.message ?: "Unknown authentication error")
            }
        }
    }
}