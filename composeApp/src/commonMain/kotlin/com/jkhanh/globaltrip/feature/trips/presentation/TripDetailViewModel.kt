package com.jkhanh.globaltrip.feature.trips.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jkhanh.globaltrip.core.logging.Logger
import com.jkhanh.globaltrip.feature.trips.domain.usecase.GetTripByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the trip detail screen
 */
class TripDetailViewModel(
    private val getTripByIdUseCase: GetTripByIdUseCase
) : ViewModel() {
    
    companion object {
        private const val TAG = "TripDetailViewModel"
    }
    
    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()
    
    /**
     * Load trip details by ID
     */
    fun loadTrip(tripId: String) {
        Logger.d("Loading trip with ID: $tripId", TAG)
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            try {
                val trip = getTripByIdUseCase(tripId)
                
                if (trip != null) {
                    Logger.i("Successfully loaded trip: ${trip.title}", TAG)
                    _uiState.value = _uiState.value.copy(
                        trip = trip,
                        isLoading = false,
                        error = null
                    )
                } else {
                    Logger.w("Trip not found with ID: $tripId", TAG)
                    _uiState.value = _uiState.value.copy(
                        trip = null,
                        isLoading = false,
                        error = "Trip not found"
                    )
                }
            } catch (e: Exception) {
                Logger.e("Failed to load trip with ID: $tripId - ${e.message}", TAG)
                _uiState.value = _uiState.value.copy(
                    trip = null,
                    isLoading = false,
                    error = "Failed to load trip: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Refresh the currently loaded trip
     */
    fun refresh() {
        val currentTrip = _uiState.value.trip
        if (currentTrip != null) {
            loadTrip(currentTrip.id)
        }
    }
    
    /**
     * Clear any error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}