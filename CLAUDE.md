# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Common Development Commands

```bash
# Build the project
./gradlew build

# Run tests for all platforms
./gradlew allTests

# Run unit tests
./gradlew test

# Run Android tests
./gradlew connectedAndroidTest

# Run iOS tests  
./gradlew iosSimulatorArm64Test

# Run checks (includes tests, lint, etc.)
./gradlew check

# Clean build artifacts
./gradlew clean

# Run Android app
./gradlew :composeApp:installDebug

# Build iOS app (requires macOS with Xcode)
cd iosApp && xcodebuild -project iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'
```

## High-Level Architecture

GlobalTrip is a Kotlin Multiplatform (KMP) travel planning application using Clean Architecture with MVI pattern:

### Architecture Layers

1. **Presentation Layer (MVI Pattern)**
   - **State**: Immutable data classes representing UI state
   - **Intent**: Sealed interfaces for user actions/events  
   - **Effect**: One-time side effects (navigation, snackbars)
   - **ViewModels**: Process intents and manage state following unidirectional data flow
   - **Composables**: Render UI based on state

2. **Domain Layer**
   - Use Cases encapsulate business logic
   - Domain models represent core entities
   - Repository interfaces define data contracts

3. **Data Layer**
   - Repository implementations coordinate data sources
   - SQLDelight for local database (type-safe SQL)
   - Platform-specific implementations via expect/actual

### Key Technologies

- **UI**: Compose Multiplatform (shared UI across all platforms)
- **DI**: Koin 4.0.3 for dependency injection with verify() API
- **Authentication**: Supabase Auth (email/password, OAuth)
- **Database**: SQLDelight with platform-specific drivers
- **Navigation**: JetBrains Compose Navigation
- **Async**: Coroutines and Flow for reactive programming

### Dependency Injection (Koin)

**Koin Module Structure:**
- **NetworkModule**: Supabase client configuration
- **CoreModule**: Storage and basic services (SecureStorage, TripRepository)
- **RepositoryModule**: Repository implementations (AuthRepository)
- **AuthModule**: Authentication use cases and ViewModels
- **TripsModule**: Trip management use cases and ViewModels
- **SettingsModule**: App settings repository and ViewModel

**Usage in Composables:**
```kotlin
@Composable
fun MyScreen() {
    val viewModel: MyViewModel = koinInject()
    // Use viewModel...
}
```

**Initialization:**
- **Android**: `GlobalTripApplication.onCreate()` calls `initKoin()`
- **iOS**: `MainViewController()` calls `initKoin()`
- **Tests**: Use `globalTripAppModule.verify()` for DI validation

### Module Structure

- **Feature modules** (`feature/trips`, `feature/auth`, etc.) - isolated features
- **Core modules** (`core/domain`, `core/data`, `core/ui`) - shared infrastructure
- **Platform source sets** (`androidMain`, `iosMain`, `wasmJsMain`) - platform code
- **DI modules** (`di/*.kt`) - Koin dependency injection configuration

### Testing Strategy

- Unit tests for ViewModels and business logic
- Integration tests for repositories  
- UI tests for Composables
- DI verification tests using Koin's `verify()` API
- Follow AAA pattern with descriptive naming
- Target 70% coverage per module

## Code Deletion Guidelines

**CRITICAL**: Before deleting any Kotlin files, always check for references in platform-specific code:

1. **Check Swift/iOS references**: Search `iosApp/` directory for any Swift files that might import or call the Kotlin code
   - Look for `import ComposeApp` statements
   - Search for Kotlin class/function names being called from Swift
   - Check `MainViewController`, `ContentView.swift`, and other entry points

2. **Check Android references**: Search `androidMain/` for any Android-specific code that references the Kotlin files
   - Look for custom Android implementations
   - Check manifest files and Android-specific configurations

3. **Check WASM references**: Search `wasmJsMain/` for any WASM-specific implementations
   - Platform-specific repository implementations
   - WASM entry points and configurations

4. **Cross-platform search**: Use global search to find all references before deletion
   - Search the entire codebase for class names, function names, and file imports
   - Pay special attention to `expect/actual` declarations

**Remember**: Kotlin Multiplatform generates native frameworks for each platform. Deleting a Kotlin file that's referenced by platform code will break the build, even if it appears unused in Kotlin.

## Git Commit Rules

**CRITICAL RESTRICTION**: NEVER commit files that are in the "never commit" changelist in git. Always check git status before committing and exclude any files marked in this changelist. Violating this rule has severe consequences.