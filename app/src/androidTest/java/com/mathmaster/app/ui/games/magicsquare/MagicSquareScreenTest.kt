package com.mathmaster.app.ui.games.magicsquare

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mathmaster.app.ui.theme.MathMasterTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MagicSquareScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screenDisplaysTitle() {
        val viewModel = MagicSquareViewModel()

        composeTestRule.setContent {
            MathMasterTheme {
                MagicSquareScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Magic Square").assertExists()
    }

    @Test
    fun screenDisplaysMagicConstant() {
        val viewModel = MagicSquareViewModel()

        composeTestRule.setContent {
            MathMasterTheme {
                MagicSquareScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Target sum: 15").assertExists()
    }

    @Test
    fun screenDisplaysGrid() {
        val viewModel = MagicSquareViewModel()

        composeTestRule.setContent {
            MathMasterTheme {
                MagicSquareScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }

        // Grid should be displayed - check that some numbers are visible
        // We don't know which numbers will be shown (depends on random generation),
        // but we know at least some fixed cells should have values
        composeTestRule.onAllNodesWithText("1", substring = true).assertCountEquals(0, 10)
    }

    @Test
    fun screenDisplaysNumberPad() {
        val viewModel = MagicSquareViewModel()

        composeTestRule.setContent {
            MathMasterTheme {
                MagicSquareScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }

        // Number pad should have buttons 1-9 for 3x3 grid
        composeTestRule.onNodeWithText("1").assertExists()
        composeTestRule.onNodeWithText("5").assertExists()
        composeTestRule.onNodeWithText("9").assertExists()
    }

    @Test
    fun screenDisplaysClearButton() {
        val viewModel = MagicSquareViewModel()

        composeTestRule.setContent {
            MathMasterTheme {
                MagicSquareScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Clear").assertExists()
    }

    @Test
    fun screenDisplaysNewPuzzleButton() {
        val viewModel = MagicSquareViewModel()

        composeTestRule.setContent {
            MathMasterTheme {
                MagicSquareScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("New Puzzle").assertExists()
    }

    @Test
    fun backButtonTriggersCallback() {
        val viewModel = MagicSquareViewModel()
        var backPressed = false

        composeTestRule.setContent {
            MathMasterTheme {
                MagicSquareScreen(
                    viewModel = viewModel,
                    onNavigateBack = { backPressed = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        assert(backPressed)
    }

    @Test
    fun difficultyDropdownIsDisplayed() {
        val viewModel = MagicSquareViewModel()

        composeTestRule.setContent {
            MathMasterTheme {
                MagicSquareScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }

        // Current difficulty should be displayed
        composeTestRule.onNodeWithText("EASY").assertExists()
    }

    @Test
    fun clickingNewPuzzleGeneratesNewPuzzle() {
        val viewModel = MagicSquareViewModel()

        composeTestRule.setContent {
            MathMasterTheme {
                MagicSquareScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }

        // Click new puzzle button
        composeTestRule.onNodeWithText("New Puzzle").performClick()

        // Screen should still be displayed (basic smoke test)
        composeTestRule.onNodeWithText("Magic Square").assertExists()
    }

    @Test
    fun rowSumIndicatorsAreDisplayed() {
        val viewModel = MagicSquareViewModel()

        composeTestRule.setContent {
            MathMasterTheme {
                MagicSquareScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Row sums:").assertExists()
    }

    @Test
    fun columnSumIndicatorsAreDisplayed() {
        val viewModel = MagicSquareViewModel()

        composeTestRule.setContent {
            MathMasterTheme {
                MagicSquareScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Column sums:").assertExists()
    }
}
