package com.mathmaster.app.ui.menu

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameMenuViewModelTest {

    private lateinit var viewModel: GameMenuViewModel

    @BeforeEach
    fun setup() {
        viewModel = GameMenuViewModel()
    }

    @Test
    fun `uiState starts with all games`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(6, state.games.size)
            assertTrue(state.games.any { it.id == GameType.ADDITION })
            assertTrue(state.games.any { it.id == GameType.SUBTRACTION })
            assertTrue(state.games.any { it.id == GameType.MULTIPLICATION })
            assertTrue(state.games.any { it.id == GameType.DIVISION })
            assertTrue(state.games.any { it.id == GameType.MIXED })
            assertTrue(state.games.any { it.id == GameType.SPEED })
        }
    }

    @Test
    fun `uiState starts with empty selectedProfile`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.selectedProfile)
        }
    }

    @Test
    fun `setSelectedProfile updates selectedProfile`() = runTest {
        viewModel.setSelectedProfile("John Doe")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("John Doe", state.selectedProfile)
        }
    }

    @Test
    fun `game definitions have correct properties`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()

            val addition = state.games.find { it.id == GameType.ADDITION }
            assertNotNull(addition)
            assertEquals("Digits", addition?.title)
            assertEquals("game_addition", addition?.route)

            val speed = state.games.find { it.id == GameType.SPEED }
            assertNotNull(speed)
            assertEquals("Speed Round", speed?.title)
            assertEquals("game_speed", speed?.route)
        }
    }
}
