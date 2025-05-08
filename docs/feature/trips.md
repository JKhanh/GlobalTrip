# Trips Feature Module

The Trips module is the core feature of the GlobalTrip application, enabling users to create, manage, and view their travel itineraries across all supported platforms.

## Features

- Trip creation and editing
- Trip timeline and itinerary management
- Destination management with point of interest suggestions
- Trip sharing and collaboration
- Trip statistics and insights
- Offline access to trip details
- Photo and document attachment
- Trip templates and recommendations
- Calendar integration

## Functional Requirements

### Trip Management

- Create, update, and delete trips
- Define trip date range and destinations
- Add activities, accommodations, and transportation
- Attach documents (tickets, reservations, etc.)
- Manage trip status (planning, active, completed)
- Track trip costs and budget
- Support offline editing with synchronization

### Trip Timeline

- View trip itinerary in chronological order
- Display daily agenda with activities and events
- Show transportation and accommodation changes
- Support time-based notifications and reminders
- Export itinerary to calendar

### Collaboration

- Share trips with fellow travelers
- Define collaboration roles and permissions
- Support real-time updates and notifications
- Track edits and changes by collaborators

## MVI Implementation

The Trips module follows the Model-View-Intent (MVI) architecture pattern:

### States

- `TripListState`: Represents list of trips with filters and sorting
- `TripDetailState`: Detailed information for a specific trip
- `TripCreationState`: Form state for creating/editing trips
- `TripTimelineState`: Chronological view of trip activities

### Intents

- `TripListIntent`: Actions for the trip list (load, filter, sort)
- `TripDetailIntent`: Actions for viewing and modifying trip details
- `TripCreationIntent`: Trip creation and editing actions
- `TripTimelineIntent`: Timeline manipulation actions

### Effects

- Navigation between trip screens
- Share trip with external apps
- Download trip documents
- Calendar integration effects

## Dependencies

### Core Dependencies

- `core:domain`: Domain models and use cases
- `core:data`: Repository implementations
- `core:database`: Local storage
- `core:network`: API communication
- `core:common`: Common utilities
- `core:ui`: UI components

### Feature Dependencies

- `feature:maps`: Maps integration for destinations
- `feature:expenses`: Expense tracking within trips
- `feature:collaboration`: Sharing and collaboration

### UI Components

- Calendar and date range pickers
- Timeline visualization components
- Map components for destinations
- Photo gallery and document viewers

## Implementation Examples

### Trip Creation Flow

```kotlin
@Composable
fun TripCreationScreen(
    viewModel: TripCreationViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TripCreationEffect.NavigateBack -> onNavigateBack()
                is TripCreationEffect.ShowError -> {
                    // Show error message
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Trip") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.processIntent(TripCreationIntent.SaveTrip)
                        },
                        enabled = state.isFormValid && !state.isSubmitting
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Trip name
            OutlinedTextField(
                value = state.name,
                onValueChange = { name ->
                    viewModel.processIntent(TripCreationIntent.UpdateName(name))
                },
                label = { Text("Trip Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.nameError != null,
                supportingText = {
                    state.nameError?.let { Text(it) }
                }
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Date range selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Start date picker
                DatePicker(
                    label = "Start Date",
                    date = state.startDate,
                    onDateSelected = { date ->
                        viewModel.processIntent(TripCreationIntent.UpdateStartDate(date))
                    },
                    error = state.dateError,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(Modifier.width(16.dp))
                
                // End date picker
                DatePicker(
                    label = "End Date",
                    date = state.endDate,
                    onDateSelected = { date ->
                        viewModel.processIntent(TripCreationIntent.UpdateEndDate(date))
                    },
                    error = state.dateError,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Destination selection
            Text(
                text = "Destinations",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(Modifier.height(8.dp))
            
            state.destinations.forEachIndexed { index, destination ->
                DestinationItem(
                    destination = destination,
                    onRemove = {
                        viewModel.processIntent(
                            TripCreationIntent.RemoveDestination(index)
                        )
                    },
                    onEdit = {
                        viewModel.processIntent(
                            TripCreationIntent.EditDestination(index)
                        )
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
            
            GlobalTripButton(
                text = "Add Destination",
                onClick = {
                    viewModel.processIntent(TripCreationIntent.AddDestination)
                },
                buttonType = ButtonType.OUTLINED,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Additional fields for description, trip type, etc.
        }
    }
    
    // Show the loading indicator when submitting
    if (state.isSubmitting) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
```

### Trip Creation ViewModel

```kotlin
class TripCreationViewModel(
    private val createTripUseCase: CreateTripUseCase,
    private val validateTripUseCase: ValidateTripUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(TripCreationState())
    val state = _state.asStateFlow()
    
    private val _effect = Channel<TripCreationEffect>()
    val effect = _effect.receiveAsFlow()
    
    fun processIntent(intent: TripCreationIntent) {
        when (intent) {
            is TripCreationIntent.UpdateName -> updateName(intent.name)
            is TripCreationIntent.UpdateStartDate -> updateStartDate(intent.date)
            is TripCreationIntent.UpdateEndDate -> updateEndDate(intent.date)
            is TripCreationIntent.AddDestination -> addDestination()
            is TripCreationIntent.RemoveDestination -> removeDestination(intent.index)
            is TripCreationIntent.EditDestination -> editDestination(intent.index)
            is TripCreationIntent.SaveTrip -> saveTrip()
        }
    }
    
    private fun updateName(name: String) {
        _state.update { it.copy(name = name) }
        validateForm()
    }
    
    private fun updateStartDate(date: LocalDate) {
        _state.update { it.copy(startDate = date) }
        validateForm()
    }
    
    private fun updateEndDate(date: LocalDate) {
        _state.update { it.copy(endDate = date) }
        validateForm()
    }
    
    private fun validateForm() {
        val validationResult = validateTripUseCase(
            name = _state.value.name,
            startDate = _state.value.startDate,
            endDate = _state.value.endDate,
            destinations = _state.value.destinations
        )
        
        _state.update { it.copy(
            nameError = validationResult.nameError,
            dateError = validationResult.dateError,
            destinationError = validationResult.destinationError,
            isFormValid = validationResult.isValid
        )}
    }
    
    private fun saveTrip() {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            
            val trip = Trip(
                id = UUID.randomUUID().toString(),
                name = _state.value.name,
                startDate = _state.value.startDate,
                endDate = _state.value.endDate,
                destinations = _state.value.destinations,
                // Other trip properties
            )
            
            createTripUseCase(trip)
                .onSuccess {
                    _effect.send(TripCreationEffect.NavigateBack)
                }
                .onFailure { error ->
                    _state.update { it.copy(isSubmitting = false) }
                    _effect.send(TripCreationEffect.ShowError(
                        error.message ?: "Failed to create trip"
                    ))
                }
        }
    }
}

data class TripCreationState(
    val name: String = "",
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now().plusDays(7),
    val destinations: List<Destination> = emptyList(),
    val nameError: String? = null,
    val dateError: String? = null,
    val destinationError: String? = null,
    val isSubmitting: Boolean = false,
    val isFormValid: Boolean = false
)

sealed interface TripCreationIntent {
    data class UpdateName(val name: String) : TripCreationIntent
    data class UpdateStartDate(val date: LocalDate) : TripCreationIntent
    data class UpdateEndDate(val date: LocalDate) : TripCreationIntent
    object AddDestination : TripCreationIntent
    data class RemoveDestination(val index: Int) : TripCreationIntent
    data class EditDestination(val index: Int) : TripCreationIntent
    object SaveTrip : TripCreationIntent
}

sealed interface TripCreationEffect {
    object NavigateBack : TripCreationEffect
    data class ShowError(val message: String) : TripCreationEffect
}
```