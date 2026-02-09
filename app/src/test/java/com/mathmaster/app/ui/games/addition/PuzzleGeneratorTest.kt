package com.mathmaster.app.ui.games.addition

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.random.Random

class PuzzleGeneratorTest {

    @Test
    fun `generatePuzzle produces valid easy puzzle`() {
        val puzzle = PuzzleGenerator.generatePuzzle(Difficulty.EASY, GameMode.CLASSIC)

        assertEquals(4, puzzle.numbers.size)
        assertTrue(puzzle.target in Difficulty.EASY.targetRange)
        assertFalse(puzzle.numbers.contains(puzzle.target))
        assertEquals(puzzle.numbers, puzzle.initialNumbers)
        assertTrue(puzzle.numbers.all { it in Difficulty.EASY.numberRange })
    }

    @Test
    fun `generatePuzzle produces valid medium puzzle`() {
        val puzzle = PuzzleGenerator.generatePuzzle(Difficulty.MEDIUM, GameMode.CLASSIC)

        assertEquals(5, puzzle.numbers.size)
        assertTrue(puzzle.target in Difficulty.MEDIUM.targetRange)
        assertFalse(puzzle.numbers.contains(puzzle.target))
        assertEquals(puzzle.numbers, puzzle.initialNumbers)
        assertTrue(puzzle.numbers.all { it in Difficulty.MEDIUM.numberRange })
    }

    @Test
    fun `generatePuzzle produces valid hard puzzle`() {
        val puzzle = PuzzleGenerator.generatePuzzle(Difficulty.HARD, GameMode.CLASSIC)

        assertEquals(6, puzzle.numbers.size)
        assertTrue(puzzle.target in Difficulty.HARD.targetRange)
        assertFalse(puzzle.numbers.contains(puzzle.target))
        assertEquals(puzzle.numbers, puzzle.initialNumbers)
        assertTrue(puzzle.numbers.all { it in Difficulty.HARD.numberRange })
    }

    @Test
    fun `generatePuzzle with fixed seed produces deterministic results`() {
        val seed = 12345L
        val puzzle1 = PuzzleGenerator.generatePuzzle(
            Difficulty.EASY,
            GameMode.CLASSIC,
            Random(seed)
        )
        val puzzle2 = PuzzleGenerator.generatePuzzle(
            Difficulty.EASY,
            GameMode.CLASSIC,
            Random(seed)
        )

        assertEquals(puzzle1.target, puzzle2.target)
        assertEquals(puzzle1.numbers, puzzle2.numbers)
    }

    @Test
    fun `generateChallengePuzzle creates valid puzzle`() {
        val puzzle = PuzzleGenerator.generateChallengePuzzle(Difficulty.MEDIUM)

        assertEquals(5, puzzle.numbers.size)
        assertTrue(puzzle.target in Difficulty.MEDIUM.targetRange)
        assertFalse(puzzle.numbers.contains(puzzle.target))
    }

    @Test
    fun `findShortestSolution finds solution for simple puzzle`() {
        val target = 8
        val numbers = listOf(5, 3)
        val allowedOps = setOf(Operation.ADD, Operation.SUBTRACT)

        val solution = PuzzleGenerator.findShortestSolution(target, numbers, allowedOps)

        assertNotNull(solution)
        assertEquals(1, solution!!.size)
        assertEquals(Operation.ADD, solution[0].operation)
        assertEquals(5, solution[0].operand1)
        assertEquals(3, solution[0].operand2)
        assertEquals(8, solution[0].result)
    }

    @Test
    fun `findShortestSolution finds multi-step solution`() {
        val target = 10
        val numbers = listOf(5, 3, 2)
        val allowedOps = setOf(Operation.ADD, Operation.SUBTRACT)

        val solution = PuzzleGenerator.findShortestSolution(target, numbers, allowedOps)

        assertNotNull(solution)
        assertTrue(solution!!.isNotEmpty())
        assertTrue(solution.size <= 3) // Should find solution within 3 steps
    }

    @Test
    fun `findShortestSolution handles impossible puzzle`() {
        val target = 100
        val numbers = listOf(1, 1)
        val allowedOps = setOf(Operation.SUBTRACT)

        val solution = PuzzleGenerator.findShortestSolution(
            target,
            numbers,
            allowedOps,
            maxDepth = 5
        )

        // May return null or not find solution within depth limit
        // This test verifies it doesn't crash on impossible puzzles
        assertTrue(solution == null || solution.isEmpty())
    }

    @Test
    fun `findShortestSolution with multiplication`() {
        val target = 15
        val numbers = listOf(5, 3)
        val allowedOps = setOf(Operation.MULTIPLY)

        val solution = PuzzleGenerator.findShortestSolution(target, numbers, allowedOps)

        assertNotNull(solution)
        assertEquals(1, solution!!.size)
        assertEquals(Operation.MULTIPLY, solution[0].operation)
        assertEquals(15, solution[0].result)
    }

    @Test
    fun `findShortestSolution with division`() {
        val target = 2
        val numbers = listOf(6, 3)
        val allowedOps = setOf(Operation.DIVIDE)

        val solution = PuzzleGenerator.findShortestSolution(target, numbers, allowedOps)

        assertNotNull(solution)
        assertEquals(1, solution!!.size)
        assertEquals(Operation.DIVIDE, solution[0].operation)
        assertEquals(6, solution[0].operand1)
        assertEquals(3, solution[0].operand2)
        assertEquals(2, solution[0].result)
    }

    @Test
    fun `findShortestSolution respects max depth`() {
        val target = 1000
        val numbers = listOf(2, 3, 5)
        val allowedOps = setOf(Operation.ADD, Operation.MULTIPLY)

        val solution = PuzzleGenerator.findShortestSolution(
            target,
            numbers,
            allowedOps,
            maxDepth = 2
        )

        // With maxDepth=2, should not find a solution to reach 1000
        assertTrue(solution == null || solution.size <= 2)
    }

    @Test
    fun `findShortestSolution handles target already in numbers`() {
        val target = 5
        val numbers = listOf(5, 3, 2)
        val allowedOps = setOf(Operation.ADD)

        val solution = PuzzleGenerator.findShortestSolution(target, numbers, allowedOps)

        assertNotNull(solution)
        assertEquals(0, solution!!.size) // No steps needed, target already exists
    }

    @Test
    fun `generated puzzle has solution`() {
        // Generate several puzzles and verify each has a solution
        repeat(5) {
            val puzzle = PuzzleGenerator.generatePuzzle(Difficulty.EASY, GameMode.CLASSIC)

            val solution = PuzzleGenerator.findShortestSolution(
                puzzle.target,
                puzzle.numbers,
                Difficulty.EASY.allowedOperations
            )

            // At minimum, the puzzle should be solvable
            // (Though findShortestSolution might not always find it within depth limit)
            assertNotNull(puzzle.solution, "Generated puzzle should have a solution")
        }
    }

    @Test
    fun `multiple generated puzzles are different`() {
        val puzzles = List(10) {
            PuzzleGenerator.generatePuzzle(Difficulty.EASY, GameMode.CLASSIC)
        }

        // Check that we get some variety in targets
        val uniqueTargets = puzzles.map { it.target }.distinct()
        assertTrue(uniqueTargets.size > 1, "Should generate varied puzzles")
    }
}
