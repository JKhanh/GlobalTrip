# Database Module

The Database module handles local data persistence for the GlobalTrip application using SQLDelight across all supported platforms.

## Features

- Cross-platform local database implementation 
- Platform-specific SQLite drivers
- Type-safe SQL queries with SQLDelight
- Data access objects (DAOs) for all entities
- Database schema migration management
- Encrypted storage for sensitive data

## Functional Requirements

### Database Schema

- Define clear and efficient SQLDelight schema for all entities
- Implement proper relationships between entities
- Create appropriate indices for query optimization
- Support versioned schema migrations

### Data Access

- Provide DAOs (Data Access Objects) for all entities
- Support CRUD operations for all entities
- Implement efficient queries for common operations
- Support transaction handling
- Return Kotlin Flow for reactive data observation

### Platform-specific Implementations

- Android: Use AndroidSqliteDriver
- iOS: Use NativeSqliteDriver
- Web/JS: Use WebSqlDriver or SqlJs driver

### Security

- Implement encryption for sensitive user data
- Provide secure key management
- Support data sanitization and validation

## Dependencies

### SQLDelight

- `com.squareup.sqldelight:runtime`: Core SQLDelight runtime
- `com.squareup.sqldelight:coroutines-extensions`: Flow extensions for SQLDelight
- `com.squareup.sqldelight:android-driver`: Android SQLite driver
- `com.squareup.sqldelight:native-driver`: iOS SQLite driver
- `com.squareup.sqldelight:sqljs-driver`: Web SQLite driver

### Security

- `net.zetetic:android-database-sqlcipher`: SQLCipher for database encryption (Android)
- `co.touchlab:stately-common`: Thread safety for multiplatform code

### Testing

- `com.squareup.sqldelight:sqlite-driver`: In-memory SQLite driver for testing
- `kotlin-test`: Kotlin testing library

## Schema Example

```sql
-- trips.sq
CREATE TABLE trip (
    id TEXT NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    start_date TEXT NOT NULL,
    end_date TEXT NOT NULL,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    sync_status TEXT NOT NULL
);

CREATE INDEX trip_start_date ON trip(start_date);
CREATE INDEX trip_end_date ON trip(end_date);

-- Query to get all trips ordered by start date
getAllTrips:
SELECT *
FROM trip
ORDER BY start_date DESC;

-- Query to get trip by id
getTripById:
SELECT *
FROM trip
WHERE id = ?;

-- Insert or replace a trip
upsertTrip:
INSERT OR REPLACE INTO trip(id, name, description, start_date, end_date, created_at, updated_at, sync_status)
VALUES (?, ?, ?, ?, ?, ?, ?, ?);

-- Delete a trip
deleteTrip:
DELETE FROM trip
WHERE id = ?;

-- Get trips with pending sync
getPendingTrips:
SELECT *
FROM trip
WHERE sync_status != 'SYNCED';
```

## Usage Examples

### Database Setup

```kotlin
// Common database factory
class DatabaseFactory(private val driverFactory: DriverFactory) {
    fun createDatabase(): AppDatabase {
        val driver = driverFactory.createDriver()
        return AppDatabase(
            driver = driver,
            tripAdapter = Trip.Adapter(
                created_atAdapter = DateLongAdapter,
                updated_atAdapter = DateLongAdapter
            )
        )
    }
}

// Platform-specific driver factory for Android
actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = context,
            name = "globaltrip.db"
        )
    }
}

// Platform-specific driver factory for iOS
actual class DriverFactory() {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = AppDatabase.Schema,
            name = "globaltrip.db"
        )
    }
}
```

### DAO Implementation

```kotlin
class TripDao(private val database: AppDatabase) {
    private val queries = database.tripQueries

    // Get all trips as Flow
    fun getAllTrips(): Flow<List<Trip>> = queries
        .getAllTrips()
        .asFlow()
        .mapToList()

    // Get trip by ID
    suspend fun getTripById(id: String): Trip? = withContext(Dispatchers.IO) {
        queries.getTripById(id).executeAsOneOrNull()
    }

    // Insert or update a trip
    suspend fun upsertTrip(trip: Trip) = withContext(Dispatchers.IO) {
        queries.upsertTrip(
            id = trip.id,
            name = trip.name,
            description = trip.description,
            start_date = trip.startDate,
            end_date = trip.endDate,
            created_at = trip.createdAt,
            updated_at = trip.updatedAt,
            sync_status = trip.syncStatus
        )
    }
    
    // Delete a trip
    suspend fun deleteTrip(id: String) = withContext(Dispatchers.IO) {
        queries.deleteTrip(id)
    }
    
    // Get trips with pending sync
    fun getPendingTrips(): Flow<List<Trip>> = queries
        .getPendingTrips()
        .asFlow()
        .mapToList()
}
```