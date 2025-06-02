package com.jkhanh.globaltrip.feature.trips.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jkhanh.globaltrip.core.domain.model.Trip
import com.jkhanh.globaltrip.feature.trips.domain.usecase.CreateTripUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

/**
 * ViewModel for trip creation
 */
class TripCreateViewModel(
    private val createTripUseCase: CreateTripUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(TripCreateState())
    val state: StateFlow<TripCreateState> = _state.asStateFlow()
    
    /**
     * Updates the title field
     */
    fun updateTitle(title: String) {
        _state.update { it.copy(title = title) }
        validateInput()
    }
    
    /**
     * Updates the description field
     */
    fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }
    
    /**
     * Updates the destination field
     */
    fun updateDestination(destination: String) {
        _state.update { it.copy(destination = destination) }
        validateInput()
    }
    
    /**
     * Updates the start date
     */
    fun updateStartDate(startDate: LocalDate) {
        _state.update { it.copy(startDate = startDate) }
        validateInput()
    }
    
    /**
     * Updates the end date
     */
    fun updateEndDate(endDate: LocalDate) {
        _state.update { it.copy(endDate = endDate) }
        validateInput()
    }
    
    /**
     * Creates a new trip
     */
    fun createTrip() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                
                val trip = Trip(
                    id = "",  // Empty ID will be replaced with a unique one in the repository
                    title = _state.value.title,
                    description = _state.value.description,
                    startDate = _state.value.startDate,
                    endDate = _state.value.endDate,
                    destination = _state.value.destination,
                    coverImageUrl = null,
                    isArchived = _state.value.isArchived,
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now(),
                    ownerId = "user1"  // Mock user ID
                )
                
                val tripId = createTripUseCase(trip)
                
                _state.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        tripId = tripId
                    )
                }
            } catch (e: IllegalArgumentException) {
                // Handle specific error when ID is already in use
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Trip ID already exists"
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to create trip"
                    )
                }
            }
        }
    }
    
    /**
     * Validates the input fields
     */
    private fun validateInput() {
        val isValid = _state.value.title.isNotBlank() &&
                _state.value.destination.isNotBlank() &&
                _state.value.startDate != null &&
                _state.value.endDate != null &&
                (_state.value.endDate != null && _state.value.startDate != null && 
                _state.value.endDate!! >= _state.value.startDate!!)
        
        _state.update { it.copy(isValid = isValid) }
    }
    
    /**
     * Updates the archive status
     */
    fun updateArchiveStatus(isArchived: Boolean) {
        _state.update { it.copy(isArchived = isArchived) }
    }
    
    /**
     * Resets the success state
     */
    fun resetSuccessState() {
        _state.update { it.copy(isSuccess = false, tripId = null) }
    }
}

/**
 * State for trip creation
 */
data class TripCreateState(
    val title: String = "",
    val description: String = "",
    val destination: String = "",
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val isArchived: Boolean = false,
    val isLoading: Boolean = false,
    val isValid: Boolean = false,
    val isSuccess: Boolean = false,
    val tripId: String? = null,
    val error: String? = null
)
