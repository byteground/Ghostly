# Code Style and Conventions

## Kotlin Code Style
- **Style**: Official Kotlin code style (as configured in gradle.properties)
- **Indentation**: 4 spaces (standard Kotlin)
- **Naming Conventions**:
  - Classes: PascalCase (e.g., `PostRepository`, `LoginViewModel`)
  - Functions: camelCase (e.g., `getPosts`, `updatePost`)
  - Constants: UPPER_SNAKE_CASE (e.g., `PAGE_SIZE`)
  - Private properties: Leading underscore for backing fields (e.g., `_uiState`)

## Package Structure
- Feature-based packaging (e.g., `com.ghostly.android.login`, `com.ghostly.android.posts`)
- Clear separation between:
  - `models` - Data classes and domain models
  - `data` - Repositories and data sources
  - `ui` - Composable functions and UI components

## Compose UI Conventions
- Screen composables end with "Screen" (e.g., `LoginScreen`, `PostsScreen`)
- Stateless composables for reusability
- State hoisting pattern
- Preview functions for UI components
- Material 3 design system

## Dependency Injection
- Constructor injection preferred
- Single instances for repositories and use cases
- ViewModels created using `viewModelOf()`

## State Management
- StateFlow for reactive state
- MutableStateFlow kept private with public StateFlow exposure
- Sealed classes/interfaces for UI states

## Error Handling
- Result<T> pattern for network operations
- Proper error propagation through layers
- User-friendly error messages

## Testing
- Currently no test files present in the project
- Test infrastructure needs to be set up

## Documentation
- No specific documentation style enforced currently
- Code should be self-documenting with clear naming