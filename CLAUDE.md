# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Ghostly is a Kotlin Multiplatform Mobile (KMM) application that serves as a modern, read-only client for Ghost CMS. The project demonstrates an offline-first approach with local caching and pagination support.

## Commands

### Build Commands
```bash
# Build Android app
./gradlew :androidApp:assembleDebug

# Build shared module
./gradlew :shared:build

# Clean build
./gradlew clean

# Sync iOS dependencies (run after shared module changes)
cd iosApp && pod install
```

### Development Commands
```bash
# Run Android app (requires Android Studio or connected device)
./gradlew :androidApp:installDebug

# Generate database schema (after Room entity changes)
./gradlew :shared:kspCommonMainKotlinMetadata

# Check for dependency updates
./gradlew dependencyUpdates
```

### Testing Commands
**Note:** No testing infrastructure is currently configured. When implementing tests:
- Use JUnit5 for unit tests
- MockK for mocking
- Turbine for testing Flows
- Add test commands here once configured

### Linting Commands
**Note:** No linting tools are currently configured. Consider adding:
- detekt for static analysis
- ktlint for code formatting

## Architecture

The project follows **Clean Architecture** with clear separation of concerns:

### Module Structure
- `androidApp/` - Android-specific UI implementation using Jetpack Compose
- `iosApp/` - iOS-specific implementation (SwiftUI - in progress)
- `shared/` - Shared business logic, data layer, and models

### Layer Architecture in Shared Module
```
shared/src/commonMain/kotlin/com/ghostly/
├── database/          # Local persistence (Room)
├── network/           # API communication (Ktor)
├── models/           # Domain models
├── repository/       # Data repositories
├── login/            # Authentication feature
├── home/             # Posts listing feature
├── settings/         # User settings feature
└── utils/            # Shared utilities
```

### Key Patterns
1. **MVVM Pattern**: ViewModels in Android module manage UI state
2. **Repository Pattern**: Abstracts data sources (network + database)
3. **Offline-First**: Room database with RemoteMediator for caching
4. **Dependency Injection**: Koin for DI configuration
5. **Coroutines & Flow**: For async operations and reactive data streams

### Important Implementation Details

#### API Configuration
- Base URL and credentials are stored in `SiteDetails` data class
- API service uses Ktor with JSON serialization
- Ghost Admin API integration with proper authentication headers

#### Database Schema
- Room database with entities for Posts, Users, Authors, Tags
- RemoteKeys for pagination support
- Proper foreign key relationships and indexes

#### Pagination
- Paging 3 library with RemoteMediator
- Handles network/database synchronization
- Page size of 15 posts

#### State Management
- UI states modeled as sealed classes (Loading, Success, Error)
- SharedFlow for one-time events
- StateFlow for UI state

### Current Features
- Ghost CMS authentication
- Posts listing with offline support
- Post detail view
- User settings and staff management
- Dark mode support

## Development Guidelines

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Prefer composition over inheritance
- Use extension functions for utility operations
- Handle nullability explicitly

### Adding New Features
1. Create feature package in shared module
2. Define models in `models/` subpackage
3. Implement repository in `data/` subpackage
4. Create ViewModel in Android module
5. Build Compose UI in appropriate screen package

### Making API Changes
1. Update models in shared module
2. Modify `ApiService` interface
3. Update repository implementation
4. Run database schema generation if entities change
5. Test offline behavior

### Common Pitfalls
- Remember to handle offline scenarios in repositories
- Use proper error handling with sealed classes
- Test on both Android and iOS (when iOS is implemented)
- Ensure proper ProGuard rules for release builds
- Handle pagination edge cases (empty lists, errors)

## Tech Stack Reference
- **Kotlin**: 2.0.0
- **Compose BOM**: 2024.06.00
- **Ktor**: 2.3.11
- **Room**: 2.7.0-alpha04
- **Koin**: 3.5.6
- **Paging**: 3.3.0-alpha02-0.5.1
- **Minimum Android SDK**: 24
- **Target Android SDK**: 34