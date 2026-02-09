package com.mathmaster.app.ui.games.magicsquare

import androidx.lifecycle.ViewModel
import com.mathmaster.app.ui.games.Difficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI state for the Magic Square game screen.
 */
data class MagicSquareUiState(
    val gameState: MagicSquareState,
    val difficulty: Difficulty = Difficulty.EASY,
    val showWinOverlay: Boolean = false
)

/**
 * ViewModel for the Magic Square game.
 */
class MagicSquareViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        MagicSquareUiState(
            gameState = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.EASY),
            difficulty = Difficulty.EASY
        )
    )
    val uiState: StateFlow<MagicSquareUiState> = _uiState.asStateFlow()

    /**
     * Selects a cell at the given position if it's editable.
     */
    fun selectCell(row: Int, col: Int) {
        _uiState.update { currentState ->
            val cell = currentState.gameState.grid[row][col]
            if (cell.isEditable()) {
                currentState.copy(
                    gameState = currentState.gameState.copy(
                        selectedCell = Pair(row, col)
                    )
                )
            } else {
                currentState
            }
        }
    }

    /**
     * Enters a number into the currently selected cell.
     */
    fun enterNumber(value: Int) {
        _uiState.update { currentState ->
            val selectedCell = currentState.gameState.selectedCell ?: return@update currentState

            val (row, col) = selectedCell
            val newGameState = MagicSquareGame.setCellValue(
                currentState.gameState,
                row,
                col,
                value
            )

            // Check if puzzle is solved
            val isSolved = MagicSquareGame.checkSolution(newGameState)
            val finalGameState = if (isSolved) {
                newGameState.copy(
                    status = GameStatus.WON,
                    message = "Congratulations! You solved it!"
                )
            } else {
                newGameState
            }

            currentState.copy(
                gameState = finalGameState,
                showWinOverlay = isSolved
            )
        }
    }

    /**
     * Clears the value of the currently selected cell.
     */
    fun clearSelectedCell() {
        _uiState.update { currentState ->
            val selectedCell = currentState.gameState.selectedCell ?: return@update currentState

            val (row, col) = selectedCell
            val newGameState = MagicSquareGame.clearCell(currentState.gameState, row, col)

            currentState.copy(gameState = newGameState)
        }
    }

    /**
     * Changes the difficulty level and generates a new puzzle.
     */
    fun setDifficulty(difficulty: Difficulty) {
        _uiState.update { currentState ->
            val newGameState = MagicSquarePuzzleGenerator.generatePuzzle(difficulty)
            MagicSquareUiState(
                gameState = newGameState,
                difficulty = difficulty,
                showWinOverlay = false
            )
        }
    }

    /**
     * Generates a new puzzle at the current difficulty level.
     */
    fun newPuzzle() {
        _uiState.update { currentState ->
            val newGameState = MagicSquarePuzzleGenerator.generatePuzzle(currentState.difficulty)
            currentState.copy(
                gameState = newGameState,
                showWinOverlay = false
            )
        }
    }

    /**
     * Dismisses the win overlay dialog.
     */
    fun dismissWinOverlay() {
        _uiState.update { it.copy(showWinOverlay = false) }
    }
}
