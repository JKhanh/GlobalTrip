package com.jkhanh.globaltrip.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jkhanh.globaltrip.core.domain.model.AuthState
import com.jkhanh.globaltrip.core.ui.theme.GlobalTripThemeOption
import com.jkhanh.globaltrip.di.AppModule
import com.jkhanh.globaltrip.feature.auth.ui.LoginScreen
import com.jkhanh.globaltrip.feature.auth.ui.RegisterScreen
import com.jkhanh.globaltrip.feature.settings.ui.SettingsScreen
import com.jkhanh.globaltrip.feature.trips.ui.TripListScreen
import com.jkhanh.globaltrip.feature.trips.ui.create.TripCreateScreen
import kotlinx.coroutines.flow.collectLatest

/**
 * Main navigation host for the app using type-safe routes
 */
@Composable
fun AppNavHost(
    themeOption: GlobalTripThemeOption,
    onThemeSelected: (GlobalTripThemeOption) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    // Auth state management
    val authViewModel = remember { AppModule.provideAuthViewModel() }
    val authUiState by authViewModel.uiState.collectAsState()
    val observeAuthStateUseCase = remember { AppModule.provideObserveAuthStateUseCase() }
    val authState by observeAuthStateUseCase().collectAsState(initial = AuthState.Loading)
    
    // Determine start destination based on auth state
    val startDestination = when (authState) {
        is AuthState.Authenticated -> Trips
        is AuthState.Unauthenticated -> Login
        is AuthState.Loading -> Login // Show login while loading
    }
    
    // Handle auth state navigation
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                // Navigate to main app if currently on auth screens
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                if (currentRoute == Login::class.simpleName || currentRoute == SignUp::class.simpleName) {
                    navController.navigate(Trips) {
                        popUpTo(Login) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            is AuthState.Unauthenticated -> {
                // Navigate to login if currently on protected screens
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                if (currentRoute != Login::class.simpleName && currentRoute != SignUp::class.simpleName) {
                    navController.navigate(Login) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            is AuthState.Loading -> {
                // Stay on current screen while loading
            }
        }
    }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRouteClass = navBackStackEntry?.destination?.route?.let { route ->
        when (route) {
            Trips::class.simpleName -> Trips
            Maps::class.simpleName -> Maps
            Expenses::class.simpleName -> Expenses
            Settings::class.simpleName -> Settings
            else -> null
        }
    }
    
    // Show bottom navigation only for authenticated users on main tabs
    val showBottomNav = authState is AuthState.Authenticated && isMainTab(currentRouteClass)
    
    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNavigationBar(
                    currentRouteClass = currentRouteClass,
                    onNavigate = { destination ->
                        navController.navigate(destination)
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
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                // Main tabs
                composable<Trips> {
                    val viewModel = remember { AppModule.provideTripListViewModel() }
                    
                    TripListScreen(
                        viewModel = viewModel,
                        onTripClick = { trip -> 
                            navController.navigate(TripDetail(trip.id))
                        },
                        onCreateTripClick = {
                            navController.navigate(TripCreate)
                        }
                    )
                }
                
                composable<Maps> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Maps Screen")
                    }
                }
                
                composable<Expenses> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Expenses Screen")
                    }
                }
                
                composable<Settings> {
                    SettingsScreen(
                        currentTheme = themeOption,
                        onThemeSelected = onThemeSelected,
                        onSignOut = if (authState is AuthState.Authenticated) {
                            {
                                authViewModel.handleIntent(
                                    com.jkhanh.globaltrip.feature.auth.presentation.AuthIntent.SignOut
                                )
                            }
                        } else null
                    )
                }
                
                // Trip screens
                composable<TripDetail> { backStackEntry ->
                    val tripDetail = backStackEntry.toRoute<TripDetail>()
                    
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Trip Detail Screen for trip: ${tripDetail.tripId}")
                    }
                }
                
                composable<TripCreate> {
                    val viewModel = remember { AppModule.provideTripCreateViewModel() }
                    
                    TripCreateScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.navigateUp() },
                        onTripCreated = { tripId ->
                            navController.navigate(TripDetail(tripId)) {
                                popUpTo(Trips::class)
                            }
                        }
                    )
                }
                
                composable<TripEdit> { backStackEntry ->
                    val tripEdit = backStackEntry.toRoute<TripEdit>()
                    
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Trip Edit Screen for trip: ${tripEdit.tripId}")
                    }
                }
                
                // Auth screens
                composable<Login> {
                    LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToMain = {
                            navController.navigate(Trips) {
                                popUpTo(Login) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onShowSnackbar = { message ->
                            // TODO: Show snackbar - for now just log
                            println("Snackbar: $message")
                        }
                    )
                }
                
                composable<SignUp> {
                    RegisterScreen(
                        viewModel = authViewModel,
                        onNavigateToLogin = {
                            navController.navigate(Login) {
                                popUpTo(SignUp) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onNavigateToMain = {
                            navController.navigate(Trips) {
                                popUpTo(SignUp) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onShowSnackbar = { message ->
                            // TODO: Show snackbar - for now just log
                            println("Snackbar: $message")
                        }
                    )
                }
                
                // Other screens
                composable<LocationDetail> { backStackEntry ->
                    val locationDetail = backStackEntry.toRoute<LocationDetail>()
                    
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Location Detail Screen for location: ${locationDetail.locationId}")
                    }
                }
            }
        }
    }
}
