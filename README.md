# GlobalTrip

A cross-platform travel companion application built with Kotlin Multiplatform (KMP) and Compose Multiplatform, designed to work seamlessly on Android, iOS, and Web.

## Project Overview

GlobalTrip helps travelers plan, organize, and share their journey experiences across multiple platforms with a single codebase. The app supports trip planning, real-time navigation, expense tracking, and collaboration with fellow travelers.

## Architecture

The project follows Clean Architecture principles combined with a modular approach:

- **Core Modules**: Reusable components shared across features
  - `common`: Utility classes and extensions
  - `data`: Data repositories and sources
  - `database`: Local persistence with SQLDelight
  - `designsystem`: UI components and theming
  - `domain`: Business logic and use cases
  - `network`: API communication with Ktor
  - `preferences`: User preferences storage
  - `security`: Authentication and encryption
  - `testing`: Test utilities
  - `ui`: Common UI components

- **Feature Modules**: Independent functional areas
  - `auth`: User authentication and profile management
  - `trips`: Trip creation, management, and details
  - `maps`: Maps integration and navigation
  - `expenses`: Expense tracking and splitting
  - `collaboration`: Sharing and collaboration features
  - `settings`: User preferences and app configuration

## Supported Platforms

- Android (Phone & Tablet)
- iOS (iPhone & iPad)
- Web (via Kotlin/Wasm)

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or newer
- Xcode 13 or newer (for iOS development)
- JDK 17 or newer
- Gradle 8.0+
- CocoaPods (for iOS dependencies)

### Building the Project

#### Android

```bash
./gradlew :androidApp:build
```

#### iOS

```bash
cd iosApp
pod install
# Open the generated .xcworkspace file in Xcode and build
```

#### Web

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

## Development Guidelines

Refer to the [Coding Conventions](./docs/coding_convention.md) document for detailed guidelines on code style, architecture patterns, and best practices used throughout the project.