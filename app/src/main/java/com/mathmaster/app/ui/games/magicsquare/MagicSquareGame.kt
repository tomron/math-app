package com.mathmaster.app.ui.games.magicsquare

/**
 * Represents the state of a single cell in the magic square grid.
 */
sealed class CellState {
    /**
     * A fixed cell with a pre-filled value that cannot be edited.
     */
    data class Fixed(val value: Int) : CellState()

    /**
     * An editable cell that the player can fill in.
     * cellValue is null if the cell is empty.
     */
    data class Editable(val cellValue: Int?) : CellState()

    fun getValue(): Int? = when (this) {
        is Fixed -> value
        is Editable -> cellValue
    }

    fun isEditable(): Boolean = this is Editable
}

/**
 * Represents the overall game status.
 */
enum class GameStatus {
    PLAYING,
    WON
}

/**
 * Represents the complete state of a magic square puzzle.
 */
data class MagicSquareState(
    val grid: List<List<CellState>>,
    val size: Int,
    val magicConstant: Int,
    val selectedCell: Pair<Int, Int>? = null,
    val status: GameStatus = GameStatus.PLAYING,
    val message: String? = null
)

/**
 * Core game logic for the Magic Square puzzle.
 */
object MagicSquareGame {

    /**
     * Sets the value of a cell at the given position.
     * Only works for editable cells. Fixed cells are unchanged.
     */
    fun setCellValue(state: MagicSquareState, row: Int, col: Int, value: Int): MagicSquareState {
        if (row !in 0 until state.size || col !in 0 until state.size) {
            return state
        }

        val cell = state.grid[row][col]
        if (!cell.isEditable()) {
            return state
        }

        val newGrid = state.grid.mapIndexed { r, rowList ->
            if (r == row) {
                rowList.mapIndexed { c, cellState ->
                    if (c == col && cellState is CellState.Editable) {
                        CellState.Editable(cellValue = value)
                    } else {
                        cellState
                    }
                }
            } else {
                rowList
            }
        }

        return state.copy(grid = newGrid)
    }

    /**
     * Clears the value of an editable cell at the given position.
     * No-op for fixed cells.
     */
    fun clearCell(state: MagicSquareState, row: Int, col: Int): MagicSquareState {
        if (row !in 0 until state.size || col !in 0 until state.size) {
            return state
        }

        val cell = state.grid[row][col]
        if (!cell.isEditable()) {
            return state
        }

        val newGrid = state.grid.mapIndexed { r, rowList ->
            if (r == row) {
                rowList.mapIndexed { c, cellState ->
                    if (c == col && cellState is CellState.Editable) {
                        CellState.Editable(cellValue = null)
                    } else {
                        cellState
                    }
                }
            } else {
                rowList
            }
        }

        return state.copy(grid = newGrid)
    }

    /**
     * Checks if the current grid state represents a valid solution.
     * Returns true if all cells are filled and every row and column sums to the magic constant.
     */
    fun checkSolution(state: MagicSquareState): Boolean {
        // Check if all cells are filled
        for (row in state.grid) {
            for (cell in row) {
                if (cell.getValue() == null) {
                    return false
                }
            }
        }

        // Check all rows
        for (i in 0 until state.size) {
            val rowSum = getRowSum(state, i)
            if (rowSum != state.magicConstant) {
                return false
            }
        }

        // Check all columns
        for (i in 0 until state.size) {
            val colSum = getColSum(state, i)
            if (colSum != state.magicConstant) {
                return false
            }
        }

        return true
    }

    /**
     * Calculates the sum of values in the specified row.
     * Returns null if any cell in the row is empty.
     */
    fun getRowSum(state: MagicSquareState, row: Int): Int? {
        if (row !in 0 until state.size) {
            return null
        }

        var sum = 0
        for (cell in state.grid[row]) {
            val value = cell.getValue() ?: return null
            sum += value
        }
        return sum
    }

    /**
     * Calculates the sum of values in the specified column.
     * Returns null if any cell in the column is empty.
     */
    fun getColSum(state: MagicSquareState, col: Int): Int? {
        if (col !in 0 until state.size) {
            return null
        }

        var sum = 0
        for (row in 0 until state.size) {
            val value = state.grid[row][col].getValue() ?: return null
            sum += value
        }
        return sum
    }
}
