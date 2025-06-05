package com.jkhanh.globaltrip.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jkhanh.globaltrip.core.domain.model.AuthState
import com.jkhanh.globaltrip.core.ui.components.EmailVerificationBanner
import com.jkhanh.globaltrip.core.ui.theme.GlobalTripThemeOption
import com.jkhanh.globaltrip.feature.auth.presentation.AuthViewModel
import com.jkhanh.globaltrip.feature.auth.domain.usecase.ObserveAuthStateUseCase
import com.jkhanh.globaltrip.feature.auth.ui.LoginScreen
import com.jkhanh.globaltrip.feature.auth.ui.RegisterScreen
import com.jkhanh.globaltrip.feature.settings.ui.SettingsScreen
import com.jkhanh.globaltrip.feature.trips.ui.TripListScreen
import com.jkhanh.globaltrip.feature.trips.ui.create.TripCreateScreen
import com.jkhanh.globaltrip.feature.trips.presentation.TripListViewModel
import com.jkhanh.globaltrip.feature.trips.presentation.TripCreateViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Main navigation host for the app using type-safe routes
 */
@Composable
fun AppNavHost(
    themeOption: GlobalTripThemeOption,
    onThemeSelected: (GlobalTripThemeOption) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    // Scaffold state for snackbar
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    
    // Auth state management using Koin injection
    val authViewModel: AuthViewModel = koinInject()
    val observeAuthStateUseCase: ObserveAuthStateUseCase = koinInject()
    val authState by observeAuthStateUseCase().collectAsState(initial = AuthState.Loading)
    
    // Email verification banner state
    var showEmailVerificationBanner by remember { mutableStateOf(true) }
    val shouldShowBanner = when (val state = authState) {
        is AuthState.Authenticated -> !state.user.isEmailVerified && showEmailVerificationBanner
        else -> false
    }
    
    // Start with main app - authentication is optional
    val startDestination = Trips
    
    // Handle auth state navigation (only for authenticated users navigating from auth screens)
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                // Navigate to main app if currently on auth screens
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                
                if (currentRoute?.contains("Login") == true || currentRoute?.contains("SignUp") == true) {
                    navController.navigate(Trips) {
                        popUpTo(Login) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            is AuthState.Unauthenticated -> {
                // No automatic navigation - users can stay in main app without auth
            }
            is AuthState.Loading -> {
                // Stay on current screen while loading
            }
        }
    }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val currentRouteClass: Any? = when (currentRoute) {
        "com.jkhanh.globaltrip.navigation.Trips" -> Trips
        "com.jkhanh.globaltrip.navigation.Maps" -> Maps
        "com.jkhanh.globaltrip.navigation.Expenses" -> Expenses
        "com.jkhanh.globaltrip.navigation.Settings" -> Settings
        else -> null
    }
    
    // Show bottom navigation for all main tabs (auth not required)
    val showBottomNav = isMainTab(currentRouteClass)
    
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(hostState = scaffoldState.snackbarHostState)
        },
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
            Column {
                // Email verification banner
                EmailVerificationBanner(
                    isVisible = shouldShowBanner,
                    onDismiss = { showEmailVerificationBanner = false },
                    onVerifyClick = {
                        // Navigate to a verification screen or show dialog
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(
                                "Email verification feature coming soon!"
                            )
                        }
                    }
                )
                
                // Main navigation content
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.weight(1f)
                ) {
                // Main tabs
                composable<Trips> {
                    val viewModel: TripListViewModel = koinInject()
                    
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
                        } else null,
                        onSignIn = if (authState !is AuthState.Authenticated) {
                            {
                                navController.navigate(Login)
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
                    val viewModel: TripCreateViewModel = koinInject()
                    
                    TripCreateScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.navigateUp() },
                        onTripCreated = { _ ->
                            navController.navigateUp()
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
                        onNavigateToMain = {
                            navController.navigate(Trips) {
                                popUpTo(Login) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onShowSnackbar = { message ->
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(message)
                            }
                        }
                    )
                }
                
                composable<SignUp> {
                    RegisterScreen(
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
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(message)
                            }
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
}
