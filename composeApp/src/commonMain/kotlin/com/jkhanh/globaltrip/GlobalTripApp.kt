package com.jkhanh.globaltrip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jkhanh.globaltrip.core.ui.theme.GlobalTripTheme
import com.jkhanh.globaltrip.core.ui.theme.GlobalTripThemeOption
import com.jkhanh.globaltrip.di.AppModule
import com.jkhanh.globaltrip.feature.settings.ui.SettingsScreen
import com.jkhanh.globaltrip.feature.trips.ui.TripListScreen
import com.jkhanh.globaltrip.feature.trips.ui.create.TripCreateScreen
import com.jkhanh.globaltrip.navigation.BottomNavigationBar
import com.jkhanh.globaltrip.navigation.Navigator
import com.jkhanh.globaltrip.navigation.Screen
import com.jkhanh.globaltrip.navigation.rememberNavigator

/**
 * Main application composable
 */
@Composable
fun GlobalTripApp() {
    val navigator = rememberNavigator()
    
    // Get the theme settings from the SettingsViewModel
    val settingsViewModel = remember { AppModule.provideSettingsViewModel() }
    val currentTheme by settingsViewModel.themeOption.collectAsState()
    
    GlobalTripTheme(
        themeOption = currentTheme
    ) {
        Scaffold(
            bottomBar = {
                // Only show bottom bar on main tabs
                if (navigator.currentScreen == Screen.Trips ||
                    navigator.currentScreen == Screen.Maps ||
                    navigator.currentScreen == Screen.Expenses ||
                    navigator.currentScreen == Screen.Settings
                ) {
                    BottomNavigationBar(
                        currentRoute = navigator.currentScreen.route,
                        onNavigate = { screen -> 
                            navigator.navigateTo(screen)
                        }
                    )
                }
            }
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colors.background
            ) {
                when (navigator.currentScreen) {
                    // Main tabs
                    Screen.Trips -> TripsScreen(navigator)
                    Screen.Maps -> MapsScreen()
                    Screen.Expenses -> ExpensesScreen()
                    Screen.Settings -> SettingsScreen(
                        currentTheme = currentTheme,
                        onThemeSelected = { theme ->
                            settingsViewModel.setThemeOption(theme)
                        }
                    )
                    
                    // Other screens
                    Screen.TripCreate -> {
                        val viewModel = remember { AppModule.provideTripCreateViewModel() }
                        TripCreateScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navigator.navigateBack() },
                            onTripCreated = { tripId ->
                                // Navigate to trip detail (to be implemented)
                                navigator.navigateTo(Screen.Trips)
                            }
                        )
                    }
                    
                    // Handle other screens when implemented
                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Screen not implemented yet")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TripsScreen(navigator: Navigator) {
    val viewModel = remember { AppModule.provideTripListViewModel() }
    
    TripListScreen(
        viewModel = viewModel,
        onTripClick = { trip -> 
            // Navigate to trip detail (to be implemented)
        },
        onCreateTripClick = {
            navigator.navigateTo(Screen.TripCreate)
        }
    )
}

@Composable
fun MapsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Maps Screen")
    }
}

@Composable
fun ExpensesScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Expenses Screen")
    }
}
