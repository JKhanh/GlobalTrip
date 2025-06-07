# GlobalTrip

A Kotlin Multiplatform (KMP) travel planning application that works across Android, iOS, and Web platforms.

## Trip List Feature Implementation

The Trip List feature allows users to view their trips categorized as upcoming, past, or archived. The implementation follows an MVI (Model-View-Intent) architecture pattern and consists of:

### Domain Layer

- **Trip Model**: Represents a trip with properties like title, dates, destination, etc.
- **TripRepository**: Interface for CRUD operations on trips
- **GetTripsUseCase**: Use case that retrieves all trips for the current user

### Data Layer

- **MockTripRepository**: A mock implementation of the TripRepository for development purposes

### UI Layer

- **TripListViewModel**: Manages the state of the trip list screen, handling filtering, archiving, and other operations
- **TripListState**: Data class representing the UI state for the trip list
- **TripListScreen**: Composable function that displays the trip list with filters and stats
- **TripCard**: Card component for displaying a trip summary
- **GTFilterChip**: Reusable filter chip component

### Tests

- **TripListViewModelTest**: Tests for the TripListViewModel
- **TripCardTest**: Tests for the TripCard composable

## Getting Started

1. Clone the repository
2. Open the project in Android Studio
3. Run on your preferred platform (Android, iOS, or Web)

## Architecture

The app follows a clean architecture approach with:

- **Feature-based modularization**: Each feature (trips, auth, maps, etc.) is a separate module
- **Core modules**: Shared infrastructure code (database, network, common utilities)
- **MVI pattern**: Each screen follows the Model-View-Intent pattern
- **Compose Multiplatform**: UI is built with Compose for all platforms

## Technologies Used

- Kotlin Multiplatform
- Compose Multiplatform for UI
- Kotlinx Datetime for date handling
- KMP Testing for cross-platform tests
