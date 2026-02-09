package com.mathmaster.app.ui.games.digits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Addition/Digits game screen.
 */
data class DigitsGameUiState(
    val gameState: GameState,
    val difficulty: Difficulty = Difficulty.EASY,
    val gameMode: GameMode = GameMode.CLASSIC,
    val timeRemaining: Int? = null,
    val challengeStats: ChallengeStats = ChallengeStats(),
    val showExplanation: Boolean = false,
    val showWinOverlay: Boolean = false,
    val showTimeoutOverlay: Boolean = false,
    val showChallengeResults: Boolean = false
)

/**
 * ViewModel for the Digits game (Addition game slot).
 */
class DigitsGameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        DigitsGameUiState(
            gameState = PuzzleGenerator.generatePuzzle(Difficulty.EASY, GameMode.CLASSIC)
        )
    )
    val uiState: StateFlow<DigitsGameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startNewGame()
    }

    /**
     * Starts a new game with current difficulty and mode.
     */
    private fun startNewGame() {
        timerJob?.cancel()

        val difficulty = _uiState.value.difficulty
        val gameMode = _uiState.value.gameMode
        val newGameState = PuzzleGenerator.generatePuzzle(difficulty, gameMode)

        _uiState.update { state ->
            state.copy(
                gameState = newGameState,
                timeRemaining = if (gameMode == GameMode.CLASSIC) null else 60,
                showExplanation = false,
                showWinOverlay = false,
                showTimeoutOverlay = false,
                showChallengeResults = false
            )
        }

        if (gameMode != GameMode.CLASSIC) {
            startTimer()
        }
    }

    /**
     * Starts the countdown timer for Timer and Challenge modes.
     */
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { state ->
                    val newTime = (state.timeRemaining ?: 0) - 1
                    if (newTime <= 0) {
                        handleTimeout()
                        state.copy(timeRemaining = 0)
                    } else {
                        state.copy(timeRemaining = newTime)
                    }
                }
            }
        }
    }

    /**
     * Handles timeout in Timer or Challenge mode.
     */
    private fun handleTimeout() {
        timerJob?.cancel()

        _uiState.update { state ->
            val newGameState = state.gameState.copy(status = GameStatus.TIMEOUT)

            if (state.gameMode == GameMode.CHALLENGE) {
                state.copy(
                    gameState = newGameState,
                    showChallengeResults = true
                )
            } else {
                state.copy(
                    gameState = newGameState,
                    showTimeoutOverlay = true
                )
            }
        }
    }

    /**
     * Handles number tile selection.
     */
    fun selectNumber(index: Int) {
        _uiState.update { state ->
            val currentIndices = state.gameState.selectedIndices

            val newIndices = when {
                // If already selected, deselect it
                index in currentIndices -> currentIndices - index

                // If two numbers already selected, swap the second one
                currentIndices.size == 2 -> listOf(currentIndices[0], index)

                // Otherwise, add to selection
                else -> currentIndices + index
            }

            state.copy(
                gameState = state.gameState.copy(selectedIndices = newIndices)
            )
        }
    }

    /**
     * Handles operation button selection.
     */
    fun selectOperation(operation: Operation) {
        _uiState.update { state ->
            val newGameState = state.gameState.copy(selectedOperation = operation)

            // If two numbers are selected, execute the move immediately
            if (newGameState.selectedIndices.size == 2) {
                val resultState = executeMove(newGameState)

                // Check for win
                if (resultState.status == GameStatus.WON) {
                    handleWin()
                }

                state.copy(gameState = resultState)
            } else {
                state.copy(gameState = newGameState)
            }
        }
    }

    /**
     * Executes the current move.
     */
    private fun executeMove(gameState: GameState): GameState {
        return com.mathmaster.app.ui.games.digits.executeMove(gameState)
    }

    /**
     * Handles win condition.
     */
    private fun handleWin() {
        timerJob?.cancel()

        _uiState.update { state ->
            if (state.gameMode == GameMode.CHALLENGE) {
                val newStats = state.challengeStats.copy(
                    puzzlesSolved = state.challengeStats.puzzlesSolved + 1,
                    totalTime = state.challengeStats.totalTime + (60 - (state.timeRemaining ?: 0)),
                    currentStreak = state.challengeStats.currentStreak + 1
                )
                state.copy(
                    challengeStats = newStats,
                    showWinOverlay = true
                )
            } else {
                state.copy(showWinOverlay = true)
            }
        }
    }

    /**
     * Undoes the last move.
     */
    fun undo() {
        _uiState.update { state ->
            state.copy(
                gameState = undoMove(state.gameState)
            )
        }
    }

    /**
     * Restarts the current puzzle.
     */
    fun restart() {
        _uiState.update { state ->
            state.copy(
                gameState = restartPuzzle(state.gameState)
            )
        }
    }

    /**
     * Generates a new puzzle (skips current puzzle).
     */
    fun newPuzzle() {
        if (_uiState.value.gameMode == GameMode.CHALLENGE) {
            // In Challenge mode, generate new puzzle but keep timer going
            val newGameState = PuzzleGenerator.generateChallengePuzzle(_uiState.value.difficulty)
            _uiState.update { state ->
                state.copy(
                    gameState = newGameState,
                    showWinOverlay = false
                )
            }
        } else {
            startNewGame()
        }
    }

    /**
     * Skips the current puzzle (Challenge mode).
     */
    fun skipPuzzle() {
        if (_uiState.value.gameMode == GameMode.CHALLENGE) {
            val newGameState = PuzzleGenerator.generateChallengePuzzle(_uiState.value.difficulty)
            _uiState.update { state ->
                state.copy(gameState = newGameState)
            }
        }
    }

    /**
     * Shows the step-by-step solution explanation.
     */
    fun showExplanation() {
        _uiState.update { state ->
            val solution = if (state.gameState.solution != null) {
                state.gameState.solution
            } else {
                PuzzleGenerator.findShortestSolution(
                    state.gameState.target,
                    state.gameState.initialNumbers,
                    state.difficulty.allowedOperations
                )
            }

            state.copy(
                gameState = state.gameState.copy(solution = solution),
                showExplanation = true
            )
        }
    }

    /**
     * Hides the explanation overlay.
     */
    fun hideExplanation() {
        _uiState.update { state ->
            state.copy(showExplanation = false)
        }
    }

    /**
     * Dismisses the win overlay.
     */
    fun dismissWinOverlay() {
        _uiState.update { state ->
            state.copy(showWinOverlay = false)
        }

        if (_uiState.value.gameMode == GameMode.CHALLENGE) {
            newPuzzle()
        }
    }

    /**
     * Dismisses the timeout overlay.
     */
    fun dismissTimeoutOverlay() {
        _uiState.update { state ->
            state.copy(showTimeoutOverlay = false)
        }
    }

    /**
     * Dismisses the challenge results overlay.
     */
    fun dismissChallengeResults() {
        _uiState.update { state ->
            state.copy(
                showChallengeResults = false,
                challengeStats = ChallengeStats()
            )
        }
    }

    /**
     * Changes the difficulty level.
     */
    fun setDifficulty(difficulty: Difficulty) {
        _uiState.update { state ->
            state.copy(difficulty = difficulty)
        }
        startNewGame()
    }

    /**
     * Changes the game mode.
     */
    fun setGameMode(mode: GameMode) {
        _uiState.update { state ->
            state.copy(
                gameMode = mode,
                challengeStats = if (mode == GameMode.CHALLENGE) ChallengeStats() else state.challengeStats
            )
        }
        startNewGame()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
