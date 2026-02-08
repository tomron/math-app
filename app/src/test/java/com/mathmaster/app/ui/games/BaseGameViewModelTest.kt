package com.mathmaster.app.ui.games

import app.cash.turbine.test
import com.mathmaster.app.ui.games.addition.AdditionGameViewModel
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BaseGameViewModelTest {

    private lateinit var viewModel: BaseGameViewModel

    @BeforeEach
    fun setup() {
        // Use AdditionGameViewModel as a concrete implementation
        viewModel = AdditionGameViewModel()
    }

    @Test
    fun `difficulty starts as EASY`() = runTest {
        viewModel.difficulty.test {
            val difficulty = awaitItem()
            assertEquals(Difficulty.EASY, difficulty)
        }
    }

    @Test
    fun `setDifficulty updates difficulty state`() = runTest {
        viewModel.setDifficulty(Difficulty.MEDIUM)

        viewModel.difficulty.test {
            val difficulty = awaitItem()
            assertEquals(Difficulty.MEDIUM, difficulty)
        }
    }

    @Test
    fun `setDifficulty can change to all difficulty levels`() = runTest {
        viewModel.setDifficulty(Difficulty.HARD)
        viewModel.difficulty.test {
            assertEquals(Difficulty.HARD, awaitItem())
        }

        viewModel.setDifficulty(Difficulty.EASY)
        viewModel.difficulty.test {
            assertEquals(Difficulty.EASY, awaitItem())
        }

        viewModel.setDifficulty(Difficulty.MEDIUM)
        viewModel.difficulty.test {
            assertEquals(Difficulty.MEDIUM, awaitItem())
        }
    }

    @Test
    fun `difficulty enum has correct display names`() {
        assertEquals("Easy", Difficulty.EASY.displayName)
        assertEquals("Medium", Difficulty.MEDIUM.displayName)
        assertEquals("Hard", Difficulty.HARD.displayName)
    }

    @Test
    fun `all difficulty levels exist`() {
        val difficulties = Difficulty.entries
        assertEquals(3, difficulties.size)
        assertTrue(difficulties.contains(Difficulty.EASY))
        assertTrue(difficulties.contains(Difficulty.MEDIUM))
        assertTrue(difficulties.contains(Difficulty.HARD))
    }
}
