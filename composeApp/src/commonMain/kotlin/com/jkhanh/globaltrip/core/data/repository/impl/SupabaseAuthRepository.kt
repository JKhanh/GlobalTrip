package com.jkhanh.globaltrip.core.data.repository.impl

import com.jkhanh.globaltrip.core.domain.model.AuthError
import com.jkhanh.globaltrip.core.domain.model.AuthResult
import com.jkhanh.globaltrip.core.domain.model.AuthState
import com.jkhanh.globaltrip.core.domain.model.AuthUser
import com.jkhanh.globaltrip.core.domain.repository.AuthRepository
import com.jkhanh.globaltrip.core.domain.repository.SecureStorage
import com.jkhanh.globaltrip.core.network.SupabaseClient
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
    private val secureStorage: SecureStorage
) : AuthRepository {
    
    override suspend fun signIn(email: String, password: String): AuthResult<AuthUser> {
        return try {
            SupabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            
            val currentUser = SupabaseClient.auth.currentUserOrNull()
            val currentSession = SupabaseClient.auth.currentSessionOrNull()
            
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
                
                AuthResult.Success(authUser)
            } else {
                AuthResult.Error(AuthError.InvalidCredentials)
            }
        } catch (e: Exception) {
            AuthResult.Error(mapExceptionToAuthError(e))
        }
    }
    
    override suspend fun signUp(email: String, password: String, name: String?): AuthResult<AuthUser> {
        return try {
            SupabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                if (name != null) {
                    data = buildJsonObject {
                        put("name", name)
                    }
                }
            }
            
            val currentUser = SupabaseClient.auth.currentUserOrNull()
            val currentSession = SupabaseClient.auth.currentSessionOrNull()
            
            if (currentUser != null) {
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
                    session.refreshToken?.let {
                        secureStorage.saveToken(SecureStorage.REFRESH_TOKEN_KEY, it)
                    }
                }
                
                AuthResult.Success(authUser)
            } else {
                AuthResult.Error(AuthError.Unknown("Failed to create user"))
            }
        } catch (e: Exception) {
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
            SupabaseClient.auth.signOut()
            secureStorage.clearAll()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(mapExceptionToAuthError(e))
        }
    }
    
    override suspend fun getCurrentUser(): AuthUser? {
        return try {
            val currentUser = SupabaseClient.auth.currentUserOrNull()
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
        
        // Check if we have a valid session
        val currentUser = getCurrentUser()
        if (currentUser != null) {
            emit(AuthState.Authenticated(currentUser))
        } else {
            emit(AuthState.Unauthenticated)
        }
        
        // TODO: Implement real-time auth state changes listener
        // This would typically use Supabase real-time subscriptions
    }
    
    override suspend fun resetPassword(email: String): AuthResult<Unit> {
        return try {
            SupabaseClient.auth.resetPasswordForEmail(email)
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(mapExceptionToAuthError(e))
        }
    }
    
    override suspend fun refreshSession(): AuthResult<AuthUser> {
        return try {
            SupabaseClient.auth.refreshCurrentSession()
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
        return when {
            exception.message?.contains("Invalid login credentials", ignoreCase = true) == true -> 
                AuthError.InvalidCredentials
            exception.message?.contains("User already registered", ignoreCase = true) == true -> 
                AuthError.EmailAlreadyExists
            exception.message?.contains("Email not confirmed", ignoreCase = true) == true -> 
                AuthError.EmailNotVerified
            exception.message?.contains("Password should be at least", ignoreCase = true) == true -> 
                AuthError.WeakPassword
            exception.message?.contains("network", ignoreCase = true) == true ||
            exception.message?.contains("connection", ignoreCase = true) == true -> 
                AuthError.NetworkError
            else -> AuthError.Unknown(exception.message)
        }
    }
}