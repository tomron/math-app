# MathMaster — Project Guide for Claude

## What Is This Project?

MathMaster is an offline-first Android app that bundles multiple math games. Users pick a profile (name + initials avatar), then choose from a menu of 6 math games, each with difficulty levels.

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose with Material 3
- **Navigation:** Compose Navigation (type-safe routes)
- **State Management:** ViewModel + StateFlow
- **Storage:** Room (SQLite) — fully offline
- **DI:** Manual dependency injection (no Hilt/Dagger)
- **Testing:** JUnit 5 + Compose Testing + Turbine (Flow testing)
- **Build:** Gradle with Kotlin DSL (.kts files)
- **Min SDK:** 24 (Android 7.0)
- **CI/CD:** GitHub Actions

## Project Structure

```
app/src/main/java/com/mathmaster/app/
├── data/
│   ├── db/              # Room: AppDatabase, DAOs, Entities
│   └── repository/      # Repository pattern over DAOs
├── ui/
│   ├── profile/         # Profile selection & creation
│   ├── menu/            # Game menu grid
│   ├── games/           # One sub-package per game
│   │   ├── addition/
│   │   ├── subtraction/
│   │   ├── multiplication/
│   │   ├── division/
│   │   ├── mixed/
│   │   └── speed/
│   └── theme/           # Material 3 theme definition
├── navigation/          # NavHost + route definitions
└── di/                  # AppContainer for manual DI
```

Tests mirror this under `app/src/test/` (unit) and `app/src/androidTest/` (instrumented).

## Conventions

### Code Style
- Follow standard Kotlin conventions
- Composable functions: PascalCase (e.g., `ProfileScreen`)
- State holders: suffix with `ViewModel`
- Database entities: suffix with `Entity`
- DAOs: suffix with `Dao`
- Use `StateFlow` for UI state, never `LiveData`

### Branching
- Each feature lives on its own branch (see progress.md)
- Use git worktrees for parallel development
- All branches merge to `main` in order

### Testing
- Every public function gets a unit test
- Every screen gets at least one UI test for core interactions
- Test files: `<ClassName>Test.kt`
- Use fakes/stubs over mocks when possible

### Dependencies
- Keep dependencies minimal
- No Hilt/Dagger — use manual DI via AppContainer
- No network libraries (app is offline-only)

## Key Commands

```bash
# Build
./gradlew assembleDebug

# Unit tests
./gradlew test

# Instrumented tests (requires emulator)
./gradlew connectedAndroidTest

# Lint
./gradlew lint

# Run on emulator
./gradlew installDebug
```

## CI/CD Workflows

| Workflow         | Trigger                  | What it does                    |
|------------------|--------------------------|---------------------------------|
| `ci.yml`         | Every push               | Lint + unit tests               |
| `test-branch.yml`| Manual (workflow_dispatch)| Full tests on a specified branch|
| `release.yml`    | Manual                   | Signed AAB build for Play Store |

## Architecture Decisions

1. **Manual DI** — The app is small; Hilt adds complexity without proportional benefit.
2. **No network layer** — Fully offline. If network is needed later, add Retrofit + OkHttp.
3. **Room for storage** — Single source of truth for profiles and future score tracking.
4. **Compose Navigation** — Keeps navigation declarative and testable.
5. **Placeholder games** — Each game screen is a stub to be filled in later. They share a common scaffold (top bar, back button, difficulty selector).

## Reference Documents

- [PRD.md](./PRD.md) — Full product requirements
- [progress.md](./progress.md) — Implementation status and merge order
