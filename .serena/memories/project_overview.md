# Ghostly Project Overview

## Purpose
Ghostly is an open-source, modern client application for the Ghost CMS (Content Management System). It's designed to provide a seamless, user-friendly experience for Ghost blog creators on mobile platforms.

## Tech Stack
- **Platform**: Kotlin Multiplatform Mobile (KMM)
- **Kotlin Version**: 2.0.0
- **Android Gradle Plugin**: 8.2.2
- **Minimum Android SDK**: 29
- **Target Android SDK**: 34

### Android Stack
- **UI Framework**: Jetpack Compose (version 1.6.8)
- **Material Design**: Material 3 (version 1.2.1)
- **Navigation**: Jetpack Navigation Compose (2.8.0-beta05)
- **Dependency Injection**: Koin (3.5.3)
- **State Management**: ViewModel with StateFlow
- **Async**: Kotlin Coroutines
- **Image Loading**: Coil (2.6.0)
- **Preferences**: DataStore (1.1.1)
- **Rich Text Editor**: richeditor-compose (1.0.0-rc05)

### Shared Module Stack
- **Networking**: Ktor Client (2.3.8)
- **Serialization**: Kotlinx Serialization (1.6.3)
- **Database**: Room (2.7.0-alpha05) with SQLite
- **Pagination**: Cash App Paging (3.3.0-alpha02-0.5.1)
- **Date/Time**: Kotlinx DateTime (0.6.0)

## Project Structure
- `/androidApp` - Android-specific implementation
- `/iosApp` - iOS-specific implementation (Cocoapods setup)
- `/shared` - Shared KMM code (data layer, repositories, use cases)
- `/gradle` - Gradle configuration files

## Current Status
- ✅ Simple read-only Android application available
- ✅ Ready to use with Ghost CMS configuration
- 🔄 Actively developing and seeking contributions
- 📱 iOS support in progress (framework configured)