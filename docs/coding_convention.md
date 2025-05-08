# GlobalTrip - Coding Convention

This document outlines the coding standards and best practices to be followed throughout the GlobalTrip project. Consistent adherence to these guidelines will ensure maintainability, readability, and security across the codebase.

## Table of Contents

1. [Kotlin Style Guide](#kotlin-style-guide)
2. [Architecture Guidelines](#architecture-guidelines)
3. [Testing Standards](#testing-standards)
4. [Compose UI Standards](#compose-ui-standards)
5. [KMP Platform-Specific Code](#kmp-platform-specific-code)
6. [Security Guidelines](#security-guidelines)
7. [Documentation](#documentation)
8. [Performance Considerations](#performance-considerations)

## Kotlin Style Guide

### Naming Conventions

- **Classes**: Use PascalCase (e.g., `TripRepository`, `MapViewModel`)
- **Functions/Properties**: Use camelCase (e.g., `getTripById()`, `userName`)
- **Constants**: Use UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`, `DEFAULT_TIMEOUT`)
- **Packages**: Use lowercase with dots (e.g., `com.globaltrip.feature.trips`)
- **Files**: Match the class name they contain (e.g., `TripRepository.kt`)

### Code Formatting

- Maximum line length: 120 characters
- Use 4 spaces for indentation, not tabs
- Put spaces around operators and after commas
- Place opening braces at the end of the line
- Use trailing commas in multi-line enumerations

### Language Features

- Prefer immutability: Use `val` over `var` when possible
- Utilize Kotlin's extension functions for utility methods
- Use type inference where it doesn't impact readability
- Prefer data classes for simple data holding structures
- Use sealed classes/interfaces for representing closed hierarchies
- Apply destructuring declarations where they improve readability
- Utilize higher-order functions for operations on collections
- Prefer non-nullable types; explicitly handle nullability where needed

### Coroutines & Flow

- Use structured concurrency principles
- Prefer `Flow` over callbacks for streaming data
- Use appropriate dispatchers (`IO`, `Default`, `Main`) based on the operation
- Properly handle exceptions in coroutine scopes
- Use `StateFlow` for UI state and `SharedFlow` for events

```kotlin
// ✅ Good Practice
private val _uiState = MutableStateFlow(UiState.Loading)
val uiState = _uiState.asStateFlow()

fun loadData() {
    viewModelScope.launch {
        try {
            _uiState.value = UiState.Loading
            val result = repository.getData()
            _uiState.value = UiState.Success(result)
        } catch (e: Exception) {
            _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
        }
    }
}

// ❌ Bad Practice
var uiState: UiState = UiState.Loading // Direct mutable state
    private set

fun loadData() {
    GlobalScope.launch { // Never use GlobalScope
        // No error handling
        uiState = UiState.Success(repository.getData())
    }
}
```

## Architecture Guidelines

### Clean Architecture with MVI

The GlobalTrip application follows Clean Architecture principles combined with the Model-View-Intent (MVI) pattern for UI state management:

1. **Presentation Layer**: UI components and ViewModels implementing MVI
2. **Domain Layer**: Business logic and use cases
3. **Data Layer**: Data sources, repositories, and models

### MVI Architecture

MVI (Model-View-Intent) is the core UI architecture pattern used throughout the application:

- **Model**: Represents the UI state (immutable data class)
- **View**: Renders the UI based on the current state (Composable functions)
- **Intent**: User actions or events that trigger state changes

The MVI flow follows a unidirectional data flow:
1. User generates intents (actions) through interaction with the UI
2. ViewModel processes intents and executes business logic (via use cases)
3. ViewModel updates the UI state
4. View observes and renders the new state
5. Cycle repeats

### Components by Layer

- **Presentation**:
  - UI State classes: Immutable data classes representing the current state of UI
  - Intent/Event classes: Sealed classes representing user actions or system events
  - Effect classes: Sealed classes representing one-time side effects (navigation, snackbars, etc.)
  - ViewModels: Process intents, manage UI state and side effects
  - UI Components: Composable functions that render UI based on state
  - Reducers: Pure functions that produce new state based on current state and intent

- **Domain**:
  - Use Cases: Encapsulate business logic operations
  - Domain Models: Core business entities
  - Repository Interfaces: Define data operations contracts

- **Data**:
  - Repository Implementations: Coordinate data sources
  - Data Sources: Remote (API) and local (Database) sources
  - Data Models: DTOs and entity classes
  - Mappers: Convert between data and domain models

### Dependency Injection

- Use Koin for dependency injection
- Define modules per feature or core component
- Inject dependencies through constructor injection
- Keep module definitions clean and organized

```kotlin
// ✅ Good Practice
val networkModule = module {
    single { provideHttpClient() }
    single<ApiService> { ApiServiceImpl(get()) }
}

val repositoryModule = module {
    single<TripRepository> { TripRepositoryImpl(get(), get()) }
}

// Constructor injection
class TripRepositoryImpl(
    private val apiService: ApiService,
    private val database: TripDatabase
) : TripRepository
```

```kotlin
// ✅ Good Practice - MVI Implementation
// 1. Define sealed interfaces for State, Intent and Effect
data class TripListState(
    val trips: List<Trip> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface TripListIntent {
    object LoadTrips : TripListIntent
    data class DeleteTrip(val tripId: String) : TripListIntent
    data class FilterTrips(val filter: TripFilter) : TripListIntent
}

sealed interface TripListEffect {
    data class ShowSnackbar(val message: String) : TripListEffect
    data class NavigateToTripDetail(val tripId: String) : TripListEffect
}

// 2. ViewModel implementation
class TripListViewModel(
    private val getTripListUseCase: GetTripListUseCase,
    private val deleteTripUseCase: DeleteTripUseCase
) : ViewModel() {
    // State management
    private val _state = MutableStateFlow(TripListState())
    val state = _state.asStateFlow()
    
    // Effect management
    private val _effect = Channel<TripListEffect>()
    val effect = _effect.receiveAsFlow()
    
    // Intent processing
    fun processIntent(intent: TripListIntent) {
        when (intent) {
            is TripListIntent.LoadTrips -> loadTrips()
            is TripListIntent.DeleteTrip -> deleteTrip(intent.tripId)
            is TripListIntent.FilterTrips -> filterTrips(intent.filter)
        }
    }
    
    private fun loadTrips() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            getTripListUseCase()
                .onSuccess { trips ->
                    _state.update { it.copy(
                        trips = trips,
                        isLoading = false
                    )}
                }
                .onFailure { error ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown error"
                    )}
                }
        }
    }
    
    private fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            deleteTripUseCase(tripId)
                .onSuccess {
                    _effect.send(TripListEffect.ShowSnackbar("Trip deleted successfully"))
                    loadTrips() // Reload trips after deletion
                }
                .onFailure { error ->
                    _effect.send(TripListEffect.ShowSnackbar(
                        "Failed to delete trip: ${error.message}"
                    ))
                }
        }
    }
}

// 3. UI implementation collecting state and processing intents
@Composable
fun TripListScreen(
    viewModel: TripListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    
    // Handle one-time effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TripListEffect.ShowSnackbar -> {
                    // Show snackbar
                }
                is TripListEffect.NavigateToTripDetail -> {
                    // Navigate to trip detail
                }
            }
        }
    }
    
    // Initial load of trips
    LaunchedEffect(Unit) {
        viewModel.processIntent(TripListIntent.LoadTrips)
    }
    
    // UI rendering based on state
    when {
        state.isLoading -> LoadingIndicator()
        state.error != null -> ErrorView(
            error = state.error,
            onRetry = { viewModel.processIntent(TripListIntent.LoadTrips) }
        )
        state.trips.isEmpty() -> EmptyTripsView(
            onCreateTrip = { /* Navigate to create trip */ }
        )
        else -> TripList(
            trips = state.trips,
            onTripClick = { tripId ->
                viewModel.processIntent(TripListIntent.NavigateToDetail(tripId))
            },
            onDeleteTrip = { tripId ->
                viewModel.processIntent(TripListIntent.DeleteTrip(tripId))
            }
        )
    }
}

// ❌ Bad Practice - Not following MVI
class BadPracticeViewModel : ViewModel() {
    // Direct mutable state
    var trips = mutableStateListOf<Trip>()
        private set
    
    // No proper state management
    var isLoading = false
        private set
        
    // No error handling
    fun loadTrips() {
        viewModelScope.launch {
            isLoading = true
            trips.clear()
            trips.addAll(repository.getTrips())
            isLoading = false
        }
    }
    
    // No effect handling
    fun deleteTrip(id: String) {
        viewModelScope.launch {
            repository.deleteTrip(id)
            // Direct UI side effect - not proper MVI
            Toast.makeText(context, "Trip deleted", Toast.LENGTH_SHORT).show()
        }
    }
}
```

## Testing Standards

### Test Types

- **Unit Tests**: Test individual functions/classes in isolation
- **Integration Tests**: Test interaction between components
- **UI Tests**: Test UI components and user flows
- **End-to-End Tests**: Test complete features

### Testing Guidelines

- Every module should have at least 70% test coverage
- Use descriptive test names that explain what's being tested
- Follow the AAA pattern (Arrange, Act, Assert)
- Use descriptive error messages in assertions
- Mock dependencies using MockK
- Use test fixtures for common test data
- Separate test implementation from test data

```kotlin
// ✅ Good Practice
@Test
fun `getTripById returns trip when trip exists`() {
    // Arrange
    val tripId = "trip123"
    val expectedTrip = Trip(id = tripId, name = "Summer Vacation")
    coEvery { tripDataSource.getTripById(tripId) } returns expectedTrip
    
    // Act
    val result = runBlocking { tripRepository.getTripById(tripId) }
    
    // Assert
    assertThat(result).isEqualTo(expectedTrip)
}
```

## Compose UI Standards

### UI Components

- Keep Composable functions focused on a single responsibility
- Extract reusable components to the `designsystem` module
- Follow the `Stateless UI Components` pattern where possible
- Use the remember API thoughtfully to avoid unnecessary recompositions
- Follow standard Compose lifecycle best practices

### Theming

- Use the MaterialTheme for consistent styling
- Define and use design tokens (colors, typography, shapes) from the `theme` package
- Never hardcode colors, sizes, or typography - use theme values
- Design with accessibility in mind (contrast ratios, touch target sizes)

```kotlin
// ✅ Good Practice
@Composable
fun TripCard(
    trip: Trip,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = trip.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = trip.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ❌ Bad Practice
@Composable
fun TripCard(trip: Trip, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // Hardcoded color
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = trip.name,
                fontSize = 18.sp, // Hardcoded font size
                fontWeight = FontWeight.Bold,
                color = Color(0xFF000000) // Hardcoded color
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = trip.description,
                fontSize = 14.sp, // Hardcoded font size
                color = Color(0xFF666666) // Hardcoded color
            )
        }
    }
}
```

## KMP Platform-Specific Code

### Expect/Actual Pattern

- Use the expect/actual pattern for platform-specific implementations
- Keep platform-specific code to a minimum
- Place expect declarations in `commonMain` and actual implementations in platform-specific source sets

```kotlin
// In commonMain
expect class LocationProvider {
    fun getCurrentLocation(): Flow<Location>
}

// In androidMain
actual class LocationProvider {
    actual fun getCurrentLocation(): Flow<Location> {
        // Android-specific implementation
    }
}

// In iosMain
actual class LocationProvider {
    actual fun getCurrentLocation(): Flow<Location> {
        // iOS-specific implementation
    }
}
```

### Platform Compatibility

- Use common interfaces with platform-specific implementations
- Avoid passing platform-specific types across module boundaries
- Use the `@OptIn` annotation for experimental KMP features with care

## Security Guidelines

### Data Protection

- Never store sensitive data in plain text
- Use secure storage for credentials and tokens
- Apply encryption for sensitive local data
- Implement proper session management and token refresh

### Network Security

- Use HTTPS for all network communication
- Implement certificate pinning for critical API endpoints
- Don't log sensitive information
- Validate all user inputs and API responses

### Authentication

- Use modern authentication protocols (OAuth 2.0, JWT)
- Implement secure token storage
- Use biometric authentication when available
- Apply proper session timeouts

```kotlin
// ✅ Good Practice
class SecureTokenStorage(private val context: Context) {
    private val encryptedSharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            "secure_prefs",
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun storeToken(token: String) {
        encryptedSharedPreferences.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        return encryptedSharedPreferences.getString("auth_token", null)
    }
}
```

## Documentation

### Code Documentation

- Document all public APIs with KDoc comments
- Include examples for complex functions
- Document parameters, return types, and exceptions
- Update documentation when code changes

```kotlin
/**
 * Fetches trip details from remote and local sources.
 *
 * This function first attempts to load data from the local database.
 * If local data is unavailable or stale, it fetches from the remote API
 * and then updates the local database.
 *
 * @param tripId The unique identifier of the trip
 * @return A flow emitting the trip data
 * @throws TripNotFoundException If the trip cannot be found
 */
fun getTripDetails(tripId: String): Flow<Trip>
```

### Architecture Documentation

- Maintain README files for each module
- Document key decisions in Architecture Decision Records (ADRs)
- Keep diagrams updated to reflect the current architecture

## Performance Considerations

### General Performance

- Avoid blocking the main thread
- Use appropriate data structures and algorithms
- Implement pagination for large data sets
- Optimize database queries

### Compose Performance

- Minimize recompositions using proper keys and stable state
- Use LaunchedEffect and rememberCoroutineScope appropriately
- Apply layoutModifier instead of layout when possible
- Use derivedStateOf for computed state

### Memory Management

- Avoid memory leaks by properly cleaning up resources
- Use weak references when appropriate
- Don't store large objects in memory when not needed
- Profile memory consumption regularly