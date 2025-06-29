# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
Ghostly is a Kotlin Multiplatform (KMP) client for Ghost CMS, currently implementing an Android app with planned iOS support. Development happens on the `develop` branch.

## Essential Commands

### Building
```bash
# Full project build
./gradlew build

# Android-specific builds
./gradlew :androidApp:assembleDebug    # Debug APK
./gradlew :androidApp:assembleRelease  # Release APK
./gradlew :androidApp:installDebug     # Install on device/emulator
```

### Code Quality
```bash
# Lint checks (MUST pass before committing)
./gradlew lint
./gradlew lintFix                      # Apply safe fixes

# Run tests
./gradlew test
./gradlew :androidApp:testDebugUnitTest
./gradlew connectedAndroidTest         # Instrumentation tests
```

### Development Workflow
```bash
# Clean build artifacts
./gradlew clean

# List all available tasks
./gradlew tasks

# View project dependencies
./gradlew dependencies
```

## Architecture Overview

### Multi-Module Structure
- **`/shared`**: KMP shared module containing business logic, data access, and networking
  - `commonMain`: Cross-platform code (repositories, use cases, data models)
  - `androidMain`: Android-specific implementations (DataStore, platform-specific networking)
  - `iosMain`: iOS-specific implementations (placeholder for future)
- **`/androidApp`**: Android UI layer using Jetpack Compose
- **`/iosApp`**: iOS app structure (not yet implemented)

### Key Architectural Patterns
1. **MVVM with ViewModels**: Each screen has a ViewModel managing UI state
2. **Repository Pattern**: Data access abstraction in shared module
3. **Use Cases**: Business logic encapsulation (e.g., `GetPostsUseCase`, `EditPostUseCase`)
4. **Dependency Injection**: Koin modules organize dependencies
   - Module structure: `appModule` + `loginModule` + `postsModule` + `ghostCommonModules` + `androidModules`

### Data Flow
1. **Network Layer**: Ktor client with JSON serialization
2. **Local Storage**: Room database with DAOs for offline support
3. **State Management**: Kotlin coroutines and Flow for reactive updates
4. **Paging**: AndroidX Paging 3 with `RemoteMediator` for efficient list loading

### Navigation
- Compose Navigation with type-safe arguments using Kotlin Serialization
- Centralized in `AppNavigation.kt` with sealed class `Destination`

### Critical Integration Points
- **Token Management**: `TokenProvider` interface with platform-specific implementations
- **DataStore**: Platform-specific persistent storage for login credentials
- **Database**: Room database shared across platforms with migrations support

## Development Guidelines
- All PRs must target `develop` branch
- Kotlin official code style enforced
- Compose UI patterns for Android
- Platform-specific UI, shared business logic

## Branch Naming Conventions
- Use the branch name feature/<name> or fix/<name> or release/<name> format.