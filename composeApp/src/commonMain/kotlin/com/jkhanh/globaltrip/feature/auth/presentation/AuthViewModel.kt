package com.jkhanh.globaltrip.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jkhanh.globaltrip.core.domain.model.AuthResult
import com.jkhanh.globaltrip.core.domain.model.AuthState
import com.jkhanh.globaltrip.core.domain.model.getUserMessage
import com.jkhanh.globaltrip.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.jkhanh.globaltrip.feature.auth.domain.usecase.ObserveAuthStateUseCase
import com.jkhanh.globaltrip.feature.auth.domain.usecase.ResetPasswordUseCase
import com.jkhanh.globaltrip.feature.auth.domain.usecase.SignInUseCase
import com.jkhanh.globaltrip.feature.auth.domain.usecase.SignOutUseCase
import com.jkhanh.globaltrip.feature.auth.domain.usecase.SignUpUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for authentication feature following MVI pattern
 */
class AuthViewModel(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private val _effects = Channel<AuthEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()
    
    init {
        observeAuthState()
        loadCurrentUser()
    }
    
    /**
     * Handle user intents
     */
    fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.SignIn -> signIn(intent.email, intent.password)
            is AuthIntent.SignUp -> signUp(intent.email, intent.password, intent.name)
            is AuthIntent.SignInWithGoogle -> signInWithGoogle()
            is AuthIntent.SignInWithFacebook -> signInWithFacebook()
            is AuthIntent.SignOut -> signOut()
            is AuthIntent.ResetPassword -> resetPassword(intent.email)
            is AuthIntent.ClearError -> clearError()
            is AuthIntent.ToggleAuthMode -> toggleAuthMode()
            is AuthIntent.TogglePasswordVisibility -> togglePasswordVisibility()
        }
    }
    
    /**
     * Observe authentication state changes
     */
    private fun observeAuthState() {
        observeAuthStateUseCase()
            .onEach { authState ->
                when (authState) {
                    is AuthState.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is AuthState.Authenticated -> {
                        _uiState.update { 
                            it.copy(
                                currentUser = authState.user,
                                isLoading = false,
                                error = null,
                                isSignInSuccessful = true
                            )
                        }
                        _effects.trySend(AuthEffect.NavigateToMain)
                    }
                    is AuthState.Unauthenticated -> {
                        _uiState.update { 
                            it.copy(
                                currentUser = null,
                                isLoading = false,
                                isSignInSuccessful = false
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Load current user on app start
     */
    private fun loadCurrentUser() {
        viewModelScope.launch {
            val currentUser = getCurrentUserUseCase()
            _uiState.update { it.copy(currentUser = currentUser) }
        }
    }
    
    /**
     * Sign in with email and password
     */
    private fun signIn(email: String, password: String) {
        if (!validateSignInForm(email, password)) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = signInUseCase(email, password)) {
                is AuthResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            currentUser = result.data,
                            isSignInSuccessful = true,
                            error = null
                        )
                    }
                    _effects.trySend(AuthEffect.ShowSuccessMessage("Welcome back!"))
                }
                is AuthResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = result.error.getUserMessage(),
                            isSignInSuccessful = false
                        )
                    }
                    _effects.trySend(AuthEffect.ShowErrorMessage(result.error.getUserMessage()))
                }
            }
        }
    }
    
    /**
     * Sign up with email, password and optional name
     */
    private fun signUp(email: String, password: String, name: String?) {
        if (!validateSignUpForm(email, password, name)) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = signUpUseCase(email, password, name)) {
                is AuthResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            currentUser = result.data,
                            isSignUpSuccessful = true,
                            error = null
                        )
                    }
                    _effects.trySend(AuthEffect.ShowSuccessMessage("Account created successfully!"))
                }
                is AuthResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.error.getUserMessage(),
                            isSignUpSuccessful = false
                        )
                    }
                    _effects.trySend(AuthEffect.ShowErrorMessage(result.error.getUserMessage()))
                }
            }
        }
    }
    
    /**
     * Sign in with Google
     */
    private fun signInWithGoogle() {
        // TODO: Implement when OAuth is ready
        _effects.trySend(AuthEffect.ShowErrorMessage("Google sign-in coming soon"))
    }
    
    /**
     * Sign in with Facebook
     */
    private fun signInWithFacebook() {
        // TODO: Implement when OAuth is ready
        _effects.trySend(AuthEffect.ShowErrorMessage("Facebook sign-in coming soon"))
    }
    
    /**
     * Sign out current user
     */
    private fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = signOutUseCase()) {
                is AuthResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            currentUser = null,
                            error = null,
                            isSignInSuccessful = false
                        )
                    }
                    _effects.trySend(AuthEffect.NavigateToLogin)
                    _effects.trySend(AuthEffect.ShowSuccessMessage("Signed out successfully"))
                }
                is AuthResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.error.getUserMessage()
                        )
                    }
                    _effects.trySend(AuthEffect.ShowErrorMessage(result.error.getUserMessage()))
                }
            }
        }
    }
    
    /**
     * Reset password
     */
    private fun resetPassword(email: String) {
        if (email.isBlank()) {
            _effects.trySend(AuthEffect.ShowErrorMessage("Please enter your email address"))
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = resetPasswordUseCase(email)) {
                is AuthResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isResetPasswordSuccessful = true,
                            error = null
                        )
                    }
                    _effects.trySend(AuthEffect.ShowPasswordResetConfirmation(email))
                }
                is AuthResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.error.getUserMessage(),
                            isResetPasswordSuccessful = false
                        )
                    }
                    _effects.trySend(AuthEffect.ShowErrorMessage(result.error.getUserMessage()))
                }
            }
        }
    }
    
    /**
     * Clear any displayed error
     */
    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    /**
     * Toggle between sign in and sign up modes
     */
    private fun toggleAuthMode() {
        _uiState.update { 
            it.copy(
                isSignInMode = !it.isSignInMode,
                error = null,
                isSignInSuccessful = false,
                isSignUpSuccessful = false
            )
        }
    }
    
    /**
     * Toggle password visibility
     */
    private fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }
    
    /**
     * Validate sign in form
     */
    private fun validateSignInForm(email: String, password: String): Boolean {
        val isEmailValid = email.isNotBlank() && email.contains("@")
        val isPasswordValid = password.isNotBlank()
        
        _uiState.update { 
            it.copy(
                isEmailValid = isEmailValid,
                isPasswordValid = isPasswordValid
            )
        }
        
        return isEmailValid && isPasswordValid
    }
    
    /**
     * Validate sign up form
     */
    private fun validateSignUpForm(email: String, password: String, name: String?): Boolean {
        val isEmailValid = email.isNotBlank() && email.contains("@")
        val isPasswordValid = password.length >= 6
        val isNameValid = name?.isNotBlank() ?: true // Name is optional
        
        _uiState.update { 
            it.copy(
                isEmailValid = isEmailValid,
                isPasswordValid = isPasswordValid,
                isNameValid = isNameValid
            )
        }
        
        return isEmailValid && isPasswordValid && isNameValid
    }
}