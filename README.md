# MathMaster

An offline-first Android app that bundles multiple math games for practice and fun. Built with Jetpack Compose and Material 3.

## Features

- **Profile System**: Create and manage multiple user profiles with auto-generated initials avatars
- **Game Menu**: Choose from 6 different math games with varying difficulty levels
- **Fully Offline**: All data stored locally with Room database - no internet required
- **Math Games**:
  1. **Digits** ✅ - Combine numbers using operations to reach a target (fully playable with 3 modes & difficulties)
  2. **Subtraction** - Practice subtraction problems (placeholder)
  3. **Multiplication** - Practice multiplication tables (placeholder)
  4. **Division** - Practice division problems (placeholder)
  5. **Mixed Operations** - Random mix of all four operations (placeholder)
  6. **Speed Round** - Timed math challenges (placeholder)

## Tech Stack

| Component | Technology |
|-----------|------------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose |
| **Design System** | Material 3 |
| **Navigation** | Compose Navigation (type-safe routes) |
| **State Management** | ViewModel + StateFlow |
| **Local Database** | Room (SQLite) |
| **Dependency Injection** | Manual DI (AppContainer) |
| **Testing** | JUnit 5, Compose Testing, Turbine |
| **Build System** | Gradle with Kotlin DSL |
| **CI/CD** | GitHub Actions |
| **Min SDK** | 24 (Android 7.0) |
| **Target SDK** | 34 (Android 14) |

## Project Structure

```
app/src/main/java/com/mathmaster/app/
├── data/
│   ├── db/              # Room database components
│   │   ├── AppDatabase.kt       # Database definition
│   │   ├── ProfileDao.kt        # Data access objects
│   │   └── ProfileEntity.kt     # Database entities
│   └── repository/      # Repository pattern implementation
│       └── ProfileRepository.kt
├── ui/
│   ├── profile/         # Profile selection & creation
│   │   ├── ProfileSelectionScreen.kt
│   │   ├── ProfileSelectionViewModel.kt
│   │   └── ProfileSelectionViewModelFactory.kt
│   ├── menu/            # Game menu grid
│   │   ├── GameMenuScreen.kt
│   │   └── GameMenuViewModel.kt
│   ├── games/           # Individual game screens
│   │   ├── digits/           # ✅ Fully playable puzzle game
│   │   │   ├── DigitsGame.kt        # Core game logic & types
│   │   │   ├── PuzzleGenerator.kt   # Puzzle generation & BFS solver
│   │   │   ├── DigitsGameViewModel.kt
│   │   │   └── DigitsGameScreen.kt
│   │   ├── subtraction/      # Placeholder
│   │   ├── multiplication/   # Placeholder
│   │   ├── division/         # Placeholder
│   │   ├── mixed/            # Placeholder
│   │   └── speed/            # Placeholder
│   └── theme/           # Material 3 theme
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── navigation/          # Navigation graph
│   ├── NavGraph.kt
│   └── Routes.kt
├── di/                  # Dependency injection
│   └── AppContainer.kt
├── MainActivity.kt
└── MathMasterApplication.kt
```

## Getting Started

### Prerequisites

- **JDK** 17 (already installed at `/opt/homebrew/opt/openjdk@17`)
- **Android SDK** with API level 34 (already installed)
- **Gradle** 8.2 or later (included via wrapper)
- **Android Emulator** (already configured)

### Quick Start (Recommended)

**Option 1: All-in-One Script**

The easiest way to run the app:

```bash
./start-dev.sh
```

This script will:
1. Start the Android emulator
2. Wait for it to boot
3. Build and install the app
4. Launch MathMaster automatically

**Option 2: Step-by-Step**

If you prefer more control:

```bash
# Terminal 1: Start the emulator
./run-emulator.sh &

# Wait 30-60 seconds for emulator to boot, then in Terminal 2:
./run-app.sh
```

**Option 3: Manual Commands**

For the current shell session (paths are set):
```bash
# Start emulator in background
/opt/homebrew/share/android-commandlinetools/emulator/emulator -avd MathMaster_Emulator &

# Wait for boot, then install
./gradlew installDebug
```

For new shell sessions, restart your terminal or run:
```bash
source ~/.zshrc
emulator -avd MathMaster_Emulator &
```

### Using Android Studio

1. **Install Android Studio**
   ```bash
   brew install --cask android-studio
   ```

2. **Open the project**
   - Launch Android Studio
   - Select "Open" → Navigate to `/Users/tomron/code/math-app`

3. **Run the app**
   - Select `MathMaster_Emulator` from the device dropdown
   - Click Run (▶️) or press `Ctrl+R`

### Building from Command Line

```bash
# Build debug APK
./gradlew assembleDebug

# Install debug build to connected device/emulator
./gradlew installDebug

# Build release APK (unsigned)
./gradlew assembleRelease
```

The debug APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Useful Development Commands

```bash
# Check connected devices
adb devices

# View app logs
adb logcat | grep MathMaster

# Clear app data (reset to fresh state)
adb shell pm clear com.mathmaster.app

# Uninstall app
adb uninstall com.mathmaster.app

# Stop emulator
adb emu kill

# Reinstall fresh
./gradlew uninstallDebug installDebug
```

## Testing

### Running Unit Tests

Unit tests use JUnit 5 and Turbine for Flow testing.

```bash
# Run all unit tests
./gradlew test

# Run tests for a specific module
./gradlew :app:test

# Run tests with coverage
./gradlew testDebugUnitTest
```

Test reports are generated at:
```
app/build/reports/tests/testDebugUnitTest/index.html
```

### Running Instrumented Tests

Instrumented tests use Compose Testing and Espresso.

```bash
# Start an emulator first, then run:
./gradlew connectedAndroidTest
```

### Running Specific Tests

```bash
# Run a specific test class
./gradlew test --tests "ProfileRepositoryTest"

# Run tests matching a pattern
./gradlew test --tests "*ViewModel*"
```

## Code Quality

### Linting

```bash
# Run Android Lint
./gradlew lint

# View lint report
open app/build/reports/lint-results-debug.html
```

### Code Style

This project follows standard Kotlin coding conventions:
- **Composable functions**: PascalCase (e.g., `ProfileScreen`)
- **State holders**: Suffix with `ViewModel`
- **Database entities**: Suffix with `Entity`
- **DAOs**: Suffix with `Dao`
- **StateFlow** for reactive UI state (never LiveData)

## CI/CD

This project uses GitHub Actions for continuous integration and deployment.

### Workflows

| Workflow | Trigger | Purpose |
|----------|---------|---------|
| **`ci.yml`** | Every push | Runs lint and unit tests |
| **`test-branch.yml`** | Manual (`workflow_dispatch`) | Full test suite on specified branch |
| **`release.yml`** | Manual (`workflow_dispatch`) | Builds signed AAB for Play Store |

### Triggering Manual Workflows

**Test a specific branch:**
```bash
gh workflow run test-branch.yml -f branch=feature/my-feature
```

**Create a release build:**
```bash
gh workflow run release.yml
```

## Working with Git Worktrees

This project uses git worktrees for parallel feature development. Each feature branch has its own working directory.

### Current Worktrees

```bash
# List all worktrees
git worktree list
```

Example output:
```
/Users/tomron/code/math-app         [main]
/Users/tomron/code/math-app-phase2  [feature/profile-system]
/Users/tomron/code/math-app-phase3  [feature/game-menu]
/Users/tomron/code/math-app-phase4  [feature/game-scaffold]
```

### Creating a New Worktree

```bash
# Create a new feature branch with its own directory
git worktree add ../math-app-my-feature feature/my-feature
cd ../math-app-my-feature
```

### Switching Between Features

```bash
# Work on profile system
cd /Users/tomron/code/math-app-phase2

# Work on game menu
cd /Users/tomron/code/math-app-phase3

# Back to main
cd /Users/tomron/code/math-app
```

### Cleaning Up Worktrees

```bash
# Remove a worktree (must commit or stash changes first)
git worktree remove ../math-app-phase2

# Clean up stale worktrees (after manual deletion)
git worktree prune

# Remove gone branches (merged and deleted remotely)
git branch -vv | grep ': gone]' | awk '{print $1}' | xargs git branch -D
```

## Pull Request Workflow

### Creating a Pull Request

1. **Commit your changes**
   ```bash
   git add .
   git commit -m "Add feature description"
   ```

2. **Push to GitHub**
   ```bash
   git push origin feature/my-feature
   ```

3. **Create PR via GitHub CLI**
   ```bash
   gh pr create --title "Feature: My Feature" --body "Description of changes"
   ```

### Merge Order

Features must be merged in the following order (see `progress.md`):

```
1. claude/setup-android-math-games-S5UBn  →  main (Foundation)
2. feature/profile-system                 →  main
3. feature/game-menu                      →  main
4. feature/game-scaffold                  →  main
5. feature/game-addition                  →  main
6. feature/game-subtraction               →  main
7. feature/game-multiplication            →  main
8. feature/game-division                  →  main
9. feature/game-mixed                     →  main
10. feature/game-speed                    →  main
11. feature/release-pipeline              →  main
```

### Before Merging

- ✅ All CI checks must pass
- ✅ Code reviewed and approved
- ✅ All tests passing locally
- ✅ No merge conflicts with base branch

## Development Guidelines

### Manual Dependency Injection

This project uses **manual DI** via `AppContainer` instead of Hilt/Dagger:

```kotlin
// app/src/main/java/com/mathmaster/app/di/AppContainer.kt
class AppContainer(context: Context) {
    private val database = AppDatabase.getInstance(context)
    val profileRepository = ProfileRepository(database.profileDao())
}
```

**Why manual DI?**
- Project is small and straightforward
- Avoids annotation processing overhead
- Easier to understand and debug
- No build-time code generation

### State Management

Use `StateFlow` for all reactive UI state:

```kotlin
// ✅ Good
class MyViewModel : ViewModel() {
    private val _state = MutableStateFlow(MyState())
    val state: StateFlow<MyState> = _state.asStateFlow()
}

// ❌ Avoid
class MyViewModel : ViewModel() {
    val data: LiveData<MyState> = MutableLiveData()
}
```

### Testing Guidelines

1. **Every public function gets a unit test**
2. **Every screen gets at least one UI test**
3. **Use fakes/stubs over mocks when possible**
4. **Test files**: `<ClassName>Test.kt`

Example test structure:
```kotlin
class ProfileRepositoryTest {
    @Test
    fun `getAllProfiles returns all profiles from database`() = runTest {
        // Arrange
        val profiles = listOf(/* ... */)

        // Act
        val result = repository.getAllProfiles().first()

        // Assert
        assertEquals(profiles, result)
    }
}
```

## Architecture

This app follows **MVVM (Model-View-ViewModel)** architecture with unidirectional data flow:

```
┌─────────────┐
│   Screen    │ (Composable UI)
└──────┬──────┘
       │ observes StateFlow
       │ calls functions
┌──────▼──────┐
│  ViewModel  │ (UI State + Business Logic)
└──────┬──────┘
       │ calls suspend functions
┌──────▼──────┐
│ Repository  │ (Data abstraction)
└──────┬──────┘
       │ uses DAOs
┌──────▼──────┐
│    Room     │ (SQLite Database)
└─────────────┘
```

### Key Principles

- ✅ **Single source of truth**: Room database
- ✅ **Unidirectional data flow**: UI → ViewModel → Repository → Database
- ✅ **Reactive streams**: StateFlow for UI state, Flow for database queries
- ✅ **Separation of concerns**: Clear boundaries between UI, business logic, and data layers
- ✅ **Testability**: Each layer can be tested in isolation

## Contributing

1. Create a feature branch from `main`
2. Make your changes with tests
3. Ensure all tests pass locally
4. Push and create a pull request
5. Wait for CI to pass and code review

## License

This project is proprietary and confidential.

## Support

For issues or questions, please open an issue on GitHub or contact the development team.

---

**Built with ❤️ using Jetpack Compose**
