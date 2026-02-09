package com.mathmaster.app.ui.games.magicsquare

import com.mathmaster.app.ui.games.Difficulty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.random.Random

class MagicSquarePuzzleGeneratorTest {

    @Test
    fun `generatePuzzle creates 3x3 grid for EASY difficulty`() {
        val puzzle = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.EASY, Random(42))

        assertEquals(3, puzzle.size)
        assertEquals(3, puzzle.grid.size)
        puzzle.grid.forEach { row ->
            assertEquals(3, row.size)
        }
    }

    @Test
    fun `generatePuzzle creates 4x4 grid for MEDIUM difficulty`() {
        val puzzle = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.MEDIUM, Random(42))

        assertEquals(4, puzzle.size)
        assertEquals(4, puzzle.grid.size)
        puzzle.grid.forEach { row ->
            assertEquals(4, row.size)
        }
    }

    @Test
    fun `generatePuzzle creates 5x5 grid for HARD difficulty`() {
        val puzzle = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.HARD, Random(42))

        assertEquals(5, puzzle.size)
        assertEquals(5, puzzle.grid.size)
        puzzle.grid.forEach { row ->
            assertEquals(5, row.size)
        }
    }

    @Test
    fun `EASY puzzle has correct magic constant`() {
        val puzzle = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.EASY, Random(42))

        assertEquals(15, puzzle.magicConstant)
    }

    @Test
    fun `MEDIUM puzzle has correct magic constant`() {
        val puzzle = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.MEDIUM, Random(42))

        assertEquals(34, puzzle.magicConstant)
    }

    @Test
    fun `HARD puzzle has correct magic constant`() {
        val puzzle = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.HARD, Random(42))

        assertEquals(65, puzzle.magicConstant)
    }

    @Test
    fun `EASY puzzle has correct number of missing cells`() {
        val puzzle = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.EASY, Random(42))

        val editableCells = puzzle.grid.flatten().count { it is CellState.Editable }
        assertTrue(editableCells in 3..4, "Expected 3-4 editable cells, got $editableCells")
    }

    @Test
    fun `MEDIUM puzzle has correct number of missing cells`() {
        val puzzle = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.MEDIUM, Random(42))

        val editableCells = puzzle.grid.flatten().count { it is CellState.Editable }
        assertTrue(editableCells in 6..8, "Expected 6-8 editable cells, got $editableCells")
    }

    @Test
    fun `HARD puzzle has correct number of missing cells`() {
        val puzzle = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.HARD, Random(42))

        val editableCells = puzzle.grid.flatten().count { it is CellState.Editable }
        assertTrue(editableCells in 10..13, "Expected 10-13 editable cells, got $editableCells")
    }

    @Test
    fun `generated 3x3 puzzle has valid structure`() {
        val puzzle = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.EASY, Random(42))

        // Check that all fixed cells have values in valid range
        puzzle.grid.flatten().forEach { cell ->
            if (cell is CellState.Fixed) {
                assertTrue(cell.value in 1..9, "Fixed cell value ${cell.value} not in range 1-9")
            }
        }

        // Check that we have both fixed and editable cells
        val fixedCount = puzzle.grid.flatten().count { it is CellState.Fixed }
        val editableCount = puzzle.grid.flatten().count { it is CellState.Editable }
        assertTrue(fixedCount > 0, "Should have some fixed cells")
        assertTrue(editableCount > 0, "Should have some editable cells")
        assertEquals(9, fixedCount + editableCount)
    }

    @Test
    fun `generated 4x4 puzzle has valid structure`() {
        val puzzle = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.MEDIUM, Random(42))

        puzzle.grid.flatten().forEach { cell ->
            if (cell is CellState.Fixed) {
                assertTrue(cell.value in 1..16, "Fixed cell value ${cell.value} not in range 1-16")
            }
        }

        val fixedCount = puzzle.grid.flatten().count { it is CellState.Fixed }
        val editableCount = puzzle.grid.flatten().count { it is CellState.Editable }
        assertTrue(fixedCount > 0, "Should have some fixed cells")
        assertTrue(editableCount > 0, "Should have some editable cells")
        assertEquals(16, fixedCount + editableCount)
    }

    @Test
    fun `generated 5x5 puzzle has valid structure`() {
        val puzzle = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.HARD, Random(42))

        puzzle.grid.flatten().forEach { cell ->
            if (cell is CellState.Fixed) {
                assertTrue(cell.value in 1..25, "Fixed cell value ${cell.value} not in range 1-25")
            }
        }

        val fixedCount = puzzle.grid.flatten().count { it is CellState.Fixed }
        val editableCount = puzzle.grid.flatten().count { it is CellState.Editable }
        assertTrue(fixedCount > 0, "Should have some fixed cells")
        assertTrue(editableCount > 0, "Should have some editable cells")
        assertEquals(25, fixedCount + editableCount)
    }

    @Test
    fun `different seeds produce different puzzles`() {
        val puzzle1 = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.EASY, Random(1))
        val puzzle2 = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.EASY, Random(2))

        // Check that at least one cell differs
        var foundDifference = false
        for (row in 0 until 3) {
            for (col in 0 until 3) {
                val cell1 = puzzle1.grid[row][col]
                val cell2 = puzzle2.grid[row][col]

                // Compare cell types and values
                when {
                    cell1 is CellState.Fixed && cell2 is CellState.Editable -> {
                        foundDifference = true
                        break
                    }
                    cell1 is CellState.Editable && cell2 is CellState.Fixed -> {
                        foundDifference = true
                        break
                    }
                    cell1 is CellState.Fixed && cell2 is CellState.Fixed -> {
                        if (cell1.value != cell2.value) {
                            foundDifference = true
                            break
                        }
                    }
                }
            }
            if (foundDifference) break
        }

        assertTrue(foundDifference, "Different seeds should produce different puzzles")
    }

    @Test
    fun `puzzle initial status is PLAYING`() {
        val puzzle = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.EASY, Random(42))

        assertEquals(GameStatus.PLAYING, puzzle.status)
    }

    @Test
    fun `3x3 puzzle fixed cells contain unique numbers`() {
        val puzzle = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.EASY, Random(42))

        val fixedNumbers = puzzle.grid.flatten()
            .mapNotNull { cell -> if (cell is CellState.Fixed) cell.value else null }

        assertEquals(fixedNumbers.size, fixedNumbers.toSet().size, "Fixed cells should have unique values")
        fixedNumbers.forEach { num ->
            assertTrue(num in 1..9, "Number $num not in valid range 1-9")
        }
    }

    @Test
    fun `4x4 puzzle fixed cells contain unique numbers`() {
        val puzzle = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.MEDIUM, Random(42))

        val fixedNumbers = puzzle.grid.flatten()
            .mapNotNull { cell -> if (cell is CellState.Fixed) cell.value else null }

        assertEquals(fixedNumbers.size, fixedNumbers.toSet().size, "Fixed cells should have unique values")
        fixedNumbers.forEach { num ->
            assertTrue(num in 1..16, "Number $num not in valid range 1-16")
        }
    }

    @Test
    fun `5x5 puzzle fixed cells contain unique numbers`() {
        val puzzle = MagicSquarePuzzleGenerator.generatePuzzle(Difficulty.HARD, Random(42))

        // Check that fixed cells contain unique numbers in valid range
        val fixedNumbers = puzzle.grid.flatten()
            .mapNotNull { cell -> if (cell is CellState.Fixed) cell.value else null }

        assertEquals(fixedNumbers.size, fixedNumbers.toSet().size, "Fixed cells should have unique values")
        fixedNumbers.forEach { num ->
            assertTrue(num in 1..25, "Number $num not in valid range 1-25")
        }
    }
}
