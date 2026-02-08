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

### 3. Math Games (6 Placeholders)

Each game follows this contract:
- Has its own screen accessible from the menu
- Displays a **top bar** with game name and a **back button** to return to the menu
- Supports **difficulty levels** (Easy / Medium / Hard) selectable before starting
- Initial implementation shows a placeholder UI with game name and difficulty selector

| # | Game             | Description                     |
|---|------------------|---------------------------------|
| 1 | Addition         | Solve addition problems         |
| 2 | Subtraction      | Solve subtraction problems      |
| 3 | Multiplication   | Solve multiplication problems   |
| 4 | Division         | Solve division problems         |
| 5 | Mixed Operations | Random mix of all four ops      |
| 6 | Speed Round      | Timed challenge with any op     |

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
│   │   ├── addition/
│   │   ├── subtraction/
│   │   ├── multiplication/
│   │   ├── division/
│   │   ├── mixed/
│   │   └── speed/
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

| Feature                  | Branch                          |
|--------------------------|---------------------------------|
| Project scaffold + CI   | `claude/setup-android-math-games-S5UBn` |
| Profile system           | `feature/profile-system`        |
| Game menu                | `feature/game-menu`             |
| Game scaffold + nav      | `feature/game-scaffold`         |
| Addition game placeholder| `feature/game-addition`         |
| Subtraction placeholder  | `feature/game-subtraction`      |
| Multiplication placeholder| `feature/game-multiplication`  |
| Division placeholder     | `feature/game-division`         |
| Mixed ops placeholder    | `feature/game-mixed`            |
| Speed round placeholder  | `feature/game-speed`            |
| Release signing + deploy | `feature/release-pipeline`      |

## Success Criteria

- App builds and runs on Android 7.0+ emulator
- Profile CRUD works fully offline
- All 6 game placeholders accessible from menu
- Back navigation works from every screen
- All tests pass in CI
- APK can be built via GitHub Actions
