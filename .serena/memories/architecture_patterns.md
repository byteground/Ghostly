# Architecture Patterns

## Overall Architecture
The project follows a **Clean Architecture** pattern with clear separation of concerns:

### Layers
1. **Presentation Layer** (androidApp/iosApp)
   - UI components (Compose for Android)
   - ViewModels
   - Navigation

2. **Domain Layer** (shared module)
   - Use Cases (e.g., GetPostsUseCase, EditPostUseCase, GetSiteDetailsUseCase)
   - Repository interfaces
   - Domain models

3. **Data Layer** (shared module)
   - Repository implementations
   - Data sources (Local and Remote)
   - Database entities
   - Network services

## Design Patterns

### MVVM Pattern
- ViewModels manage UI state using StateFlow
- Unidirectional data flow
- Example: LoginViewModel, PostsViewModel, SettingsViewModel

### Repository Pattern
- Abstracts data sources from the rest of the app
- Examples: PostRepository, SettingsRepository, SiteRepository
- Each repository has an interface and implementation

### Use Case Pattern
- Business logic encapsulated in use cases
- Single responsibility principle
- Examples: GetPostsUseCase, EditPostUseCase

### Dependency Injection
- Using Koin for DI
- Modules organized by feature (loginModule, postsModule, etc.)
- CommonModule for shared dependencies

### Offline-First with Paging
- Room database for local storage
- RemoteMediator for network/database synchronization
- Paging 3 library for efficient data loading

## Key Architectural Components
- **Navigation**: Jetpack Navigation Compose with type-safe arguments
- **State Management**: MutableStateFlow and StateFlow
- **Coroutines**: For async operations with proper scope management
- **Sealed Classes**: For Result types (Success/Error)
- **Data Classes**: For immutable domain models