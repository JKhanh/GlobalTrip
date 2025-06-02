package com.jkhanh.globaltrip.feature.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jkhanh.globaltrip.core.ui.components.theme.ThemePreview
import com.jkhanh.globaltrip.core.ui.components.theme.ThemeSelector
import com.jkhanh.globaltrip.core.ui.theme.GlobalTripThemeOption

@Composable
fun SettingsScreen(
    currentTheme: GlobalTripThemeOption,
    onThemeSelected: (GlobalTripThemeOption) -> Unit,
    onSignOut: (() -> Unit)? = null
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // App Theme Section
        Text(
            text = "App Theme",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Theme Selector
        ThemeSelector(
            currentTheme = currentTheme,
            onThemeSelected = onThemeSelected
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Theme Preview
        ThemePreview()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Account Section (if signed in)
        if (onSignOut != null) {
            Text(
                text = "Account",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Button(
                        onClick = onSignOut,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.error
                        )
                    ) {
                        Text(
                            text = "Sign Out",
                            color = MaterialTheme.colors.onError
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // About Section
        Text(
            text = "About",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "GlobalTrip",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Version 1.0.0",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Text(
                    text = "A cross-platform trip planning application built with Kotlin Multiplatform and Jetpack Compose.",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}