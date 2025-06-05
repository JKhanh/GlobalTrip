package com.jkhanh.globaltrip.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Warning banner shown when user's email is not verified
 */
@Composable
fun EmailVerificationBanner(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onVerifyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            backgroundColor = Color(0xFFFFF3CD), // Light yellow warning color
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFF856404), // Dark yellow/amber
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Email not verified",
                        style = MaterialTheme.typography.subtitle2,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF856404)
                    )
                    Text(
                        text = "Please verify your email to secure your account",
                        style = MaterialTheme.typography.body2,
                        color = Color(0xFF856404)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                TextButton(
                    onClick = onVerifyClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF856404)
                    )
                ) {
                    Text(
                        text = "Verify",
                        fontWeight = FontWeight.Bold
                    )
                }
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Color(0xFF856404),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}