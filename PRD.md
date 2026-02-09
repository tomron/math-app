# MathMaster - Product Requirements Document

## Overview

MathMaster is an offline-first Android app that bundles multiple simple math games under one roof. Users create profiles (identified by name + avatar) and pick from a menu of games, each with its own difficulty levels.

## Target Platform

- **OS:** Android
- **Min SDK:** 24 (Android 7.0 Nougat, ~97% device coverage)
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Local Storage:** Room (SQLite)
- **Offline:** Fully functional without network access

## Core Features

### 1. Profile System

- **Profile selection screen** is the app entry point
- Users can **create**, **select**, and **delete** profiles
- A profile consists of:
  - **Name** (unique, required, max 20 characters)
  - **Avatar** (auto-generated initials from the name, colored background)
- Profile data persisted locally via Room
- No authentication required

### 2. Game Menu

- After selecting a profile, the user sees a **game menu grid**
- Each game is represented by a **card** with icon, title, and short description
- 6 game slots in the initial release (placeholder implementations)

### 3. Math Games

Each game follows this contract:
- Has its own screen accessible from the menu
- Displays a **top bar** with game name and a **back button** to return to the menu
- Supports **difficulty levels** (Easy / Medium / Hard) selectable before starting

| # | Game             | Description                                    | Status      |
|---|------------------|------------------------------------------------|-------------|
| 1 | **Digits** ✅     | Combine numbers to reach target (3 modes: Classic/Timer/Challenge) | **Complete** |
| 2 | Subtraction      | Solve subtraction problems                     | Placeholder |
| 3 | Multiplication   | Solve multiplication problems                  | Placeholder |
| 4 | Division         | Solve division problems                        | Placeholder |
| 5 | Mixed Operations | Random mix of all four ops                     | Placeholder |
| 6 | Speed Round      | Timed challenge with any op                    | Placeholder |

#### Digits Game (Fully Implemented)

The Digits game is a number puzzle where players combine numbers using arithmetic operations to reach a target.

**Features:**
- **3 Difficulty Levels:**
  - Easy: 4 numbers, +/- only, targets 10-50
  - Medium: 5 numbers, +/-/×, targets 50-200
  - Hard: 6 numbers, all operations, targets 100-500

- **3 Game Modes:**
  - Classic: Untimed, solve at your own pace
  - Timer: 60-second countdown
  - Challenge: Continuous puzzles with cumulative timer

- **Gameplay:**
  - Select two numbers from the grid
  - Choose an operation (+, -, ×, ÷)
  - Combine them to create a new number
  - Repeat until you reach the target

- **Features:**
  - Undo moves
  - Restart puzzle
  - View step-by-step solution (BFS algorithm)
  - Skip to new puzzle
  - Move counter
  - Auto-reverse for non-commutative operations

### 4. Navigation

- Profile Selection → Game Menu → Game Screen
- Back navigation available at every level
- Hardware back button support

## Non-Functional Requirements

### Offline Support
- App works entirely offline
- All data stored locally via Room
- No network calls required

### Testing
- Unit tests for ViewModels, Repositories, and game logic
- UI/instrumented tests for navigation and profile flows
- Tests run on GitHub Actions for every push
- Tests can be triggered on-demand for a specific branch

### CI/CD (GitHub Actions)
- **On every push:** Run unit tests and lint
- **On-demand workflow:** Accept a branch name parameter, run full test suite
- **Release workflow:** Build signed APK/AAB for Google Play deployment

### Local Development
- Project runs in Android Studio with standard emulator
- Gradle-based build system
- No special environment setup beyond Android SDK

## Architecture

```
app/
├── data/
│   ├── db/          # Room database, DAOs, entities
│   └── repository/  # Repository layer
├── ui/
│   ├── profile/     # Profile selection & creation screens
│   ├── menu/        # Game menu screen
│   ├── games/       # Individual game screens
│   │   ├── digits/         # ✅ Full implementation
│   │   ├── subtraction/    # Placeholder
│   │   ├── multiplication/ # Placeholder
│   │   ├── division/       # Placeholder
│   │   ├── mixed/          # Placeholder
│   │   └── speed/          # Placeholder
│   └── theme/       # Material theme, colors, typography
├── navigation/      # Compose Navigation graph
└── di/              # Manual dependency injection
```

### Tech Stack

| Layer        | Technology                    |
|--------------|-------------------------------|
| UI           | Jetpack Compose + Material 3  |
| Navigation   | Compose Navigation            |
| State        | ViewModel + StateFlow         |
| Storage      | Room                          |
| DI           | Manual (no Hilt/Dagger)       |
| Testing      | JUnit 5, Compose Testing, Turbine |
| CI/CD        | GitHub Actions                |
| Build        | Gradle (Kotlin DSL)           |

## Feature Branch Strategy

Each feature is developed on its own branch using git worktrees:

| Feature                  | Branch                          | Status |
|--------------------------|---------------------------------|--------|
| Project scaffold + CI   | `claude/setup-android-math-games-S5UBn` | ✅ Merged |
| Profile system           | `feature/profile-system`        | ✅ Merged |
| Game menu                | `feature/game-menu`             | ✅ Merged |
| Digits game (full)       | `feature/digits-game`           | 🔄 In PR |
| Subtraction placeholder  | `feature/game-subtraction`      | Not started |
| Multiplication placeholder| `feature/game-multiplication`  | Not started |
| Division placeholder     | `feature/game-division`         | Not started |
| Mixed ops placeholder    | `feature/game-mixed`            | Not started |
| Speed round placeholder  | `feature/game-speed`            | Not started |
| Release signing + deploy | `feature/release-pipeline`      | Not started |

## Success Criteria

- ✅ App builds and runs on Android 7.0+ emulator
- ✅ Profile CRUD works fully offline
- ✅ Game menu displays all 6 games
- ✅ Digits game fully playable with 3 modes and 3 difficulty levels
- ✅ Back navigation works from every screen
- ✅ Core game logic tests pass (48/48)
- ✅ Lint passes
- ✅ Development environment fully documented
- 🔄 PR created for Digits game
- ⏳ Remaining 5 game placeholders to be implemented
- ⏳ APK can be built via GitHub Actions (workflow exists, not yet tested)
