package com.jkhanh.globaltrip.feature.auth.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jkhanh.globaltrip.core.ui.components.GTOutlinedButton

/**
 * Social login buttons component
 */
@Composable
fun SocialLoginButtons(
    onGoogleSignIn: () -> Unit,
    onFacebookSignIn: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Divider with "OR" text
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Divider(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "OR",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Divider(modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Google Sign In Button
        GTOutlinedButton(
            text = "Continue with Google",
            onClick = onGoogleSignIn,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Facebook Sign In Button
        GTOutlinedButton(
            text = "Continue with Facebook",
            onClick = onFacebookSignIn,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}