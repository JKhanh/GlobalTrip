package com.jkhanh.globaltrip.feature.auth.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.jkhanh.globaltrip.core.ui.components.GTButton
import com.jkhanh.globaltrip.core.ui.components.GTTextField

/**
 * Reusable authentication form component
 */
@Composable
fun AuthForm(
    email: String,
    password: String,
    name: String = "",
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNameChange: (String) -> Unit = {},
    onSubmit: () -> Unit,
    isSignUpMode: Boolean = false,
    isLoading: Boolean = false,
    isEmailValid: Boolean = true,
    isPasswordValid: Boolean = true,
    isNameValid: Boolean = true,
    isFormValid: Boolean = true,
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Email field
        GTTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email",
            placeholder = "Enter your email",
            isError = !isEmailValid,
            errorMessage = if (!isEmailValid) "Please enter a valid email" else null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = if (isSignUpMode) ImeAction.Next else ImeAction.Next
            )
        )
        
        // Name field (only for sign up)
        if (isSignUpMode) {
            GTTextField(
                value = name,
                onValueChange = onNameChange,
                label = "Name",
                placeholder = "Enter your name",
                isError = !isNameValid,
                errorMessage = if (!isNameValid) "Please enter your name" else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
        }
        
        // Password field
        GTTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Password",
            placeholder = if (isSignUpMode) "Choose a strong password" else "Enter your password",
            isError = !isPasswordValid,
            errorMessage = when {
                !isPasswordValid && isSignUpMode -> "Password must be at least 6 characters"
                !isPasswordValid -> "Please enter your password"
                else -> null
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Text(
                        text = if (isPasswordVisible) "Hide" else "Show",
                        style = androidx.compose.material.MaterialTheme.typography.caption
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Submit button
        GTButton(
            text = if (isSignUpMode) "Create Account" else "Sign In",
            onClick = onSubmit,
            enabled = isFormValid && !isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}