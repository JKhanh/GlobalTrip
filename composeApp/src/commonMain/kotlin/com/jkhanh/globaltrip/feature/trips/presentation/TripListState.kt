package com.jkhanh.globaltrip.feature.trips.presentation

import com.jkhanh.globaltrip.core.domain.model.Trip

/**
 * State for the trip list screen based on Figma design
 */
data class TripListState(
    val allTrips: List<Trip> = emptyList(),
    val upcomingTrips: List<Trip> = emptyList(),
    val pastTrips: List<Trip> = emptyList(),
    val archivedTrips: List<Trip> = emptyList(),
    val filteredTrips: List<Trip> = emptyList(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
