# GlobalTrip - Cross-Platform Trip Planning App

A smart travel planning application built with Kotlin Multiplatform and Compose Multiplatform for Android, iOS, and Web.

## Project Overview

GlobalTrip is a comprehensive travel planning platform that provides features for timeline creation, task management, expense tracking, map integration, and collaboration. The application is designed to work seamlessly across Android, iOS, and Web platforms, with offline support.

## Architecture

The project follows clean architecture principles with a modular approach:

```
com.jkhanh.globaltrip/
├── core/                   # Core modules (shared across features)
│   ├── domain/             # Domain layer with business logic
│   │   ├── model/          # Domain models (Trip, Task, Activity, etc.)
│   │   └── repository/     # Repository interfaces
│   ├── data/               # Data layer implementation
│   │   ├── repository/     # Repository implementations
│   │   └── source/         # Data sources (local and remote)
│   └── ui/                 # Common UI components
│       ├── theme/          # Theming (colors, typography, etc.)
│       └── components/     # Reusable UI components (buttons, cards, etc.)
└── feature/                # Feature modules
    ├── trips/              # Trip planning feature
    │   ├── domain/         # Trip-specific business logic
    │   ├── presentation/   # ViewModels and states
    │   └── ui/             # Trip UI components
    ├── auth/               # Authentication feature
    ├── maps/               # Maps and navigation feature
    └── expenses/           # Expense tracking feature
```

## Key Features (MVP Phase)

1. **Trip Timeline**
   - Create and manage trips with start/end dates
   - View trip details and activities

2. **Task Management**
   - Create tasks and packing lists for trips
   - Mark tasks as completed

3. **Basic Expense Tracking**
   - Add expenses with categories
   - View expense summaries

4. **Offline Support**
   - Work with trips when offline
   - Sync when back online

## Technology Stack

- **Kotlin**: 2.1.20
- **Compose Multiplatform**: 1.7.3
- **KotlinX DateTime**: For cross-platform date handling
- **Android**: Min SDK 24, Target SDK 35
- **Kotlin Multiplatform**: Android, iOS, and Web (Wasm)

## Project Structure

### Domain Models

The core domain models include:
- `Trip`: Represents a travel trip with dates, destination, etc.
- `TripDay`: A day in the trip itinerary
- `Activity`: An activity scheduled for a specific day
- `Task`: A checklist item or todo for trip preparation
- `Expense`: A cost associated with the trip
- `Location`: A geographical location with coordinates

### Repositories

Repository interfaces define the operations for each domain model:
- `TripRepository`: Operations for trip management
- `ActivityRepository`: Operations for activities
- `TaskRepository`: Operations for tasks and checklists
- `ExpenseRepository`: Operations for expense tracking
- `LocationRepository`: Operations for managing locations

### UI Components

Custom UI components provide a consistent look and feel:
- `GTButton`: Standard and outlined button variants
- `GTTextField`: Text input fields
- `GTCard`: Card containers for content

### Navigation

Simple navigation system with:
- `Screen`: Defines all screens and routes
- `Navigator`: Handles screen transitions with back stack
- `BottomNavigationBar`: Main navigation between primary screens

## Getting Started

### Prerequisites

- Android Studio Electric Eel or newer
- Xcode 14+ (for iOS development)
- JDK 11+

### Building the Project

#### Android
```
./gradlew :composeApp:assembleDebug
```

#### iOS
```
./gradlew :composeApp:podInstall
```
Then open the Xcode project in the `iosApp` directory.

#### Web
```
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

## Development Roadmap

### Phase 1: MVP (Current)
- Trip timeline creation
- Task/packing checklist
- Basic expense tracking
- Offline support

### Phase 2: Full Product
- Real-time collaboration
- Template system
- Advanced mapping features

### Phase 3: AI Experience
- Smart suggestions
- Delay prediction
- Cost optimization

## License

[MIT License](LICENSE)
