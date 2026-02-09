package com.mathmaster.app.ui.games.addition

import kotlin.random.Random

/**
 * Generates Digits puzzles using forward simulation.
 */
object PuzzleGenerator {

    /**
     * Generates a new puzzle for the given difficulty and mode.
     */
    fun generatePuzzle(difficulty: Difficulty, mode: GameMode, random: Random = Random): GameState {
        var attempts = 0
        val maxAttempts = 100

        while (attempts < maxAttempts) {
            attempts++

            // Generate random starting numbers
            val numbers = List(difficulty.numberCount) {
                random.nextInt(difficulty.numberRange.first, difficulty.numberRange.last + 1)
            }

            // Simulate forward to generate a target
            val (target, solution) = simulateForward(numbers, difficulty.allowedOperations, random)

            // Ensure target is in the desired range and not already in starting numbers
            if (target in difficulty.targetRange && target !in numbers) {
                return GameState(
                    target = target,
                    numbers = numbers,
                    initialNumbers = numbers,
                    solution = solution
                )
            }
        }

        // Fallback: create a simple solvable puzzle
        return createFallbackPuzzle(difficulty)
    }

    /**
     * Simulates forward from starting numbers to generate a target and solution.
     */
    private fun simulateForward(
        startNumbers: List<Int>,
        allowedOps: Set<Operation>,
        random: Random
    ): Pair<Int, List<SolutionStep>> {
        val numbers = startNumbers.toMutableList()
        val solution = mutableListOf<SolutionStep>()
        val operations = allowedOps.toList()

        // Perform 2-4 random operations
        val operationCount = random.nextInt(2, minOf(5, numbers.size))

        repeat(operationCount) {
            if (numbers.size < 2) return@repeat

            // Pick two random numbers
            val index1 = random.nextInt(numbers.size)
            var index2 = random.nextInt(numbers.size)
            while (index2 == index1 && numbers.size > 1) {
                index2 = random.nextInt(numbers.size)
            }

            val num1 = numbers[index1]
            val num2 = numbers[index2]

            // Pick a random operation
            val operation = operations.random(random)

            // Try to apply it
            val result = operation.apply(num1, num2)
            if (result != null && result > 0) {
                // Remove the two numbers and add the result
                val indices = listOf(index1, index2).sorted()
                numbers.removeAt(indices[1])
                numbers.removeAt(indices[0])
                numbers.add(result)

                solution.add(
                    SolutionStep(
                        operation = operation,
                        operand1 = num1,
                        operand2 = num2,
                        result = result,
                        description = "$num1 ${operation.symbol} $num2 = $result"
                    )
                )
            }
        }

        // The target is one of the resulting numbers (preferably the largest)
        val target = numbers.maxOrNull() ?: startNumbers.first()
        return target to solution
    }

    /**
     * Creates a simple fallback puzzle guaranteed to be solvable.
     */
    private fun createFallbackPuzzle(difficulty: Difficulty): GameState {
        val target = when (difficulty) {
            Difficulty.EASY -> 20
            Difficulty.MEDIUM -> 100
            Difficulty.HARD -> 200
        }

        val numbers = when (difficulty) {
            Difficulty.EASY -> listOf(5, 3, 2, 1)
            Difficulty.MEDIUM -> listOf(10, 5, 4, 3, 2)
            Difficulty.HARD -> listOf(25, 10, 8, 5, 4, 2)
        }

        return GameState(
            target = target,
            numbers = numbers,
            initialNumbers = numbers
        )
    }

    /**
     * Generates a new puzzle for Challenge mode, preserving stats.
     */
    fun generateChallengePuzzle(difficulty: Difficulty, random: Random = Random): GameState {
        return generatePuzzle(difficulty, GameMode.CHALLENGE, random)
    }

    /**
     * Finds the shortest solution to a puzzle using BFS.
     * Returns null if no solution is found within reasonable depth.
     */
    fun findShortestSolution(
        target: Int,
        numbers: List<Int>,
        allowedOps: Set<Operation>,
        maxDepth: Int = 10
    ): List<SolutionStep>? {
        // BFS state: (remaining numbers, solution path)
        data class SearchState(
            val numbers: List<Int>,
            val path: List<SolutionStep>
        )

        val queue = ArrayDeque<SearchState>()
        queue.add(SearchState(numbers, emptyList()))
        val visited = mutableSetOf<List<Int>>()

        while (queue.isNotEmpty()) {
            val state = queue.removeFirst()

            // Check if we've reached the target
            if (state.numbers.contains(target)) {
                return state.path
            }

            // Depth limit
            if (state.path.size >= maxDepth) continue

            // Skip if we've seen this number configuration
            val sortedNumbers = state.numbers.sorted()
            if (sortedNumbers in visited) continue
            visited.add(sortedNumbers)

            // Try all combinations of two numbers with all operations
            for (i in state.numbers.indices) {
                for (j in state.numbers.indices) {
                    if (i >= j) continue

                    val num1 = state.numbers[i]
                    val num2 = state.numbers[j]

                    for (op in allowedOps) {
                        // Try both orderings for non-commutative operations
                        val pairs = if (op.isCommutative) {
                            listOf(num1 to num2)
                        } else {
                            listOf(num1 to num2, num2 to num1)
                        }

                        for ((a, b) in pairs) {
                            val result = op.apply(a, b)
                            if (result != null && result > 0) {
                                val newNumbers = state.numbers.toMutableList()
                                newNumbers.removeAt(maxOf(i, j))
                                newNumbers.removeAt(minOf(i, j))
                                newNumbers.add(result)

                                val newStep = SolutionStep(
                                    operation = op,
                                    operand1 = a,
                                    operand2 = b,
                                    result = result,
                                    description = "$a ${op.symbol} $b = $result"
                                )

                                queue.add(
                                    SearchState(
                                        numbers = newNumbers,
                                        path = state.path + newStep
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        return null // No solution found
    }
}
