# Domain Module

The Domain module contains the core business logic for the GlobalTrip application, independent of any UI or data source implementation. It defines the core entities, use cases, and repository interfaces.

## Features

- Business domain entities
- Use case definitions
- Repository interfaces
- Business validation rules
- Domain-specific exception types
- Business logic utilities
- Events and domain services

## Functional Requirements

### Domain Entities

- Define immutable domain models for all core entities
- Implement domain validation logic
- Create value objects for complex types
- Define rich domain objects with behavior
- Implement proper equality and hashing
- Support serialization for network/database

### Use Cases

- Implement single-responsibility use cases
- Support both synchronous and asynchronous operations
- Handle proper error cases
- Define clear input/output boundaries
- Support coroutines and Flow for reactive operations
- Implement proper validation logic

### Repository Interfaces

- Define repository interfaces for data layer
- Support both cached and fresh data retrieval
- Allow reactive data access with Flow
- Define proper error handling strategies
- Support CRUD operations
- Enable search and filtering

## MVI Implementation Support

The Domain module provides the foundation for MVI implementation used throughout the app:

- **Models**: Domain entities used to construct UI states
- **Intent handlers**: Use cases that execute business logic in response to intents
- **State changes**: Result objects returned by use cases to update UI state

## Dependencies

### Kotlin Libraries

- `kotlin-stdlib`: Kotlin standard library
- `kotlinx-coroutines-core`: Coroutines for async operations
- `kotlinx-datetime`: Date and time handling
- `kotlinx-serialization-core`: Serialization support

### Testing

- `kotlin-test`: Testing library
- `kotlinx-coroutines-test`: Testing utilities for coroutines

## Implementation Examples

### Domain Entity

```kotlin
data class Trip(
    val id: String,
    val name: String,
    val description: String? = null,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val destinations: List<Destination> = emptyList(),
    val participants: List<Participant> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val activities: List<Activity> = emptyList(),
    val createdAt: Instant,
    val updatedAt: Instant,
    val createdBy: String
) {
    init {
        require(name.isNotBlank()) { "Trip name cannot be empty" }
        require(endDate >= startDate) { "End date must be after or equal to start date" }
    }
    
    val duration: Int
        get() = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
        
    val isActive: Boolean
        get() {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            return today in startDate..endDate
        }
        
    val isPast: Boolean
        get() {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            return endDate < today
        }
        
    val isFuture: Boolean
        get() {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            return startDate > today
        }
    
    fun getTotalExpense(): Money {
        return expenses.fold(Money.ZERO) { total, expense ->
            total + expense.amount
        }
    }
    
    fun getActivitiesForDay(date: LocalDate): List<Activity> {
        return activities.filter { it.date == date }
    }
    
    fun addDestination(destination: Destination): Trip {
        return copy(
            destinations = destinations + destination,
            updatedAt = Clock.System.now()
        )
    }
    
    fun addParticipant(participant: Participant): Trip {
        return copy(
            participants = participants + participant,
            updatedAt = Clock.System.now()
        )
    }
}
```

### Repository Interface

```kotlin
interface TripRepository {
    /**
     * Get all trips for the current user.
     * 
     * @return A flow emitting the list of trips, which updates when trips change.
     */
    fun getTrips(): Flow<List<Trip>>
    
    /**
     * Get a specific trip by ID.
     * 
     * @param id The trip ID
     * @return The trip, or null if not found
     * @throws TripNotFoundException If the trip was not found
     */
    suspend fun getTripById(id: String): Trip
    
    /**
     * Create a new trip or update an existing one.
     * 
     * @param trip The trip to save
     * @return The saved trip with updated fields (like ID for new trips)
     * @throws TripValidationException If the trip fails validation
     * @throws NetworkException If there was a network error
     */
    suspend fun saveTrip(trip: Trip): Trip
    
    /**
     * Delete a trip by ID.
     * 
     * @param id The trip ID
     * @throws TripNotFoundException If the trip was not found
     * @throws PermissionDeniedException If the user doesn't have permission to delete
     */
    suspend fun deleteTrip(id: String)
    
    /**
     * Search for trips matching the given criteria.
     * 
     * @param query The search query
     * @param filters Optional filters to apply
     * @return A list of matching trips
     */
    suspend fun searchTrips(query: String, filters: TripFilters = TripFilters()): List<Trip>
    
    /**
     * Get trips shared with the current user.
     * 
     * @return A flow emitting shared trips
     */
    fun getSharedTrips(): Flow<List<Trip>>
}
```

### Use Case

```kotlin
class CreateTripUseCase(
    private val tripRepository: TripRepository,
    private val tripValidator: TripValidator,
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    /**
     * Create a new trip with the provided data.
     * 
     * @param name The name of the trip
     * @param description Optional description
     * @param startDate Start date of the trip
     * @param endDate End date of the trip
     * @param destinations List of destinations
     * @return Result containing the created trip or an error
     */
    suspend operator fun invoke(
        name: String,
        description: String? = null,
        startDate: LocalDate,
        endDate: LocalDate,
        destinations: List<Destination> = emptyList()
    ): Result<Trip> = withContext(dispatcher) {
        try {
            // Validate input
            val validationResult = tripValidator.validateTrip(
                name = name,
                startDate = startDate,
                endDate = endDate,
                destinations = destinations
            )
            
            if (!validationResult.isValid) {
                return@withContext Result.failure(
                    TripValidationException(validationResult.errors)
                )
            }
            
            // Get current user
            val currentUser = userRepository.getCurrentUser()
                ?: return@withContext Result.failure(
                    AuthenticationException("User not authenticated")
                )
            
            // Create trip entity
            val now = Clock.System.now()
            val trip = Trip(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                startDate = startDate,
                endDate = endDate,
                destinations = destinations,
                createdAt = now,
                updatedAt = now,
                createdBy = currentUser.id
            )
            
            // Save to repository
            val savedTrip = tripRepository.saveTrip(trip)
            
            Result.success(savedTrip)
        } catch (e: Exception) {
            when (e) {
                is TripValidationException -> Result.failure(e)
                is NetworkException -> Result.failure(e)
                else -> Result.failure(
                    UnexpectedDomainException("Failed to create trip", e)
                )
            }
        }
    }
}
```

### Domain Exception

```kotlin
sealed class DomainException(message: String, cause: Throwable? = null) : 
    Exception(message, cause)

class TripNotFoundException(tripId: String) : 
    DomainException("Trip with ID $tripId not found")

class TripValidationException(val errors: List<ValidationError>) : 
    DomainException("Trip validation failed: ${errors.joinToString()}")

class AuthenticationException(message: String) : 
    DomainException(message)

class PermissionDeniedException(message: String) : 
    DomainException(message)

class NetworkException(message: String, cause: Throwable? = null) : 
    DomainException(message, cause)

class UnexpectedDomainException(message: String, cause: Throwable? = null) : 
    DomainException(message, cause)
```