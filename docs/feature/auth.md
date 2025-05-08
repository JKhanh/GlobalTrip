# Auth Feature Module

The Auth module handles user authentication, registration, profile management, and session tracking within the GlobalTrip application.

## Features

- User registration and login
- Social media authentication (Google, Apple, Facebook)
- Password management and recovery
- User profile creation and editing
- Session management and token handling
- Biometric authentication (where supported)
- Email verification
- Account linking between platforms

## Functional Requirements

### Authentication

- Implement secure authentication flows for email/password
- Support OAuth2 for social media logins
- Handle token refresh and storage securely
- Provide biometric login on supported platforms
- Support offline authentication
- Implement proper session timeout and renewal

### User Profiles

- Create and manage user profiles
- Support profile picture upload and management
- Store user preferences
- Track user activity history
- Manage privacy settings

### Security

- Implement secure password policies
- Support two-factor authentication
- Handle secure credential storage
- Implement account recovery flows
- Protect against common authentication attacks

## MVI Implementation

The Auth module follows the Model-View-Intent (MVI) architecture pattern:

### States

- `LoginState`: Tracks login form input, validation, and submission status
- `RegistrationState`: Manages registration form data and validation
- `ProfileState`: Represents the current user profile information
- `AuthState`: Overall authentication state (authenticated, unauthenticated, loading)

### Intents

- `LoginIntent`: User actions related to logging in
- `RegistrationIntent`: User actions for account creation
- `ProfileIntent`: User profile modification actions
- `AuthIntent`: General authentication actions (logout, verify token, etc.)

### Effects

- Navigation effects (to verification screen, profile screen, etc.)
- Toast/snackbar notifications for success/failure
- Biometric prompt display

## Dependencies

### Core Dependencies

- `core:domain`: Domain models and use cases
- `core:data`: Repository implementations
- `core:network`: Network communication
- `core:security`: Secure storage
- `core:common`: Common utilities
- `core:ui`: UI components

### Authentication Libraries

- `io.ktor:ktor-client-auth`: Authentication for API requests
- `net.openid:appauth`: OAuth2 implementation
- Platform-specific biometric libraries

### UI Components

- Compose Multiplatform UI components
- Material 3 design components
- Form validation components

## Implementation Examples

### Login Screen

```kotlin
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginEffect.NavigateToHome -> {
                    // Navigate to home screen
                }
                is LoginEffect.ShowError -> {
                    // Show error message
                }
                is LoginEffect.NavigateToRegistration -> {
                    // Navigate to registration
                }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo and welcome text
        
        // Email field
        OutlinedTextField(
            value = state.email,
            onValueChange = { email ->
                viewModel.processIntent(LoginIntent.UpdateEmail(email))
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = state.emailError != null,
            supportingText = {
                state.emailError?.let { Text(it) }
            }
        )
        
        // Password field
        OutlinedTextField(
            value = state.password,
            onValueChange = { password ->
                viewModel.processIntent(LoginIntent.UpdatePassword(password))
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            isError = state.passwordError != null,
            supportingText = {
                state.passwordError?.let { Text(it) }
            }
        )
        
        // Login button
        GlobalTripButton(
            text = "Login",
            onClick = {
                viewModel.processIntent(LoginIntent.SubmitLogin)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            loading = state.isLoading,
            enabled = state.isFormValid
        )
        
        // Social login options
        
        // Register link
        TextButton(
            onClick = {
                viewModel.processIntent(LoginIntent.NavigateToRegistration)
            }
        ) {
            Text("Don't have an account? Register")
        }
    }
}
```

### Login ViewModel

```kotlin
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()
    
    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()
    
    fun processIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.UpdateEmail -> updateEmail(intent.email)
            is LoginIntent.UpdatePassword -> updatePassword(intent.password)
            is LoginIntent.SubmitLogin -> submitLogin()
            is LoginIntent.SocialLogin -> handleSocialLogin(intent.provider)
            is LoginIntent.NavigateToRegistration -> {
                viewModelScope.launch {
                    _effect.send(LoginEffect.NavigateToRegistration)
                }
            }
        }
    }
    
    private fun updateEmail(email: String) {
        val emailError = validateEmailUseCase(email)
        _state.update { it.copy(
            email = email,
            emailError = emailError,
            isFormValid = emailError == null && _state.value.passwordError == null
                && email.isNotEmpty() && _state.value.password.isNotEmpty()
        )}
    }
    
    private fun updatePassword(password: String) {
        val passwordError = validatePasswordUseCase(password)
        _state.update { it.copy(
            password = password,
            passwordError = passwordError,
            isFormValid = _state.value.emailError == null && passwordError == null
                && _state.value.email.isNotEmpty() && password.isNotEmpty()
        )}
    }
    
    private fun submitLogin() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            loginUseCase(
                email = _state.value.email,
                password = _state.value.password
            ).onSuccess {
                _effect.send(LoginEffect.NavigateToHome)
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false) }
                _effect.send(LoginEffect.ShowError(error.message ?: "Login failed"))
            }
        }
    }
}

data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isFormValid: Boolean = false
)

sealed interface LoginIntent {
    data class UpdateEmail(val email: String) : LoginIntent
    data class UpdatePassword(val password: String) : LoginIntent
    object SubmitLogin : LoginIntent
    data class SocialLogin(val provider: SocialProvider) : LoginIntent
    object NavigateToRegistration : LoginIntent
}

sealed interface LoginEffect {
    object NavigateToHome : LoginEffect
    object NavigateToRegistration : LoginEffect
    data class ShowError(val message: String) : LoginEffect
}

enum class SocialProvider {
    GOOGLE, APPLE, FACEBOOK
}
```