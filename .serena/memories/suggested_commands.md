# Suggested Commands for Ghostly Development

## Build Commands
```bash
# Clean build
./gradlew clean

# Build the project
./gradlew build

# Build Android app only
./gradlew :androidApp:build

# Build shared module
./gradlew :shared:build

# Assemble debug APK
./gradlew :androidApp:assembleDebug

# Assemble release APK
./gradlew :androidApp:assembleRelease
```

## Installation Commands
```bash
# Install debug build on connected device/emulator
./gradlew installDebug

# Uninstall the app
./gradlew uninstallDebug
```

## Running the App
```bash
# Run on connected Android device/emulator
./gradlew :androidApp:installDebug

# Launch Android Studio (macOS)
open -a "Android Studio" .
```

## iOS/CocoaPods Commands
```bash
# Install CocoaPods dependencies
./gradlew podInstall

# Generate podspec
./gradlew podspec

# Build XCFramework
./gradlew podPublishDebugXCFramework
```

## Code Quality (Currently not configured but standard commands)
```bash
# Run lint checks
./gradlew lint

# Generate lint report
./gradlew lintDebug

# Fix lint issues (safe fixes only)
./gradlew lintFix
```

## Testing (Infrastructure needs setup)
```bash
# Run all tests
./gradlew test

# Run Android unit tests
./gradlew :androidApp:testDebugUnitTest

# Run iOS tests
./gradlew iosSimulatorArm64Test
```

## Dependency Management
```bash
# Show dependencies
./gradlew dependencies

# Show Android dependencies
./gradlew androidDependencies
```

## Git Commands (Darwin/macOS specific)
```bash
# Common git operations
git status
git add .
git commit -m "message"
git push origin develop
git pull origin develop

# Create new feature branch
git checkout -b feature/branch-name

# Merge changes
git merge develop
```

## System Commands (Darwin/macOS)
```bash
# List files
ls -la

# Change directory
cd directory_name

# Find files
find . -name "*.kt"

# Search in files (using ripgrep if installed, otherwise grep)
rg "pattern" --type kotlin
grep -r "pattern" --include="*.kt" .

# Open in Finder
open .
```

## Gradle Wrapper
```bash
# Use gradle wrapper (always prefer this)
./gradlew [task]

# Update gradle wrapper
./gradlew wrapper --gradle-version=8.4
```

## Environment Setup
```bash
# Check Java version
java -version

# Check Kotlin version
kotlin -version

# List available tasks
./gradlew tasks

# Get detailed task help
./gradlew help --task [taskName]
```