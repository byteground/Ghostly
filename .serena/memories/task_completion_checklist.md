# Task Completion Checklist

When completing a development task in the Ghostly project, ensure you:

## Before Committing Code

### 1. Code Quality
- [ ] Code follows the official Kotlin code style
- [ ] No unnecessary imports
- [ ] No commented-out code (unless with explanation)
- [ ] Meaningful variable and function names
- [ ] Private backing fields use underscore prefix (e.g., `_uiState`)

### 2. Build Verification
```bash
# Clean and rebuild to ensure no compilation errors
./gradlew clean build
```

### 3. Lint Checks (when configured)
```bash
# Run lint to check for issues
./gradlew lint

# Fix auto-fixable issues
./gradlew lintFix
```

### 4. Manual Testing
- [ ] Test on Android emulator/device (API 29+)
- [ ] Test different screen sizes if UI changes
- [ ] Verify no crashes or ANRs
- [ ] Check for proper error handling

### 5. Dependency Injection
- [ ] New dependencies properly registered in appropriate Koin modules
- [ ] ViewModels registered using `viewModelOf()`

### 6. State Management
- [ ] StateFlow properly exposed (private mutable, public read-only)
- [ ] Proper coroutine scope usage in ViewModels
- [ ] No memory leaks

### 7. UI/UX (for UI changes)
- [ ] Follows Material 3 design guidelines
- [ ] Compose previews added for new components
- [ ] Dark theme support considered
- [ ] Proper loading and error states

### 8. Architecture Compliance
- [ ] Follows Clean Architecture principles
- [ ] Repository pattern properly implemented
- [ ] Use cases for business logic
- [ ] Proper separation of concerns

## Before Creating PR

### 1. Git Hygiene
- [ ] Commits have meaningful messages
- [ ] Branch created from `develop`
- [ ] No merge conflicts with `develop`

### 2. Documentation
- [ ] Update README if adding new features
- [ ] Add inline comments for complex logic
- [ ] Update this checklist if new standards introduced

### 3. Final Checks
```bash
# Ensure working directory is clean
git status

# Run final build
./gradlew assembleDebug
```

## Post-PR Creation
- [ ] Link to relevant issue in PR description
- [ ] Add screenshots/GIFs for UI changes
- [ ] Request review from maintainers
- [ ] Respond to review comments promptly