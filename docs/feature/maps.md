# Maps Feature Module

The Maps module provides location-based services, maps integration, and navigation capabilities for the GlobalTrip application across all supported platforms.

## Features

- Interactive maps for trip planning and navigation
- Point of interest discovery and recommendations
- Address search and geocoding
- Route planning with multiple transportation modes
- Offline maps for travel without internet
- Location sharing between travelers
- Custom map markers and trip visualization
- Travel time and distance estimates
- Location-based alerts and reminders

## Functional Requirements

### Maps Integration

- Display interactive maps for trip planning
- Support multiple map providers (Google Maps, MapKit, Mapbox)
- Implement common map controls (zoom, pan, rotate)
- Support different map types (standard, satellite, terrain)
- Provide platform-specific map implementations
- Cache map data for offline use
- Handle permissions for location access

### Location Services

- Get and track user's current location
- Implement geofencing for arrival/departure alerts
- Support background location updates
- Calculate distances between points
- Convert between coordinate systems
- Handle location permissions across platforms

### Points of Interest

- Search for points of interest near destinations
- Display information about attractions, restaurants, etc.
- Show user reviews and ratings
- Save favorite places to trip itinerary
- Provide recommendations based on user preferences
- Filter POIs by category, rating, etc.

### Navigation

- Generate routes between destinations
- Support multiple transportation modes
- Provide turn-by-turn directions
- Calculate travel time and distance
- Optimize routes with multiple stops
- Handle traffic and public transit information

## MVI Implementation

The Maps module follows the Model-View-Intent (MVI) architecture pattern:

### States

- `MapState`: Current map view state including center, zoom level
- `SearchState`: State of location/POI search results
- `RouteState`: State of current route planning
- `POIState`: Information about selected points of interest

### Intents

- `MapIntent`: Actions related to map manipulation
- `SearchIntent`: Location and POI search actions
- `RouteIntent`: Route planning and navigation actions
- `POIIntent`: Actions for managing points of interest

### Effects

- Permission requests
- Navigation directions
- Location sharing
- Map screenshots

## Dependencies

### Core Dependencies

- `core:domain`: Domain models and use cases
- `core:data`: Repository implementations
- `core:common`: Common utilities
- `core:ui`: UI components
- `core:network`: API communication
- `core:database`: Local storage

### Platform-Specific Map SDKs

- Android: Google Maps SDK, Mapbox SDK
- iOS: MapKit, Google Maps SDK
- Web: Mapbox GL JS, Google Maps JavaScript API

### Location Libraries

- `org.jetbrains.kotlinx:kotlinx-coroutines-core`: For async operations
- Platform-specific location services

## Implementation Examples

### Map Screen

```kotlin
@Composable
fun MapScreen(
    viewModel: MapViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val currentLocation = LocalLocationProvider.current
    
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MapEffect.RequestLocationPermission -> {
                    // Request location permission based on platform
                }
                is MapEffect.NavigateToDirections -> {
                    // Handle navigation to directions screen
                }
                is MapEffect.ShareLocation -> {
                    // Handle location sharing
                }
            }
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Render the map using platform-specific implementation
        MapView(
            center = state.center,
            zoomLevel = state.zoomLevel,
            mapType = state.mapType,
            markers = state.markers,
            routes = state.routes,
            onMapClick = { location ->
                viewModel.processIntent(MapIntent.MapClicked(location))
            },
            onMarkerClick = { marker ->
                viewModel.processIntent(MapIntent.MarkerClicked(marker))
            },
            onCameraMove = { center, zoom ->
                viewModel.processIntent(MapIntent.CameraMoved(center, zoom))
            }
        )
        
        // Map controls
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            MapTypeButton(
                currentType = state.mapType,
                onTypeChange = { newType ->
                    viewModel.processIntent(MapIntent.ChangeMapType(newType))
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            MyLocationButton(
                onClick = {
                    viewModel.processIntent(MapIntent.CenterOnLocation)
                }
            )
        }
        
        // Search bar
        SearchBar(
            query = state.searchQuery,
            onQueryChange = { query ->
                viewModel.processIntent(MapIntent.UpdateSearchQuery(query))
            },
            onSearch = {
                viewModel.processIntent(MapIntent.PerformSearch)
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        )
        
        // Show search results
        if (state.searchResults.isNotEmpty() && !state.isSearching) {
            SearchResultsList(
                results = state.searchResults,
                onResultClick = { result ->
                    viewModel.processIntent(MapIntent.SelectSearchResult(result))
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
            )
        }
        
        // Show loading indicator
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
            )
        }
        
        // Selected location details
        state.selectedLocation?.let { location ->
            LocationDetailCard(
                location = location,
                onDirectionsClick = {
                    viewModel.processIntent(MapIntent.GetDirections(location))
                },
                onAddToTripClick = {
                    viewModel.processIntent(MapIntent.AddToTrip(location))
                },
                onDismiss = {
                    viewModel.processIntent(MapIntent.DismissSelectedLocation)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }
    }
}
```

### Map ViewModel

```kotlin
class MapViewModel(
    private val getPOIsUseCase: GetPOIsUseCase,
    private val getRouteUseCase: GetRouteUseCase,
    private val locationTracker: LocationTracker,
    private val searchPlacesUseCase: SearchPlacesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(MapState())
    val state = _state.asStateFlow()
    
    private val _effect = Channel<MapEffect>()
    val effect = _effect.receiveAsFlow()
    
    init {
        viewModelScope.launch {
            // Start tracking location
            locationTracker.locationFlow.collect { location ->
                _state.update { it.copy(currentLocation = location) }
                
                // Center map on location if auto-center is enabled
                if (it.autoCenterEnabled && it.initialLocationSet.not()) {
                    _state.update { it.copy(
                        center = location.toLatLng(),
                        initialLocationSet = true
                    )}
                }
            }
        }
    }
    
    fun processIntent(intent: MapIntent) {
        when (intent) {
            is MapIntent.CenterOnLocation -> centerOnLocation()
            is MapIntent.ChangeMapType -> changeMapType(intent.mapType)
            is MapIntent.UpdateSearchQuery -> updateSearchQuery(intent.query)
            is MapIntent.PerformSearch -> performSearch()
            is MapIntent.SelectSearchResult -> selectSearchResult(intent.result)
            is MapIntent.MarkerClicked -> onMarkerClicked(intent.marker)
            is MapIntent.GetDirections -> getDirections(intent.location)
            is MapIntent.AddToTrip -> addToTrip(intent.location)
            is MapIntent.DismissSelectedLocation -> dismissSelectedLocation()
            is MapIntent.CameraMoved -> onCameraMoved(intent.center, intent.zoomLevel)
            is MapIntent.MapClicked -> onMapClicked(intent.location)
        }
    }
    
    private fun centerOnLocation() {
        viewModelScope.launch {
            val hasPermission = locationTracker.hasLocationPermission()
            
            if (hasPermission) {
                _state.value.currentLocation?.let { location ->
                    _state.update { it.copy(
                        center = location.toLatLng(),
                        zoomLevel = 15f
                    )}
                } ?: run {
                    locationTracker.getCurrentLocation()?.let { location ->
                        _state.update { it.copy(
                            center = location.toLatLng(),
                            zoomLevel = 15f
                        )}
                    }
                }
            } else {
                _effect.send(MapEffect.RequestLocationPermission)
            }
        }
    }
    
    private fun performSearch() {
        viewModelScope.launch {
            val query = _state.value.searchQuery
            if (query.isBlank()) return@launch
            
            _state.update { it.copy(isSearching = true) }
            
            searchPlacesUseCase(
                query = query,
                center = _state.value.center,
                radius = 5000 // 5km radius
            ).onSuccess { results ->
                _state.update { it.copy(
                    searchResults = results,
                    isSearching = false
                )}
            }.onFailure { error ->
                _state.update { it.copy(isSearching = false) }
                _effect.send(MapEffect.ShowError(
                    error.message ?: "Search failed"
                ))
            }
        }
    }
    
    private fun getDirections(location: Location) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val currentLocation = _state.value.currentLocation
                ?: locationTracker.getCurrentLocation()
            
            if (currentLocation != null) {
                getRouteUseCase(
                    origin = currentLocation,
                    destination = location,
                    mode = TransportMode.DRIVING
                ).onSuccess { route ->
                    _state.update { it.copy(
                        routes = listOf(route),
                        isLoading = false
                    )}
                }.onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(MapEffect.ShowError(
                        error.message ?: "Failed to get directions"
                    ))
                }
            } else {
                _state.update { it.copy(isLoading = false) }
                _effect.send(MapEffect.RequestLocationPermission)
            }
        }
    }
}

data class MapState(
    val center: LatLng = LatLng(0.0, 0.0),
    val zoomLevel: Float = 10f,
    val mapType: MapType = MapType.STANDARD,
    val markers: List<Marker> = emptyList(),
    val routes: List<Route> = emptyList(),
    val currentLocation: Location? = null,
    val initialLocationSet: Boolean = false,
    val autoCenterEnabled: Boolean = true,
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val selectedLocation: Location? = null,
    val isSearching: Boolean = false,
    val isLoading: Boolean = false
)

sealed interface MapIntent {
    object CenterOnLocation : MapIntent
    data class ChangeMapType(val mapType: MapType) : MapIntent
    data class UpdateSearchQuery(val query: String) : MapIntent
    object PerformSearch : MapIntent
    data class SelectSearchResult(val result: SearchResult) : MapIntent
    data class MarkerClicked(val marker: Marker) : MapIntent
    data class GetDirections(val location: Location) : MapIntent
    data class AddToTrip(val location: Location) : MapIntent
    object DismissSelectedLocation : MapIntent
    data class CameraMoved(val center: LatLng, val zoomLevel: Float) : MapIntent
    data class MapClicked(val location: LatLng) : MapIntent
}

sealed interface MapEffect {
    object RequestLocationPermission : MapEffect
    data class NavigateToDirections(val route: Route) : MapEffect
    data class ShareLocation(val location: Location) : MapEffect
    data class ShowError(val message: String) : MapEffect
}

enum class MapType {
    STANDARD, SATELLITE, TERRAIN, HYBRID
}
```