package com.jkhanh.globaltrip.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jkhanh.globaltrip.core.ui.components.GTCard
import com.jkhanh.globaltrip.core.ui.components.GTOutlinedButton
import org.koin.compose.koinInject
import com.jkhanh.globaltrip.feature.auth.presentation.AuthEffect
import com.jkhanh.globaltrip.feature.auth.presentation.AuthIntent
import com.jkhanh.globaltrip.feature.auth.presentation.AuthViewModel
import com.jkhanh.globaltrip.feature.auth.ui.components.AuthForm
import com.jkhanh.globaltrip.feature.auth.ui.components.SocialLoginButtons
import kotlinx.coroutines.flow.collectLatest

/**
 * Login/Register screen for authentication
 */
@Composable
fun LoginScreen(
    onNavigateToMain: () -> Unit,
    onShowSnackbar: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel: AuthViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    
    // Local validation for password matching
    val passwordsMatch = password == confirmPassword || confirmPassword.isEmpty()
    
    // Handle effects
    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is AuthEffect.NavigateToMain -> {
                    // Navigation handled by auth state observer in AppNavHost
                }
                is AuthEffect.NavigateToLogin -> {
                    // Already on login screen
                }
                is AuthEffect.ShowSuccessMessage -> onShowSnackbar(effect.message)
                is AuthEffect.ShowErrorMessage -> onShowSnackbar(effect.message)
                is AuthEffect.ShowPasswordResetConfirmation -> {
                    onShowSnackbar("Password reset link sent to ${effect.email}")
                }
            }
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App title
            Text(
                text = "GlobalTrip",
                style = MaterialTheme.typography.h3,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Plan your next adventure",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Auth Card
            GTCard {
                Column {
                    // Mode title
                    Text(
                        text = if (uiState.isSignInMode) "Welcome Back" else "Create Account",
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = if (uiState.isSignInMode) "Sign in to continue your journey" else "Join us to start planning",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Auth Form
                    AuthForm(
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        name = name,
                        onEmailChange = { 
                            email = it
                            viewModel.handleIntent(AuthIntent.ValidateEmail(it))
                        },
                        onPasswordChange = { 
                            password = it
                            viewModel.handleIntent(AuthIntent.ValidatePassword(it))
                        },
                        onConfirmPasswordChange = { 
                            confirmPassword = it
                        },
                        onNameChange = { 
                            name = it
                            viewModel.handleIntent(AuthIntent.ValidateName(it))
                        },
                        onSubmit = {
                            if (uiState.isSignInMode) {
                                viewModel.handleIntent(AuthIntent.SignIn(email, password))
                            } else {
                                if (passwordsMatch) {
                                    viewModel.handleIntent(AuthIntent.SignUp(email, password, name.takeIf { it.isNotBlank() }))
                                }
                            }
                        },
                        isSignUpMode = !uiState.isSignInMode,
                        isLoading = uiState.isLoading,
                        isEmailValid = uiState.isEmailValid,
                        isPasswordValid = uiState.isPasswordValid,
                        isNameValid = uiState.isNameValid,
                        isPasswordsMatch = passwordsMatch,
                        isFormValid = if (uiState.isSignInMode) {
                            uiState.isCurrentFormValid
                        } else {
                            uiState.isCurrentFormValid && passwordsMatch
                        }
                    )
                    
                    // Forgot Password (only in sign in mode)
                    if (uiState.isSignInMode) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        TextButton(
                            onClick = {
                                if (email.isNotBlank()) {
                                    viewModel.handleIntent(AuthIntent.ResetPassword(email))
                                } else {
                                    onShowSnackbar("Please enter your email first")
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Forgot Password?")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Social Login Buttons
                    SocialLoginButtons(
                        onGoogleSignIn = { viewModel.handleIntent(AuthIntent.SignInWithGoogle) },
                        onFacebookSignIn = { viewModel.handleIntent(AuthIntent.SignInWithFacebook) },
                        isLoading = uiState.isLoading
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Toggle between sign in and sign up
                    GTOutlinedButton(
                        text = if (uiState.isSignInMode) "New user? Create account" else "Already have an account? Sign in",
                        onClick = { 
                            viewModel.handleIntent(AuthIntent.ToggleAuthMode)
                            // Clear form when switching modes
                            email = ""
                            password = ""
                            name = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        
        // Loading overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.primary
                )
            }
        }
    }
}