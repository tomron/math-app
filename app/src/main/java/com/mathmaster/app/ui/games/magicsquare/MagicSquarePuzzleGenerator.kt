package com.mathmaster.app.ui.games.magicsquare

import com.mathmaster.app.ui.games.Difficulty
import kotlin.random.Random

/**
 * Generates Magic Square puzzles with varying difficulty levels.
 */
object MagicSquarePuzzleGenerator {

    /**
     * Generates a new magic square puzzle based on the given difficulty.
     */
    fun generatePuzzle(difficulty: Difficulty, random: Random = Random.Default): MagicSquareState {
        val size = when (difficulty) {
            Difficulty.EASY -> 3
            Difficulty.MEDIUM -> 4
            Difficulty.HARD -> 5
        }

        val magicConstant = when (size) {
            3 -> 15
            4 -> 34
            5 -> 65
            else -> throw IllegalArgumentException("Unsupported size: $size")
        }

        // Generate a complete magic square
        val completedSquare = when (size) {
            3 -> generate3x3MagicSquare(random)
            4 -> generate4x4MagicSquare(random)
            5 -> generate5x5MagicSquare(random)
            else -> throw IllegalArgumentException("Unsupported size: $size")
        }

        // Determine how many cells to remove
        val cellsToRemove = when (difficulty) {
            Difficulty.EASY -> random.nextInt(3, 5)      // 3-4 out of 9
            Difficulty.MEDIUM -> random.nextInt(6, 9)    // 6-8 out of 16
            Difficulty.HARD -> random.nextInt(10, 14)    // 10-13 out of 25
        }

        // Remove cells to create the puzzle
        val puzzleGrid = removeCells(completedSquare, cellsToRemove, random)

        return MagicSquareState(
            grid = puzzleGrid,
            size = size,
            magicConstant = magicConstant,
            status = GameStatus.PLAYING
        )
    }

    /**
     * Generates a 3x3 magic square using the Lo Shu construction.
     * Applies random rotation and reflection for variety.
     */
    private fun generate3x3MagicSquare(random: Random): List<List<Int>> {
        // Base Lo Shu square
        val base = listOf(
            listOf(2, 7, 6),
            listOf(9, 5, 1),
            listOf(4, 3, 8)
        )

        return transformSquare(base, random)
    }

    /**
     * Generates a 4x4 magic square using a known construction.
     * Uses the classic doubly even construction method.
     */
    private fun generate4x4MagicSquare(random: Random): List<List<Int>> {
        // Classic 4x4 magic square construction
        val base = listOf(
            listOf(1, 15, 14, 4),
            listOf(12, 6, 7, 9),
            listOf(8, 10, 11, 5),
            listOf(13, 3, 2, 16)
        )

        return transformSquare(base, random)
    }

    /**
     * Generates a 5x5 magic square using the Siamese (De la Loubère) method.
     */
    private fun generate5x5MagicSquare(random: Random): List<List<Int>> {
        val n = 5
        val square = Array(n) { IntArray(n) }

        // Start position (middle of top row)
        var row = 0
        var col = n / 2

        for (num in 1..n * n) {
            square[row][col] = num

            // Calculate next position (up and right)
            val newRow = (row - 1 + n) % n
            val newCol = (col + 1) % n

            if (square[newRow][newCol] != 0) {
                // If occupied, move down instead
                row = (row + 1) % n
            } else {
                row = newRow
                col = newCol
            }
        }

        val base = square.map { it.toList() }
        return transformSquare(base, random)
    }

    /**
     * Applies random rotation and reflection to a magic square for variety.
     */
    private fun transformSquare(square: List<List<Int>>, random: Random): List<List<Int>> {
        var result = square

        // Random rotation (0°, 90°, 180°, 270°)
        val rotations = random.nextInt(4)
        repeat(rotations) {
            result = rotate90(result)
        }

        // Random reflection (horizontal)
        if (random.nextBoolean()) {
            result = reflectHorizontal(result)
        }

        return result
    }

    /**
     * Rotates a square 90 degrees clockwise.
     */
    private fun rotate90(square: List<List<Int>>): List<List<Int>> {
        val n = square.size
        return List(n) { row ->
            List(n) { col ->
                square[n - 1 - col][row]
            }
        }
    }

    /**
     * Reflects a square horizontally (left-right mirror).
     */
    private fun reflectHorizontal(square: List<List<Int>>): List<List<Int>> {
        return square.map { row -> row.reversed() }
    }

    /**
     * Removes a specified number of cells from the completed square to create the puzzle.
     */
    private fun removeCells(
        completedSquare: List<List<Int>>,
        count: Int,
        random: Random
    ): List<List<CellState>> {
        val size = completedSquare.size
        val positions = mutableListOf<Pair<Int, Int>>()

        // Collect all positions
        for (row in 0 until size) {
            for (col in 0 until size) {
                positions.add(Pair(row, col))
            }
        }

        // Shuffle and select cells to remove
        positions.shuffle(random)
        val toRemove = positions.take(count).toSet()

        // Build the puzzle grid
        return completedSquare.mapIndexed { row, rowList ->
            rowList.mapIndexed { col, value ->
                if (Pair(row, col) in toRemove) {
                    CellState.Editable(cellValue = null)
                } else {
                    CellState.Fixed(value = value)
                }
            }
        }
    }
}
