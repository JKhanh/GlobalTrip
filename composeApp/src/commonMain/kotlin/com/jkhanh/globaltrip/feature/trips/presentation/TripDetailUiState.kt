package com.jkhanh.globaltrip.feature.trips.presentation

import com.jkhanh.globaltrip.core.domain.model.Trip

/**
 * UI state for the trip detail screen
 */
data class TripDetailUiState(
    val trip: Trip? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)