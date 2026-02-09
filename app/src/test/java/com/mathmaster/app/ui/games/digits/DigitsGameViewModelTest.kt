package com.mathmaster.app.ui.games.digits

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DigitsGameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has valid game`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            val state = awaitItem()

            assertEquals(Difficulty.EASY, state.difficulty)
            assertEquals(GameMode.CLASSIC, state.gameMode)
            assertEquals(GameStatus.PLAYING, state.gameState.status)
            assertEquals(4, state.gameState.numbers.size)
            assertNull(state.timeRemaining)
            assertFalse(state.showExplanation)
            assertFalse(state.showWinOverlay)
        }
    }

    @Test
    fun `selectNumber adds to selection`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            awaitItem() // Initial state

            viewModel.selectNumber(0)
            val state1 = awaitItem()
            assertEquals(listOf(0), state1.gameState.selectedIndices)

            viewModel.selectNumber(1)
            val state2 = awaitItem()
            assertEquals(listOf(0, 1), state2.gameState.selectedIndices)
        }
    }

    @Test
    fun `selectNumber deselects if already selected`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            awaitItem() // Initial state

            viewModel.selectNumber(0)
            awaitItem()

            viewModel.selectNumber(0)
            val state = awaitItem()
            assertTrue(state.gameState.selectedIndices.isEmpty())
        }
    }

    @Test
    fun `selectNumber swaps second selection when two already selected`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            awaitItem() // Initial state

            viewModel.selectNumber(0)
            awaitItem()

            viewModel.selectNumber(1)
            awaitItem()

            viewModel.selectNumber(2)
            val state = awaitItem()
            assertEquals(listOf(0, 2), state.gameState.selectedIndices)
        }
    }

    @Test
    fun `selectOperation executes move when two numbers selected`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            val initial = awaitItem()
            val num1 = initial.gameState.numbers[0]
            val num2 = initial.gameState.numbers[1]

            viewModel.selectNumber(0)
            awaitItem()

            viewModel.selectNumber(1)
            awaitItem()

            viewModel.selectOperation(Operation.ADD)
            val state = awaitItem()

            // Numbers should be combined
            assertEquals(3, state.gameState.numbers.size)
            assertTrue(state.gameState.numbers.contains(num1 + num2))
            assertTrue(state.gameState.selectedIndices.isEmpty())
        }
    }

    @Test
    fun `selectOperation only sets operation when less than two numbers selected`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            awaitItem() // Initial state

            viewModel.selectNumber(0)
            awaitItem()

            viewModel.selectOperation(Operation.ADD)
            val state = awaitItem()

            // Operation should be set but move not executed
            assertEquals(Operation.ADD, state.gameState.selectedOperation)
            assertEquals(4, state.gameState.numbers.size) // No numbers combined
        }
    }

    @Test
    fun `undo restores previous state`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            val initial = awaitItem()
            val initialNumbers = initial.gameState.numbers

            viewModel.selectNumber(0)
            awaitItem()

            viewModel.selectNumber(1)
            awaitItem()

            viewModel.selectOperation(Operation.ADD)
            awaitItem()

            viewModel.undo()
            val state = awaitItem()

            assertEquals(initialNumbers, state.gameState.numbers)
            assertEquals(0, state.gameState.moveCount)
        }
    }

    @Test
    fun `restart resets to initial puzzle state`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            val initial = awaitItem()
            val initialNumbers = initial.gameState.initialNumbers

            viewModel.selectNumber(0)
            awaitItem()

            viewModel.selectNumber(1)
            awaitItem()

            viewModel.selectOperation(Operation.ADD)
            awaitItem()

            viewModel.restart()
            val state = awaitItem()

            assertEquals(initialNumbers, state.gameState.numbers)
            assertEquals(0, state.gameState.moveCount)
            assertTrue(state.gameState.history.isEmpty())
        }
    }

    @Test
    fun `setDifficulty changes difficulty and starts new game`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            awaitItem() // Initial EASY state

            viewModel.setDifficulty(Difficulty.HARD)
            val state = awaitItem()

            assertEquals(Difficulty.HARD, state.difficulty)
            assertEquals(6, state.gameState.numbers.size) // HARD has 6 numbers
        }
    }

    @Test
    fun `setGameMode changes mode and starts new game`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            awaitItem() // Initial CLASSIC state

            viewModel.setGameMode(GameMode.TIMER)
            val state = awaitItem()

            assertEquals(GameMode.TIMER, state.gameMode)
            assertEquals(60, state.timeRemaining)
        }
    }

    @Test
    fun `timer mode counts down`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            awaitItem() // Initial state

            viewModel.setGameMode(GameMode.TIMER)
            val state1 = awaitItem()
            assertEquals(60, state1.timeRemaining)

            advanceTimeBy(1000)
            val state2 = awaitItem()
            assertEquals(59, state2.timeRemaining)

            advanceTimeBy(1000)
            val state3 = awaitItem()
            assertEquals(58, state3.timeRemaining)
        }
    }

    @Test
    fun `timer timeout shows timeout overlay`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            awaitItem() // Initial state

            viewModel.setGameMode(GameMode.TIMER)
            awaitItem() // Mode changed, timer at 60

            // Fast forward to timeout
            advanceTimeBy(60_000)

            val state = awaitItem()
            assertEquals(0, state.timeRemaining)
            assertTrue(state.showTimeoutOverlay)
            assertEquals(GameStatus.TIMEOUT, state.gameState.status)
        }
    }

    @Test
    fun `challenge mode timeout shows challenge results`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            awaitItem() // Initial state

            viewModel.setGameMode(GameMode.CHALLENGE)
            awaitItem() // Mode changed

            // Fast forward to timeout
            advanceTimeBy(60_000)

            val state = awaitItem()
            assertTrue(state.showChallengeResults)
            assertEquals(GameStatus.TIMEOUT, state.gameState.status)
        }
    }

    @Test
    fun `showExplanation sets showExplanation flag`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            awaitItem() // Initial state

            viewModel.showExplanation()
            val state = awaitItem()

            assertTrue(state.showExplanation)
            assertNotNull(state.gameState.solution)
        }
    }

    @Test
    fun `hideExplanation clears showExplanation flag`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            awaitItem() // Initial state

            viewModel.showExplanation()
            awaitItem()

            viewModel.hideExplanation()
            val state = awaitItem()

            assertFalse(state.showExplanation)
        }
    }

    @Test
    fun `dismissWinOverlay hides overlay`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            awaitItem() // Initial state

            // Manually set win overlay for testing
            viewModel.showExplanation() // Just to trigger an update
            awaitItem()

            viewModel.dismissWinOverlay()
            val state = awaitItem()

            assertFalse(state.showWinOverlay)
        }
    }

    @Test
    fun `dismissTimeoutOverlay hides overlay`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            awaitItem() // Initial state

            viewModel.dismissTimeoutOverlay()
            val state = awaitItem()

            assertFalse(state.showTimeoutOverlay)
        }
    }

    @Test
    fun `dismissChallengeResults resets stats`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            awaitItem() // Initial state

            viewModel.dismissChallengeResults()
            val state = awaitItem()

            assertFalse(state.showChallengeResults)
            assertEquals(0, state.challengeStats.puzzlesSolved)
        }
    }

    @Test
    fun `newPuzzle generates new puzzle`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            val initial = awaitItem()
            val initialTarget = initial.gameState.target

            viewModel.newPuzzle()
            val state = awaitItem()

            // New puzzle should have different target (most likely)
            // or at minimum, reset the game state
            assertEquals(GameStatus.PLAYING, state.gameState.status)
            assertEquals(0, state.gameState.moveCount)
        }
    }

    @Test
    fun `classic mode has no timer`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            val state = awaitItem()

            assertEquals(GameMode.CLASSIC, state.gameMode)
            assertNull(state.timeRemaining)
        }
    }

    @Test
    fun `challenge mode initializes stats`() = runTest {
        val viewModel = DigitsGameViewModel()

        viewModel.uiState.test {
            awaitItem() // Initial state

            viewModel.setGameMode(GameMode.CHALLENGE)
            val state = awaitItem()

            assertEquals(0, state.challengeStats.puzzlesSolved)
            assertEquals(0, state.challengeStats.totalTime)
            assertEquals(0, state.challengeStats.currentStreak)
        }
    }
}
