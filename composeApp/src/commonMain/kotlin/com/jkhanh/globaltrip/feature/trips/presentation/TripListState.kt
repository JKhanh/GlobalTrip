package com.jkhanh.globaltrip.feature.trips.presentation

import com.jkhanh.globaltrip.core.domain.model.Trip

/**
 * State for the trip list screen
 */
data class TripListState(
    val trips: List<Trip> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterType: TripFilterType = TripFilterType.ALL
)

/**
 * Filter types for trip list
 */
enum class TripFilterType {
    ALL,
    UPCOMING,
    PAST,
    ARCHIVED
}
