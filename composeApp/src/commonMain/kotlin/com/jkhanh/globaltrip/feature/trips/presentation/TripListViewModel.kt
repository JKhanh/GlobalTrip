package com.jkhanh.globaltrip.feature.trips.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jkhanh.globaltrip.core.domain.model.Trip
import com.jkhanh.globaltrip.feature.trips.domain.usecase.GetTripsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * ViewModel for the trip list screen
 */
class TripListViewModel(
    private val getTripsUseCase: GetTripsUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(TripListState())
    val state: StateFlow<TripListState> = _state.asStateFlow()
    
    init {
        loadTrips()
    }
    
    /**
     * Loads trips for the current user
     */
    fun loadTrips() {
        _state.update { it.copy(isLoading = true, error = null) }
        
        getTripsUseCase()
            .onEach { trips ->
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                
                val upcomingTrips = trips.filter { trip -> 
                    trip.startDate != null && trip.startDate >= today && !trip.isArchived
                }
                
                val pastTrips = trips.filter { trip ->
                    trip.endDate != null && trip.endDate < today && !trip.isArchived
                }
                
                val archivedTrips = trips.filter { it.isArchived }
                
                _state.update {
                    it.copy(
                        allTrips = trips,
                        upcomingTrips = upcomingTrips,
                        pastTrips = pastTrips,
                        archivedTrips = archivedTrips,
                        isLoading = false,
                        error = null
                    )
                }
            }
            .catch { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load trips"
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}

