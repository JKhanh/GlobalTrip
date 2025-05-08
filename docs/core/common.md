# Common Module

The Common module provides shared utility classes, extensions, and helper functions used throughout the GlobalTrip application across all platforms.

## Features

- Platform-agnostic utility functions and extension methods
- Common data structures and type definitions
- Resource management utilities
- Date and time formatting utilities
- String manipulation and validation helpers
- Collection extension functions
- Logging infrastructure
- Result handling wrappers

## Functional Requirements

### Utility Functions

- Provide extension functions for standard Kotlin types (String, Int, Collection, etc.)
- Implement date/time utilities with proper timezone handling
- Create safe type conversion methods
- Provide standardized result handling and error wrappers

### Resource Management

- Define resource access abstractions for different platforms
- Implement proper localization utilities
- Provide consistent asset access methods

### Logging

- Implement a cross-platform logging system
- Support different log levels (DEBUG, INFO, WARNING, ERROR)
- Provide context-aware logging extensions
- Include optional crash reporting integration

## Dependencies

### Kotlin Standard Library

- `kotlin-stdlib-common`: Core Kotlin standard library for common code
- `kotlinx-datetime`: Cross-platform date/time library
- `kotlinx-coroutines-core`: Coroutines for asynchronous programming

### Testing

- `kotlin-test`: Common testing library
- `kotlin-test-annotations-common`: Common test annotations
- `kotlinx-coroutines-test`: Testing utilities for coroutines

## Usage Examples

### String Extensions

```kotlin
// Validate email address
val isValid = "user@example.com".isValidEmail()

// Create initials from full name
val initials = "John Doe".toInitials() // "JD"
```

### Date Formatting

```kotlin
// Format date for display
val formattedDate = LocalDate(2025, 5, 1).formatForUser(DateFormat.MEDIUM)

// Check if date is in the future
val isFuture = LocalDateTime.now().plusDays(1).isFuture()
```

### Result Handling

```kotlin
// Using the Result wrapper
fun fetchData(): Result<Data, Error> {
    return try {
        Result.Success(repository.getData())
    } catch (e: Exception) {
        Result.Failure(Error.NetworkError(e.message))
    }
}

// Usage with when expression
when (val result = fetchData()) {
    is Result.Success -> handleData(result.data)
    is Result.Failure -> handleError(result.error)
}
```