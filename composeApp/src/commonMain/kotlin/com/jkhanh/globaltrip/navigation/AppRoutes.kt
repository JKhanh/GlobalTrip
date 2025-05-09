package com.jkhanh.globaltrip.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using @Serializable
 */

// Main tabs
@Serializable
object Trips

@Serializable
object Maps

@Serializable
object Expenses

@Serializable
object Settings

// Authentication screens
@Serializable
object Login

@Serializable
object SignUp

// Trip screens with arguments
@Serializable
data class TripDetail(val tripId: String)

@Serializable
object TripCreate

@Serializable
data class TripEdit(val tripId: String)

// Other screens with arguments
@Serializable
data class LocationDetail(val locationId: String)

// Extension function to check if a route is a main tab
fun isMainTab(route: Any?): Boolean {
    return route is Trips || route is Maps || route is Expenses || route is Settings
}