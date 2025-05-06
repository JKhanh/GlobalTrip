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
import kotlinx.datetime.LocalDate
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
                _state.update {
                    it.copy(
                        trips = trips,
                        isLoading = false,
                        error = null
                    )
                }
                applyFilter(_state.value.filterType)
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
    
    /**
     * Applies a filter to the trip list
     */
    fun applyFilter(filterType: TripFilterType) {
        _state.update { it.copy(filterType = filterType) }
        
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        val filteredTrips = when (filterType) {
            TripFilterType.ALL -> _state.value.trips
            TripFilterType.UPCOMING -> _state.value.trips.filter { it.startDate != null && it.startDate >= today && !it.isArchived }
            TripFilterType.PAST -> _state.value.trips.filter { it.endDate != null && it.endDate < today && !it.isArchived }
            TripFilterType.ARCHIVED -> _state.value.trips.filter { it.isArchived }
        }
        
        _state.update { it.copy(trips = filteredTrips) }
    }
}
