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
 * Standalone Register screen (alternative to combined LoginScreen)
 */
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
    onShowSnackbar: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel: AuthViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    
    // Handle effects
    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is AuthEffect.NavigateToMain -> onNavigateToMain()
                is AuthEffect.NavigateToLogin -> onNavigateToLogin()
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
                text = "Join GlobalTrip",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Create your account to start planning amazing trips",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Register Card
            GTCard {
                Column {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Register Form
                    AuthForm(
                        email = email,
                        password = password,
                        name = name,
                        onEmailChange = { 
                            email = it
                            viewModel.handleIntent(AuthIntent.ValidateEmail(it))
                        },
                        onPasswordChange = { 
                            password = it
                            viewModel.handleIntent(AuthIntent.ValidatePassword(it))
                        },
                        onNameChange = { 
                            name = it
                            viewModel.handleIntent(AuthIntent.ValidateName(it))
                        },
                        onSubmit = {
                            viewModel.handleIntent(
                                AuthIntent.SignUp(
                                    email = email,
                                    password = password,
                                    name = name.takeIf { it.isNotBlank() }
                                )
                            )
                        },
                        isSignUpMode = true,
                        isLoading = uiState.isLoading,
                        isEmailValid = uiState.isEmailValid,
                        isPasswordValid = uiState.isPasswordValid,
                        isNameValid = uiState.isNameValid,
                        isFormValid = uiState.isSignUpFormValid
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Social Login Buttons
                    SocialLoginButtons(
                        onGoogleSignIn = { viewModel.handleIntent(AuthIntent.SignInWithGoogle) },
                        onFacebookSignIn = { viewModel.handleIntent(AuthIntent.SignInWithFacebook) },
                        isLoading = uiState.isLoading
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Back to Login
                    GTOutlinedButton(
                        text = "Already have an account? Sign in",
                        onClick = onNavigateToLogin,
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