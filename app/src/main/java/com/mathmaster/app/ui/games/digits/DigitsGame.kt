package com.mathmaster.app.ui.games.digits

/**
 * Difficulty levels with corresponding game configurations.
 */
enum class Difficulty(
    val targetRange: IntRange,
    val numberCount: Int,
    val allowedOperations: Set<Operation>,
    val numberRange: IntRange
) {
    EASY(
        targetRange = 10..50,
        numberCount = 4,
        allowedOperations = setOf(Operation.ADD, Operation.SUBTRACT),
        numberRange = 1..10
    ),
    MEDIUM(
        targetRange = 50..200,
        numberCount = 5,
        allowedOperations = setOf(Operation.ADD, Operation.SUBTRACT, Operation.MULTIPLY),
        numberRange = 1..20
    ),
    HARD(
        targetRange = 100..500,
        numberCount = 6,
        allowedOperations = setOf(Operation.ADD, Operation.SUBTRACT, Operation.MULTIPLY, Operation.DIVIDE),
        numberRange = 1..25
    )
}

/**
 * Arithmetic operations available in the game.
 */
enum class Operation(val symbol: String) {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("×"),
    DIVIDE("÷");

    fun apply(a: Int, b: Int): Int? = when (this) {
        ADD -> a + b
        SUBTRACT -> a - b
        MULTIPLY -> a * b
        DIVIDE -> if (b != 0 && a % b == 0) a / b else null
    }

    val isCommutative: Boolean
        get() = this == ADD || this == MULTIPLY
}

/**
 * Game modes with different time constraints and win conditions.
 */
enum class GameMode {
    CLASSIC,  // No time limit, solve at your own pace
    TIMER,    // 60 second time limit
    CHALLENGE // Continuous puzzles with cumulative timer
}

/**
 * Game status enumeration.
 */
enum class GameStatus {
    PLAYING,
    WON,
    TIMEOUT
}

/**
 * Represents a single move in the game history.
 */
data class HistoryEntry(
    val numbers: List<Int>,
    val selectedIndices: List<Int>,
    val selectedOperation: Operation?
)

/**
 * Represents a step in the solution explanation.
 */
data class SolutionStep(
    val operation: Operation,
    val operand1: Int,
    val operand2: Int,
    val result: Int,
    val description: String
)

/**
 * Statistics for Challenge mode.
 */
data class ChallengeStats(
    val puzzlesSolved: Int = 0,
    val totalTime: Int = 0,
    val currentStreak: Int = 0
)

/**
 * The complete state of a Digits game.
 */
data class GameState(
    val target: Int,
    val numbers: List<Int>,
    val initialNumbers: List<Int>,
    val selectedIndices: List<Int> = emptyList(),
    val selectedOperation: Operation? = null,
    val history: List<HistoryEntry> = emptyList(),
    val status: GameStatus = GameStatus.PLAYING,
    val moveCount: Int = 0,
    val message: String = "",
    val solution: List<SolutionStep>? = null
) {
    /**
     * Checks if the current state represents a win.
     */
    val isWon: Boolean
        get() = numbers.contains(target) && status != GameStatus.TIMEOUT

    /**
     * Gets a preview message showing what would happen if the operation were applied.
     */
    fun getPreviewMessage(): String {
        if (selectedIndices.size != 2 || selectedOperation == null) return ""

        val num1 = numbers[selectedIndices[0]]
        val num2 = numbers[selectedIndices[1]]
        val result = selectedOperation.apply(num1, num2)

        return if (result != null) {
            "$num1 ${selectedOperation.symbol} $num2 = $result"
        } else {
            "Invalid operation"
        }
    }
}

/**
 * Applies an operation to two numbers in the game state.
 * Returns null if the operation is invalid.
 */
fun applyOperation(state: GameState, index1: Int, index2: Int, operation: Operation): GameState? {
    if (index1 == index2) return null
    if (index1 !in state.numbers.indices || index2 !in state.numbers.indices) return null

    val num1 = state.numbers[index1]
    val num2 = state.numbers[index2]
    val result = operation.apply(num1, num2) ?: return null

    // Reject negative results
    if (result <= 0) return null

    // Create new number list with the two numbers replaced by the result
    val newNumbers = state.numbers.toMutableList()
    val indices = listOf(index1, index2).sorted()
    newNumbers.removeAt(indices[1])
    newNumbers.removeAt(indices[0])
    newNumbers.add(result)

    // Save current state to history
    val historyEntry = HistoryEntry(
        numbers = state.numbers,
        selectedIndices = state.selectedIndices,
        selectedOperation = state.selectedOperation
    )

    val newState = state.copy(
        numbers = newNumbers,
        selectedIndices = emptyList(),
        selectedOperation = null,
        history = state.history + historyEntry,
        moveCount = state.moveCount + 1,
        message = "$num1 ${operation.symbol} $num2 = $result"
    )

    // Check for win condition
    return if (newState.isWon) {
        newState.copy(status = GameStatus.WON)
    } else {
        newState
    }
}

/**
 * Executes a move by trying to apply the selected operation to the selected numbers.
 * For non-commutative operations, tries both orderings.
 */
fun executeMove(state: GameState): GameState {
    if (state.selectedIndices.size != 2 || state.selectedOperation == null) {
        return state.copy(message = "Select two numbers and an operation")
    }

    val index1 = state.selectedIndices[0]
    val index2 = state.selectedIndices[1]
    val operation = state.selectedOperation

    // Try applying the operation in the selected order
    val result = applyOperation(state, index1, index2, operation)
    if (result != null) return result

    // If the operation is not commutative, try the reverse order
    if (!operation.isCommutative) {
        val reverseResult = applyOperation(state, index2, index1, operation)
        if (reverseResult != null) return reverseResult
    }

    return state.copy(message = "Invalid operation")
}

/**
 * Undoes the last move.
 */
fun undoMove(state: GameState): GameState {
    if (state.history.isEmpty()) return state

    val lastEntry = state.history.last()
    return state.copy(
        numbers = lastEntry.numbers,
        selectedIndices = emptyList(),
        selectedOperation = null,
        history = state.history.dropLast(1),
        moveCount = maxOf(0, state.moveCount - 1),
        message = "Move undone"
    )
}

/**
 * Restarts the current puzzle.
 */
fun restartPuzzle(state: GameState): GameState {
    return state.copy(
        numbers = state.initialNumbers,
        selectedIndices = emptyList(),
        selectedOperation = null,
        history = emptyList(),
        status = GameStatus.PLAYING,
        moveCount = 0,
        message = "Puzzle restarted"
    )
}
