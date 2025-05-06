package com.jkhanh.globaltrip.navigation

/**
 * Represents a screen in the app with its route path
 */
sealed class Screen(val route: String) {
    // Main tabs
    object Trips : Screen("trips")
    object Maps : Screen("maps")
    object Expenses : Screen("expenses")
    object Settings : Screen("settings")
    
    // Trip screens
    object TripDetail : Screen("trip_detail/{tripId}") {
        fun createRoute(tripId: String) = "trip_detail/$tripId"
    }
    object TripCreate : Screen("trip_create")
    object TripEdit : Screen("trip_edit/{tripId}") {
        fun createRoute(tripId: String) = "trip_edit/$tripId"
    }
    
    // Authentication screens
    object Login : Screen("login")
    object SignUp : Screen("signup")
    
    // Other screens
    object LocationDetail : Screen("location_detail/{locationId}") {
        fun createRoute(locationId: String) = "location_detail/$locationId"
    }
}
