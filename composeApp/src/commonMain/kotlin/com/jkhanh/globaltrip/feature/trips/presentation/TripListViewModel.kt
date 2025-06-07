package com.jkhanh.globaltrip.feature.trips.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jkhanh.globaltrip.core.domain.model.Trip
import com.jkhanh.globaltrip.core.logging.Logger
import com.jkhanh.globaltrip.feature.trips.domain.usecase.GetTripsUseCase
import com.jkhanh.globaltrip.feature.trips.domain.usecase.SearchTripsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * ViewModel for the trip list screen
 */
class TripListViewModel(
    private val getTripsUseCase: GetTripsUseCase,
    private val searchTripsUseCase: SearchTripsUseCase
) : ViewModel() {
    
    companion object {
        private const val TAG = "TripListViewModel"
        private const val SEARCH_DELAY_MS = 300L
    }
    
    private val _state = MutableStateFlow(TripListState())
    val state: StateFlow<TripListState> = _state.asStateFlow()
    
    private var searchJob: Job? = null
    
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
    
    /**
     * Update search query and perform search
     */
    fun updateSearchQuery(query: String) {
        Logger.d("Updating search query: $query", TAG)
        
        _state.update { it.copy(searchQuery = query) }
        
        // Cancel previous search
        searchJob?.cancel()
        
        // Perform search with debounce
        searchJob = viewModelScope.launch {
            delay(SEARCH_DELAY_MS)
            performSearch(query)
        }
    }
    
    /**
     * Toggle search mode
     */
    fun toggleSearch() {
        val isSearchActive = !_state.value.isSearchActive
        Logger.d("Toggling search mode: $isSearchActive", TAG)
        
        _state.update { 
            it.copy(
                isSearchActive = isSearchActive,
                searchQuery = if (!isSearchActive) "" else it.searchQuery,
                filteredTrips = if (!isSearchActive) emptyList() else it.filteredTrips
            )
        }
        
        if (!isSearchActive) {
            searchJob?.cancel()
        }
    }
    
    /**
     * Clear search and return to normal view
     */
    fun clearSearch() {
        Logger.d("Clearing search", TAG)
        searchJob?.cancel()
        
        _state.update { 
            it.copy(
                searchQuery = "",
                isSearchActive = false,
                filteredTrips = emptyList()
            )
        }
    }
    
    /**
     * Perform the actual search operation
     */
    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _state.update { it.copy(filteredTrips = emptyList()) }
            return
        }
        
        Logger.d("Performing search for: $query", TAG)
        
        searchTripsUseCase(query)
            .onEach { filteredTrips ->
                Logger.i("Search found ${filteredTrips.size} trips for query: $query", TAG)
                _state.update { it.copy(filteredTrips = filteredTrips) }
            }
            .catch { e ->
                Logger.e("Search failed: ${e.message}", TAG)
                _state.update { 
                    it.copy(
                        error = "Search failed: ${e.message}",
                        filteredTrips = emptyList()
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}

