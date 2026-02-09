package com.mathmaster.app.ui.games.magicsquare

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MagicSquareGameTest {

    @Test
    fun `setCellValue updates editable cell`() {
        val grid = listOf(
            listOf(CellState.Fixed(2), CellState.Editable(null), CellState.Fixed(6)),
            listOf(CellState.Fixed(9), CellState.Fixed(5), CellState.Fixed(1)),
            listOf(CellState.Fixed(4), CellState.Fixed(3), CellState.Fixed(8))
        )
        val state = MagicSquareState(grid = grid, size = 3, magicConstant = 15)

        val newState = MagicSquareGame.setCellValue(state, 0, 1, 7)

        val updatedCell = newState.grid[0][1]
        assertTrue(updatedCell is CellState.Editable)
        assertEquals(7, (updatedCell as CellState.Editable).cellValue)
    }

    @Test
    fun `setCellValue does not update fixed cell`() {
        val grid = listOf(
            listOf(CellState.Fixed(2), CellState.Fixed(7), CellState.Fixed(6)),
            listOf(CellState.Fixed(9), CellState.Fixed(5), CellState.Fixed(1)),
            listOf(CellState.Fixed(4), CellState.Fixed(3), CellState.Fixed(8))
        )
        val state = MagicSquareState(grid = grid, size = 3, magicConstant = 15)

        val newState = MagicSquareGame.setCellValue(state, 0, 1, 99)

        val cell = newState.grid[0][1]
        assertTrue(cell is CellState.Fixed)
        assertEquals(7, (cell as CellState.Fixed).value)
    }

    @Test
    fun `setCellValue with out of bounds returns unchanged state`() {
        val grid = listOf(
            listOf(CellState.Fixed(2), CellState.Editable(null), CellState.Fixed(6))
        )
        val state = MagicSquareState(grid = grid, size = 1, magicConstant = 15)

        val newState = MagicSquareGame.setCellValue(state, 5, 5, 7)

        assertEquals(state, newState)
    }

    @Test
    fun `clearCell clears editable cell`() {
        val grid = listOf(
            listOf(CellState.Fixed(2), CellState.Editable(7), CellState.Fixed(6)),
            listOf(CellState.Fixed(9), CellState.Fixed(5), CellState.Fixed(1)),
            listOf(CellState.Fixed(4), CellState.Fixed(3), CellState.Fixed(8))
        )
        val state = MagicSquareState(grid = grid, size = 3, magicConstant = 15)

        val newState = MagicSquareGame.clearCell(state, 0, 1)

        val clearedCell = newState.grid[0][1]
        assertTrue(clearedCell is CellState.Editable)
        assertNull((clearedCell as CellState.Editable).cellValue)
    }

    @Test
    fun `clearCell does not affect fixed cell`() {
        val grid = listOf(
            listOf(CellState.Fixed(2), CellState.Fixed(7), CellState.Fixed(6)),
            listOf(CellState.Fixed(9), CellState.Fixed(5), CellState.Fixed(1)),
            listOf(CellState.Fixed(4), CellState.Fixed(3), CellState.Fixed(8))
        )
        val state = MagicSquareState(grid = grid, size = 3, magicConstant = 15)

        val newState = MagicSquareGame.clearCell(state, 0, 0)

        val cell = newState.grid[0][0]
        assertTrue(cell is CellState.Fixed)
        assertEquals(2, (cell as CellState.Fixed).value)
    }

    @Test
    fun `checkSolution returns true for valid complete grid`() {
        // Classic 3x3 magic square
        val grid = listOf(
            listOf(CellState.Fixed(2), CellState.Fixed(7), CellState.Fixed(6)),
            listOf(CellState.Fixed(9), CellState.Fixed(5), CellState.Fixed(1)),
            listOf(CellState.Fixed(4), CellState.Fixed(3), CellState.Fixed(8))
        )
        val state = MagicSquareState(grid = grid, size = 3, magicConstant = 15)

        val result = MagicSquareGame.checkSolution(state)

        assertTrue(result)
    }

    @Test
    fun `checkSolution returns false for incomplete grid`() {
        val grid = listOf(
            listOf(CellState.Fixed(2), CellState.Editable(null), CellState.Fixed(6)),
            listOf(CellState.Fixed(9), CellState.Fixed(5), CellState.Fixed(1)),
            listOf(CellState.Fixed(4), CellState.Fixed(3), CellState.Fixed(8))
        )
        val state = MagicSquareState(grid = grid, size = 3, magicConstant = 15)

        val result = MagicSquareGame.checkSolution(state)

        assertFalse(result)
    }

    @Test
    fun `checkSolution returns false for wrong sums`() {
        val grid = listOf(
            listOf(CellState.Fixed(1), CellState.Fixed(2), CellState.Fixed(3)),
            listOf(CellState.Fixed(4), CellState.Fixed(5), CellState.Fixed(6)),
            listOf(CellState.Fixed(7), CellState.Fixed(8), CellState.Fixed(9))
        )
        val state = MagicSquareState(grid = grid, size = 3, magicConstant = 15)

        val result = MagicSquareGame.checkSolution(state)

        assertFalse(result)
    }

    @Test
    fun `getRowSum calculates correct sum`() {
        val grid = listOf(
            listOf(CellState.Fixed(2), CellState.Fixed(7), CellState.Fixed(6)),
            listOf(CellState.Fixed(9), CellState.Fixed(5), CellState.Fixed(1)),
            listOf(CellState.Fixed(4), CellState.Fixed(3), CellState.Fixed(8))
        )
        val state = MagicSquareState(grid = grid, size = 3, magicConstant = 15)

        assertEquals(15, MagicSquareGame.getRowSum(state, 0))
        assertEquals(15, MagicSquareGame.getRowSum(state, 1))
        assertEquals(15, MagicSquareGame.getRowSum(state, 2))
    }

    @Test
    fun `getRowSum returns null for incomplete row`() {
        val grid = listOf(
            listOf(CellState.Fixed(2), CellState.Editable(null), CellState.Fixed(6)),
            listOf(CellState.Fixed(9), CellState.Fixed(5), CellState.Fixed(1)),
            listOf(CellState.Fixed(4), CellState.Fixed(3), CellState.Fixed(8))
        )
        val state = MagicSquareState(grid = grid, size = 3, magicConstant = 15)

        assertNull(MagicSquareGame.getRowSum(state, 0))
        assertEquals(15, MagicSquareGame.getRowSum(state, 1))
    }

    @Test
    fun `getRowSum returns null for out of bounds`() {
        val grid = listOf(
            listOf(CellState.Fixed(2), CellState.Fixed(7), CellState.Fixed(6))
        )
        val state = MagicSquareState(grid = grid, size = 1, magicConstant = 15)

        assertNull(MagicSquareGame.getRowSum(state, 5))
    }

    @Test
    fun `getColSum calculates correct sum`() {
        val grid = listOf(
            listOf(CellState.Fixed(2), CellState.Fixed(7), CellState.Fixed(6)),
            listOf(CellState.Fixed(9), CellState.Fixed(5), CellState.Fixed(1)),
            listOf(CellState.Fixed(4), CellState.Fixed(3), CellState.Fixed(8))
        )
        val state = MagicSquareState(grid = grid, size = 3, magicConstant = 15)

        assertEquals(15, MagicSquareGame.getColSum(state, 0))
        assertEquals(15, MagicSquareGame.getColSum(state, 1))
        assertEquals(15, MagicSquareGame.getColSum(state, 2))
    }

    @Test
    fun `getColSum returns null for incomplete column`() {
        val grid = listOf(
            listOf(CellState.Fixed(2), CellState.Fixed(7), CellState.Fixed(6)),
            listOf(CellState.Editable(null), CellState.Fixed(5), CellState.Fixed(1)),
            listOf(CellState.Fixed(4), CellState.Fixed(3), CellState.Fixed(8))
        )
        val state = MagicSquareState(grid = grid, size = 3, magicConstant = 15)

        assertNull(MagicSquareGame.getColSum(state, 0))
        assertEquals(15, MagicSquareGame.getColSum(state, 1))
    }

    @Test
    fun `getColSum returns null for out of bounds`() {
        val grid = listOf(
            listOf(CellState.Fixed(2))
        )
        val state = MagicSquareState(grid = grid, size = 1, magicConstant = 15)

        assertNull(MagicSquareGame.getColSum(state, 5))
    }
}
