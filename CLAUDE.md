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
- **Target SDK:** 34 (Android 14)
- **CI/CD:** GitHub Actions

## Project Structure

```
app/src/main/java/com/mathmaster/app/
├── data/
│   ├── db/              # Room: AppDatabase, DAOs, Entities
│   │   ├── AppDatabase.kt
│   │   ├── ProfileDao.kt
│   │   └── ProfileEntity.kt
│   └── repository/      # Repository pattern over DAOs
│       └── ProfileRepository.kt
├── ui/
│   ├── profile/         # Profile selection & creation
│   │   ├── ProfileSelectionScreen.kt
│   │   ├── ProfileSelectionViewModel.kt
│   │   └── ProfileSelectionViewModelFactory.kt
│   ├── menu/            # Game menu grid
│   │   ├── GameMenuScreen.kt
│   │   └── GameMenuViewModel.kt
│   ├── games/           # One sub-package per game
│   │   ├── addition/
│   │   ├── subtraction/
│   │   ├── multiplication/
│   │   ├── division/
│   │   ├── mixed/
│   │   └── speed/
│   └── theme/           # Material 3 theme definition
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── navigation/          # NavHost + route definitions
│   ├── NavGraph.kt
│   └── Routes.kt
├── di/                  # AppContainer for manual DI
│   └── AppContainer.kt
├── MainActivity.kt
└── MathMasterApplication.kt
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
- Keep code simple and focused - avoid over-engineering
- Only add features, refactoring, or improvements when explicitly requested

### Branching & Git Workflow
- Each feature lives on its own branch (see progress.md)
- Use git worktrees for parallel development
- All branches merge to `main` in order (see Merge Order section)
- Branch naming convention: `feature/<feature-name>` or `claude/<description>`

### Testing
- Every public function gets a unit test
- Every screen gets at least one UI test for core interactions
- Test files: `<ClassName>Test.kt`
- Use fakes/stubs over mocks when possible
- Tests must pass before merging

### Dependencies
- Keep dependencies minimal
- No Hilt/Dagger — use manual DI via AppContainer
- No network libraries (app is offline-only)
- Room for database with version 2.6.1

## Key Commands

```bash
# Build
./gradlew assembleDebug

# Unit tests (uses JUnit 5)
./gradlew test

# Instrumented tests (requires emulator)
./gradlew connectedAndroidTest

# Lint
./gradlew lint

# Run on emulator
./gradlew installDebug

# Clean build
./gradlew clean

# Build release APK
./gradlew assembleRelease
```

## Testing in Detail

### Unit Tests
- Located in `app/src/test/`
- Use JUnit 5 (`org.junit.jupiter`)
- Use Turbine for Flow testing
- Use `kotlinx-coroutines-test` for coroutine testing
- Run with: `./gradlew test`
- Reports generated at: `app/build/reports/tests/testDebugUnitTest/index.html`

### Instrumented Tests
- Located in `app/src/androidTest/`
- Use Compose Testing (`androidx.compose.ui:ui-test-junit4`)
- Use Espresso for view interactions
- Require an emulator or device (API 24+)
- Run with: `./gradlew connectedAndroidTest`

### Test Structure
```kotlin
class MyClassTest {
    @Test
    fun `descriptive test name in backticks`() = runTest {
        // Arrange
        val input = createTestData()

        // Act
        val result = systemUnderTest.doSomething(input)

        // Assert
        assertEquals(expected, result)
    }
}
```

## Linting

```bash
# Run lint
./gradlew lint

# View results
open app/build/reports/lint-results-debug.html
```

Lint runs automatically in CI on every push.

## CI/CD Workflows

### Available Workflows

| Workflow         | Trigger                  | What it does                    |
|------------------|--------------------------|---------------------------------|
| `ci.yml`         | Every push to main/feature/claude branches + PRs | Lint + unit tests + upload reports |
| `test-branch.yml`| Manual (workflow_dispatch) | Full tests + build APK on specified branch |
| `release.yml`    | Manual (workflow_dispatch) | Signed AAB build for Google Play Store |

### CI Workflow Details
- Runs on: Ubuntu latest
- Java: JDK 17 (Temurin distribution)
- Caches Gradle dependencies
- Steps:
  1. Checkout code
  2. Setup JDK 17
  3. Grant execute permission to gradlew
  4. Run lint (`./gradlew lint`)
  5. Run unit tests (`./gradlew test`)
  6. Upload test reports (always, even on failure)
  7. Upload lint reports (always, even on failure)

### Triggering Manual Workflows

**Test a specific branch:**
```bash
# Via GitHub CLI
gh workflow run test-branch.yml -f branch=feature/my-feature

# Via GitHub web UI
# Go to Actions → Test Branch On-Demand → Run workflow → Enter branch name
```

**Create a release build:**
```bash
gh workflow run release.yml
```

### Viewing CI Results
```bash
# List recent workflow runs
gh run list

# View details of a specific run
gh run view <run-id>

# View logs of a failed run
gh run view <run-id> --log-failed
```

## Working with Git Worktrees

This project uses git worktrees extensively for parallel feature development. Each feature branch gets its own working directory.

### Why Worktrees?
- Work on multiple features simultaneously without switching branches
- Keep build artifacts and IDE state separate per feature
- Avoid constant rebuilding when switching contexts
- Test integration between features easily

### Current Worktrees Layout

```
/Users/tomron/code/math-app         # Main repo (main branch)
/Users/tomron/code/math-app-phase2  # feature/profile-system
/Users/tomron/code/math-app-phase3  # feature/game-menu
/Users/tomron/code/math-app-phase4  # feature/game-scaffold
```

### Worktree Commands

**List all worktrees:**
```bash
git worktree list
```

**Create a new worktree:**
```bash
# Create new branch and worktree
git worktree add ../math-app-phase5 -b feature/game-addition

# Create worktree from existing branch
git worktree add ../math-app-phase5 feature/game-addition
```

**Navigate between worktrees:**
```bash
# Work on profile system
cd /Users/tomron/code/math-app-phase2

# Work on game menu
cd /Users/tomron/code/math-app-phase3

# Back to main
cd /Users/tomron/code/math-app
```

**Remove a worktree:**
```bash
# Remove worktree (must have clean working tree)
git worktree remove ../math-app-phase2

# Force remove (use carefully)
git worktree remove --force ../math-app-phase2
```

**Prune stale worktrees:**
```bash
# Clean up worktree metadata for manually deleted directories
git worktree prune

# Dry run to see what would be pruned
git worktree prune --dry-run
```

### Working in Worktrees

**In each worktree, you can:**
- Make commits independently
- Run tests and builds
- Open in Android Studio (each worktree is a separate project)
- Push to remote: `git push origin <branch-name>`

**Rebasing worktree on main:**
```bash
cd /Users/tomron/code/math-app-phase2
git fetch origin
git rebase origin/main
git push -f  # Force push after rebase
```

### Cleaning Up After Merges

**Remove merged branches and their worktrees:**
```bash
# 1. List branches that have been merged
git branch --merged main

# 2. Remove worktrees for merged branches
git worktree remove ../math-app-phase2

# 3. Delete the local branch
git branch -d feature/profile-system

# 4. Delete remote branch (if not already deleted)
git push origin --delete feature/profile-system
```

**Clean up "gone" branches (merged and deleted remotely):**
```bash
# Show branches that are gone on remote
git branch -vv | grep ': gone]'

# Delete all gone branches
git branch -vv | grep ': gone]' | awk '{print $1}' | xargs git branch -D

# Or use the custom skill (if available)
# /clean_gone
```

**Prune remote tracking branches:**
```bash
git fetch --prune
```

## Pull Request Workflow

### Creating a PR

1. **Work in your worktree:**
   ```bash
   cd /Users/tomron/code/math-app-phase2
   # Make changes, write tests
   ```

2. **Commit changes:**
   ```bash
   git add .
   git commit -m "Implement profile system with Room database"
   ```

3. **Run tests locally:**
   ```bash
   ./gradlew test
   ./gradlew lint
   ```

4. **Push to remote:**
   ```bash
   git push origin feature/profile-system
   ```

5. **Create PR via GitHub CLI:**
   ```bash
   gh pr create --title "Phase 2: Profile System" \
                --body "Implements profile CRUD with Room database, ViewModels, and Compose UI"
   ```

   **Or via web UI:**
   - Go to GitHub repository
   - Click "Compare & pull request"
   - Fill in title and description
   - Create PR

### Before Merging

**Pre-merge checklist:**
- [ ] All CI checks passing (green checkmark)
- [ ] Code reviewed and approved
- [ ] All tests passing locally
- [ ] No merge conflicts with base branch
- [ ] Follows merge order (see below)

### Merge Order

**IMPORTANT:** Features must be merged in this specific order:

```
1. claude/setup-android-math-games-S5UBn  →  main (Foundation) ✅
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

See [progress.md](./progress.md) for current status.

### Merging a PR

**Via GitHub CLI:**
```bash
# Squash merge (recommended for clean history)
gh pr merge <number> --squash --delete-branch

# Merge commit (preserves all commits)
gh pr merge <number> --merge --delete-branch

# Rebase merge
gh pr merge <number> --rebase --delete-branch
```

**Via web UI:**
- Go to the PR page
- Click "Squash and merge" (recommended)
- Confirm merge
- Delete branch

### After Merging

1. **Update main:**
   ```bash
   cd /Users/tomron/code/math-app
   git checkout main
   git pull
   ```

2. **Rebase remaining feature branches:**
   ```bash
   cd /Users/tomron/code/math-app-phase3
   git fetch origin
   git rebase origin/main
   git push -f
   ```

3. **Clean up merged worktrees:**
   ```bash
   git worktree remove ../math-app-phase2
   git branch -d feature/profile-system
   ```

### Handling CI Failures

**If CI fails on a PR:**

1. **View the failure:**
   ```bash
   gh pr checks <number>
   gh run view <run-id> --log-failed
   ```

2. **Fix locally in the worktree:**
   ```bash
   cd /Users/tomron/code/math-app-phase2
   # Fix the issue
   ./gradlew test  # Verify fix
   ```

3. **Commit and push:**
   ```bash
   git add .
   git commit -m "Fix failing tests"
   git push
   ```

4. **CI will automatically re-run**

### Rebasing on Main

**When main has moved forward:**

```bash
cd /Users/tomron/code/math-app-phase2
git fetch origin
git rebase origin/main

# If conflicts arise
git status  # See conflicting files
# Fix conflicts in editor
git add <resolved-files>
git rebase --continue

# Force push (rebase rewrites history)
git push -f
```

## Deployment

### Debug Builds (Local)
```bash
# Build debug APK
./gradlew assembleDebug

# APK location
app/build/outputs/apk/debug/app-debug.apk

# Install to connected device
./gradlew installDebug
```

### Release Builds (via CI)

1. **Trigger release workflow:**
   ```bash
   gh workflow run release.yml
   ```

2. **Download AAB artifact:**
   - Go to Actions → Release workflow run
   - Download `app-release` artifact

3. **Upload to Google Play Console:**
   - Internal testing → Create new release
   - Upload AAB
   - Fill in release notes
   - Submit for review

**Note:** Release signing requires keystore configuration in GitHub secrets.

## Architecture Decisions

1. **Manual DI** — The app is small; Hilt adds complexity without proportional benefit.
2. **No network layer** — Fully offline. If network is needed later, add Retrofit + OkHttp.
3. **Room for storage** — Single source of truth for profiles and future score tracking.
4. **Compose Navigation** — Keeps navigation declarative and testable.
5. **JUnit 5** — Modern testing framework with better syntax than JUnit 4.
6. **StateFlow over LiveData** — Better Kotlin coroutines integration.
7. **Git worktrees** — Enables parallel development on multiple features.

## Troubleshooting

### Build Issues

**Gradle sync fails:**
```bash
./gradlew clean
# File → Invalidate Caches → Invalidate and Restart (in Android Studio)
```

**Dependencies not resolving:**
```bash
./gradlew --refresh-dependencies
```

### Test Issues

**JUnit 5 tests not running:**
- Verify `tasks.withType<Test> { useJUnitPlatform() }` in app/build.gradle.kts
- Invalidate caches and restart Android Studio

**Room schema export warning:**
- This is expected (we don't export schemas in this project)
- Safe to ignore or set `exportSchema = false` in `@Database` annotation

### Worktree Issues

**Cannot checkout branch (already in use):**
```bash
git worktree list  # See which worktree is using the branch
cd <worktree-path>  # Navigate to that worktree instead
```

**Stale worktree metadata:**
```bash
git worktree prune
```

### CI Issues

**CI stuck on "waiting for deployment":**
- Check GitHub Actions settings
- Ensure environment approvals are not blocking

**CI fails on lint:**
- Run `./gradlew lint` locally
- Fix issues shown in `app/build/reports/lint-results-debug.html`

## Reference Documents

- [README.md](./README.md) — Project overview and getting started guide
- [PRD.md](./PRD.md) — Full product requirements
- [progress.md](./progress.md) — Implementation status and merge order

## Quick Reference

**Common workflows:**

```bash
# Start new feature
git worktree add ../math-app-new-feature -b feature/new-feature
cd ../math-app-new-feature

# Make changes, test, commit
./gradlew test
git add .
git commit -m "Implement new feature"

# Push and create PR
git push origin feature/new-feature
gh pr create --title "New Feature"

# After merge, clean up
cd /Users/tomron/code/math-app
git pull
git worktree remove ../math-app-new-feature
git branch -d feature/new-feature
```
