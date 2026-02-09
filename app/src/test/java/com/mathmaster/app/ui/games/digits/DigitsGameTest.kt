package com.mathmaster.app.ui.games.digits

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DigitsGameTest {

    @Test
    fun `applyOperation with addition`() {
        val state = GameState(
            target = 10,
            numbers = listOf(5, 3, 2),
            initialNumbers = listOf(5, 3, 2)
        )

        val result = applyOperation(state, 0, 1, Operation.ADD)

        assertNotNull(result)
        assertEquals(listOf(2, 8), result!!.numbers.sorted())
        assertEquals(1, result.moveCount)
        assertEquals("5 + 3 = 8", result.message)
    }

    @Test
    fun `applyOperation with subtraction`() {
        val state = GameState(
            target = 10,
            numbers = listOf(5, 3, 2),
            initialNumbers = listOf(5, 3, 2)
        )

        val result = applyOperation(state, 0, 1, Operation.SUBTRACT)

        assertNotNull(result)
        assertEquals(listOf(2, 2), result!!.numbers.sorted())
        assertEquals("5 - 3 = 2", result.message)
    }

    @Test
    fun `applyOperation with multiplication`() {
        val state = GameState(
            target = 10,
            numbers = listOf(5, 3, 2),
            initialNumbers = listOf(5, 3, 2)
        )

        val result = applyOperation(state, 0, 1, Operation.MULTIPLY)

        assertNotNull(result)
        assertEquals(listOf(2, 15), result!!.numbers.sorted())
        assertEquals("5 × 3 = 15", result.message)
    }

    @Test
    fun `applyOperation with valid division`() {
        val state = GameState(
            target = 10,
            numbers = listOf(6, 3, 2),
            initialNumbers = listOf(6, 3, 2)
        )

        val result = applyOperation(state, 0, 1, Operation.DIVIDE)

        assertNotNull(result)
        assertEquals(listOf(2, 2), result!!.numbers.sorted())
        assertEquals("6 ÷ 3 = 2", result.message)
    }

    @Test
    fun `applyOperation with invalid division returns null`() {
        val state = GameState(
            target = 10,
            numbers = listOf(5, 3, 2),
            initialNumbers = listOf(5, 3, 2)
        )

        val result = applyOperation(state, 0, 1, Operation.DIVIDE)

        assertNull(result)
    }

    @Test
    fun `applyOperation with division by zero returns null`() {
        val state = GameState(
            target = 10,
            numbers = listOf(5, 0, 2),
            initialNumbers = listOf(5, 0, 2)
        )

        val result = applyOperation(state, 0, 1, Operation.DIVIDE)

        assertNull(result)
    }

    @Test
    fun `applyOperation with same index returns null`() {
        val state = GameState(
            target = 10,
            numbers = listOf(5, 3, 2),
            initialNumbers = listOf(5, 3, 2)
        )

        val result = applyOperation(state, 0, 0, Operation.ADD)

        assertNull(result)
    }

    @Test
    fun `applyOperation with out of bounds index returns null`() {
        val state = GameState(
            target = 10,
            numbers = listOf(5, 3, 2),
            initialNumbers = listOf(5, 3, 2)
        )

        val result = applyOperation(state, 0, 5, Operation.ADD)

        assertNull(result)
    }

    @Test
    fun `applyOperation saves history`() {
        val state = GameState(
            target = 10,
            numbers = listOf(5, 3, 2),
            initialNumbers = listOf(5, 3, 2)
        )

        val result = applyOperation(state, 0, 1, Operation.ADD)

        assertNotNull(result)
        assertEquals(1, result!!.history.size)
        assertEquals(listOf(5, 3, 2), result.history[0].numbers)
    }

    @Test
    fun `applyOperation detects win condition`() {
        val state = GameState(
            target = 8,
            numbers = listOf(5, 3, 2),
            initialNumbers = listOf(5, 3, 2)
        )

        val result = applyOperation(state, 0, 1, Operation.ADD)

        assertNotNull(result)
        assertEquals(GameStatus.WON, result!!.status)
        assertTrue(result.isWon)
    }

    @Test
    fun `executeMove with valid selection`() {
        val state = GameState(
            target = 10,
            numbers = listOf(5, 3, 2),
            initialNumbers = listOf(5, 3, 2),
            selectedIndices = listOf(0, 1),
            selectedOperation = Operation.ADD
        )

        val result = executeMove(state)

        assertEquals(listOf(2, 8), result.numbers.sorted())
        assertTrue(result.selectedIndices.isEmpty())
        assertNull(result.selectedOperation)
    }

    @Test
    fun `executeMove with no operation selected`() {
        val state = GameState(
            target = 10,
            numbers = listOf(5, 3, 2),
            initialNumbers = listOf(5, 3, 2),
            selectedIndices = listOf(0, 1)
        )

        val result = executeMove(state)

        assertEquals("Select two numbers and an operation", result.message)
        assertEquals(state.numbers, result.numbers)
    }

    @Test
    fun `executeMove with no numbers selected`() {
        val state = GameState(
            target = 10,
            numbers = listOf(5, 3, 2),
            initialNumbers = listOf(5, 3, 2),
            selectedOperation = Operation.ADD
        )

        val result = executeMove(state)

        assertEquals("Select two numbers and an operation", result.message)
        assertEquals(state.numbers, result.numbers)
    }

    @Test
    fun `executeMove tries reverse order for non-commutative operations`() {
        val state = GameState(
            target = 10,
            numbers = listOf(3, 5, 2),
            initialNumbers = listOf(3, 5, 2),
            selectedIndices = listOf(0, 1),
            selectedOperation = Operation.SUBTRACT
        )

        val result = executeMove(state)

        // Should try 3-5=-2 (invalid), then 5-3=2 (valid)
        assertEquals(listOf(2, 2), result.numbers.sorted())
    }

    @Test
    fun `executeMove with invalid operation`() {
        val state = GameState(
            target = 10,
            numbers = listOf(5, 3, 2),
            initialNumbers = listOf(5, 3, 2),
            selectedIndices = listOf(0, 1),
            selectedOperation = Operation.DIVIDE
        )

        val result = executeMove(state)

        assertEquals("Invalid operation", result.message)
        assertEquals(state.numbers, result.numbers)
    }

    @Test
    fun `undoMove restores previous state`() {
        val state = GameState(
            target = 10,
            numbers = listOf(8, 2),
            initialNumbers = listOf(5, 3, 2),
            history = listOf(
                HistoryEntry(
                    numbers = listOf(5, 3, 2),
                    selectedIndices = emptyList(),
                    selectedOperation = null
                )
            ),
            moveCount = 1
        )

        val result = undoMove(state)

        assertEquals(listOf(5, 3, 2), result.numbers)
        assertTrue(result.history.isEmpty())
        assertEquals(0, result.moveCount)
        assertEquals("Move undone", result.message)
    }

    @Test
    fun `undoMove with empty history does nothing`() {
        val state = GameState(
            target = 10,
            numbers = listOf(5, 3, 2),
            initialNumbers = listOf(5, 3, 2)
        )

        val result = undoMove(state)

        assertEquals(state.numbers, result.numbers)
        assertTrue(result.history.isEmpty())
    }

    @Test
    fun `restartPuzzle resets to initial state`() {
        val state = GameState(
            target = 10,
            numbers = listOf(8, 2),
            initialNumbers = listOf(5, 3, 2),
            selectedIndices = listOf(0),
            selectedOperation = Operation.ADD,
            history = listOf(
                HistoryEntry(
                    numbers = listOf(5, 3, 2),
                    selectedIndices = emptyList(),
                    selectedOperation = null
                )
            ),
            moveCount = 1,
            status = GameStatus.WON
        )

        val result = restartPuzzle(state)

        assertEquals(listOf(5, 3, 2), result.numbers)
        assertTrue(result.selectedIndices.isEmpty())
        assertNull(result.selectedOperation)
        assertTrue(result.history.isEmpty())
        assertEquals(0, result.moveCount)
        assertEquals(GameStatus.PLAYING, result.status)
        assertEquals("Puzzle restarted", result.message)
    }

    @Test
    fun `getPreviewMessage shows correct preview`() {
        val state = GameState(
            target = 10,
            numbers = listOf(5, 3, 2),
            initialNumbers = listOf(5, 3, 2),
            selectedIndices = listOf(0, 1),
            selectedOperation = Operation.ADD
        )

        val preview = state.getPreviewMessage()

        assertEquals("5 + 3 = 8", preview)
    }

    @Test
    fun `getPreviewMessage returns empty when incomplete`() {
        val state = GameState(
            target = 10,
            numbers = listOf(5, 3, 2),
            initialNumbers = listOf(5, 3, 2),
            selectedIndices = listOf(0)
        )

        val preview = state.getPreviewMessage()

        assertEquals("", preview)
    }

    @Test
    fun `getPreviewMessage shows invalid for division`() {
        val state = GameState(
            target = 10,
            numbers = listOf(5, 3, 2),
            initialNumbers = listOf(5, 3, 2),
            selectedIndices = listOf(0, 1),
            selectedOperation = Operation.DIVIDE
        )

        val preview = state.getPreviewMessage()

        assertEquals("Invalid operation", preview)
    }

    @Test
    fun `Operation isCommutative returns correct values`() {
        assertTrue(Operation.ADD.isCommutative)
        assertFalse(Operation.SUBTRACT.isCommutative)
        assertTrue(Operation.MULTIPLY.isCommutative)
        assertFalse(Operation.DIVIDE.isCommutative)
    }

    @Test
    fun `Difficulty configs are correctly set`() {
        assertEquals(4, Difficulty.EASY.numberCount)
        assertEquals(5, Difficulty.MEDIUM.numberCount)
        assertEquals(6, Difficulty.HARD.numberCount)

        assertEquals(2, Difficulty.EASY.allowedOperations.size)
        assertEquals(3, Difficulty.MEDIUM.allowedOperations.size)
        assertEquals(4, Difficulty.HARD.allowedOperations.size)
    }
}
