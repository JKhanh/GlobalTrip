package com.jkhanh.globaltrip.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jkhanh.globaltrip.core.domain.model.AuthError
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
            is AuthIntent.ValidateEmail -> validateEmail(intent.email)
            is AuthIntent.ValidatePassword -> validatePassword(intent.password)
            is AuthIntent.ValidateName -> validateName(intent.name)
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
                        // Navigation handled by auth state observer in AppNavHost
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
        if (!validateSignInForm(email, password)) {
            println("📱 DEBUG: Sign in form validation failed")
            return
        }
        
        println("📱 DEBUG: Starting sign in process in ViewModel")
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            println("📱 DEBUG: Calling signInUseCase")
            when (val result = signInUseCase(email, password)) {
                is AuthResult.Success -> {
                    println("📱 DEBUG: Sign in use case successful")
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            currentUser = result.data,
                            isSignInSuccessful = true,
                            error = null
                        )
                    }
                    _effects.trySend(AuthEffect.ShowSuccessMessage("Welcome back!"))
                    // Navigation handled by auth state observer in AppNavHost
                }
                is AuthResult.Error -> {
                    println("📱 DEBUG: Sign in use case failed: ${result.error}")
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
        if (!validateSignUpForm(email, password, name)) {
            println("📱 DEBUG: Sign up form validation failed")
            return
        }
        
        println("📱 DEBUG: Starting sign up process in ViewModel")
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            println("📱 DEBUG: Calling signUpUseCase")
            when (val result = signUpUseCase(email, password, name)) {
                is AuthResult.Success -> {
                    println("📱 DEBUG: Sign up use case successful")
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            currentUser = result.data,
                            isSignUpSuccessful = true,
                            error = null
                        )
                    }
                    
                    if (result.data.isEmailVerified) {
                        _effects.trySend(AuthEffect.ShowSuccessMessage("Account created and verified!"))
                    } else {
                        _effects.trySend(AuthEffect.ShowSuccessMessage("Account created! You can start using the app. Please verify your email when convenient."))
                    }
                    
                    // Navigation handled by auth state observer in AppNavHost
                }
                is AuthResult.Error -> {
                    println("📱 DEBUG: Sign up use case failed: ${result.error}")
                    
                    // Handle email verification case specially for sign up
                    if (result.error is AuthError.EmailNotVerified) {
                        // Sign up was successful but needs email verification
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = null,
                                isSignUpSuccessful = true
                            )
                        }
                        _effects.trySend(AuthEffect.ShowSuccessMessage("Account created! Please check your email to verify your account before signing in."))
                        
                        // Auto-navigate to login screen after a brief delay
                        viewModelScope.launch {
                            kotlinx.coroutines.delay(2500) // Wait 2.5 seconds to show success message
                            _effects.trySend(AuthEffect.NavigateToLogin)
                        }
                    } else {
                        // Actual sign up failure
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
                isSignUpSuccessful = false,
                // Reset validation states when switching modes
                isEmailValid = true,
                isPasswordValid = true,
                isNameValid = true
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
     * Validate email field in real-time
     */
    private fun validateEmail(email: String) {
        val isEmailValid = email.isNotBlank() && isValidEmailFormat(email)
        _uiState.update { it.copy(isEmailValid = isEmailValid) }
    }
    
    /**
     * Validate password field in real-time
     */
    private fun validatePassword(password: String) {
        val isPasswordValid = if (uiState.value.isSignInMode) {
            password.isNotBlank()
        } else {
            password.length >= 6
        }
        _uiState.update { it.copy(isPasswordValid = isPasswordValid) }
    }
    
    /**
     * Validate name field in real-time
     */
    private fun validateName(name: String) {
        val isNameValid = name.isBlank() || name.trim().length >= 2 // Name is optional but if provided must be at least 2 chars
        _uiState.update { it.copy(isNameValid = isNameValid) }
    }
    
    /**
     * Validate sign in form
     */
    private fun validateSignInForm(email: String, password: String): Boolean {
        val isEmailValid = email.isNotBlank() && isValidEmailFormat(email)
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
        val isEmailValid = email.isNotBlank() && isValidEmailFormat(email)
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
    
    /**
     * Validate email format
     */
    private fun isValidEmailFormat(email: String): Boolean {
        return email.contains("@") && email.contains(".") && email.length > 5
    }
}