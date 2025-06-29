# Detailed Project Structure

## Root Level Files
- `build.gradle.kts` - Root build configuration, applies plugins
- `settings.gradle.kts` - Project modules configuration
- `gradle.properties` - Gradle and Kotlin configuration
- `gradlew` / `gradlew.bat` - Gradle wrapper scripts
- `.gitignore` - Git ignore configuration
- `README.md` - Project documentation
- `LICENSE` - MIT License

## /shared Module Structure
Kotlin Multiplatform shared code:

### Package: com.ghostly
- `CommonModule.kt` - Koin DI module definitions
- `Platform.kt` - Platform-specific implementations

### Sub-packages:
- **database/** - Room database implementation
  - `GhostlyDatabase.kt` - Database definition
  - `databaseModule.kt` - Database DI module
  - **dao/** - Data Access Objects
    - `PostDao.kt` - Post operations
    - `RemoteKeysDao.kt` - Pagination keys
  - **entities/** - Database entities
    - `PostEntity.kt`
    - `RemoteKeys.kt`

- **datastore/** - DataStore preferences
  - `DataStoreRepository.kt` - Preferences management
  - `DataStoreConstants.kt` - Key constants
  - `LoginDetailsStore.kt` - Login info storage

- **login/** - Authentication feature
  - **data/** - Repository implementations
  - **models/** - Login-related models
    - `LoginDetails.kt`, `LoginState.kt`, `SiteDetails.kt`

- **network/** - Networking layer
  - `ApiClient.kt` - Ktor client setup
  - `ApiService.kt` - API endpoints
  - `Endpoints.kt` - URL constants
  - `TokenProvider.kt` - Auth token management
  - **models/** - Network DTOs

- **posts/** - Posts feature
  - **data/** - Repository, use cases, data sources
  - **models/** - Post-related models

- **settings/** - Settings feature
  - **data/** - SettingsRepository
  - **models/** - User, Roles, Invite

- **mappers/** - Entity to domain model mappers

## /androidApp Module Structure

### Package: com.ghostly.android
- `MainActivity.kt` - Single activity
- `GhostApplication.kt` - Application class
- `AppNavigation.kt` - Navigation graph
- `AppModule.kt` - App-level DI

### Feature Packages:
- **home/** - Home screen implementation
- **login/** - Login flow
  - `LoginViewModel.kt`
  - **ui/** - Login composables
- **posts/** - Posts list and details
  - ViewModels, UI components
- **settings/** - App settings
  - `SettingsViewModel.kt`
  - Settings screens
- **theme/** - Material theme setup
- **ui/components/** - Reusable UI components
- **utils/** - Utility extensions

### Resources (/res):
- **values/** - Strings, colors, themes
- **drawable/** - Vector drawables
- **mipmap/** - App icons

## /iosApp Module
- CocoaPods integration setup
- `Podfile` - iOS dependencies
- Framework configuration for KMM

## /gradle Module
- `libs.versions.toml` - Version catalog for dependencies

## Key Architectural Decisions
1. **Single Activity Architecture** - MainActivity with Compose Navigation
2. **Feature Modules** - Code organized by feature
3. **Offline-First** - Room database with RemoteMediator
4. **Reactive UI** - StateFlow and Compose state
5. **Clean Architecture** - Clear separation of layers
6. **Type Safety** - Kotlin's type system, sealed classes