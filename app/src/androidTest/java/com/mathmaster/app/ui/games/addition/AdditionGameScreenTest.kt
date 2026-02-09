package com.mathmaster.app.ui.games.addition

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.mathmaster.app.ui.theme.MathMasterTheme
import org.junit.Rule
import org.junit.Test

class AdditionGameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun gameScreen_displaysAllElements() {
        composeTestRule.setContent {
            MathMasterTheme {
                val viewModel = AdditionGameViewModel()
                AdditionGameScreen(
                    viewModel = viewModel,
                    onBackPressed = {}
                )
            }
        }

        // Verify top bar
        composeTestRule.onNodeWithText("Digits").assertExists()
        composeTestRule.onNodeWithContentDescription("Back").assertExists()

        // Verify mode selector
        composeTestRule.onNodeWithText("Classic").assertExists()
        composeTestRule.onNodeWithText("Timer").assertExists()
        composeTestRule.onNodeWithText("Challenge").assertExists()

        // Verify difficulty selector
        composeTestRule.onNodeWithText("Easy").assertExists()
        composeTestRule.onNodeWithText("Medium").assertExists()
        composeTestRule.onNodeWithText("Hard").assertExists()

        // Verify target display
        composeTestRule.onNodeWithText("Target").assertExists()

        // Verify action buttons
        composeTestRule.onNodeWithText("Undo").assertExists()
        composeTestRule.onNodeWithText("Restart").assertExists()
        composeTestRule.onNodeWithText("Explain").assertExists()
        composeTestRule.onNodeWithText("New").assertExists()
    }

    @Test
    fun gameScreen_numberTilesClickable() {
        composeTestRule.setContent {
            MathMasterTheme {
                val viewModel = AdditionGameViewModel()
                AdditionGameScreen(
                    viewModel = viewModel,
                    onBackPressed = {}
                )
            }
        }

        // Get the first number and click it
        // Note: We can't easily verify the exact number, but we can verify tiles exist
        composeTestRule.onAllNodes(hasClickAction())
            .filter(hasAnyAncestor(isRoot()))
            .onFirst()
            .assertExists()
    }

    @Test
    fun gameScreen_operationButtonsDisplay() {
        composeTestRule.setContent {
            MathMasterTheme {
                val viewModel = AdditionGameViewModel()
                AdditionGameScreen(
                    viewModel = viewModel,
                    onBackPressed = {}
                )
            }
        }

        // Easy mode should have + and -
        composeTestRule.onNodeWithText("+").assertExists()
        composeTestRule.onNodeWithText("-").assertExists()
    }

    @Test
    fun gameScreen_switchToMediumDifficulty() {
        composeTestRule.setContent {
            MathMasterTheme {
                val viewModel = AdditionGameViewModel()
                AdditionGameScreen(
                    viewModel = viewModel,
                    onBackPressed = {}
                )
            }
        }

        // Click Medium difficulty
        composeTestRule.onNodeWithText("Medium").performClick()

        // Medium mode should have + - ×
        composeTestRule.onNodeWithText("+").assertExists()
        composeTestRule.onNodeWithText("-").assertExists()
        composeTestRule.onNodeWithText("×").assertExists()
    }

    @Test
    fun gameScreen_switchToHardDifficulty() {
        composeTestRule.setContent {
            MathMasterTheme {
                val viewModel = AdditionGameViewModel()
                AdditionGameScreen(
                    viewModel = viewModel,
                    onBackPressed = {}
                )
            }
        }

        // Click Hard difficulty
        composeTestRule.onNodeWithText("Hard").performClick()

        // Hard mode should have all operations
        composeTestRule.onNodeWithText("+").assertExists()
        composeTestRule.onNodeWithText("-").assertExists()
        composeTestRule.onNodeWithText("×").assertExists()
        composeTestRule.onNodeWithText("÷").assertExists()
    }

    @Test
    fun gameScreen_switchToTimerMode() {
        composeTestRule.setContent {
            MathMasterTheme {
                val viewModel = AdditionGameViewModel()
                AdditionGameScreen(
                    viewModel = viewModel,
                    onBackPressed = {}
                )
            }
        }

        // Click Timer mode
        composeTestRule.onNodeWithText("Timer").performClick()

        // Timer should be displayed
        composeTestRule.onNodeWithText("Time: 60s", substring = true).assertExists()
    }

    @Test
    fun gameScreen_switchToChallengeMode() {
        composeTestRule.setContent {
            MathMasterTheme {
                val viewModel = AdditionGameViewModel()
                AdditionGameScreen(
                    viewModel = viewModel,
                    onBackPressed = {}
                )
            }
        }

        // Click Challenge mode
        composeTestRule.onNodeWithText("Challenge").performClick()

        // Timer should be displayed
        composeTestRule.onNodeWithText("Time: 60s", substring = true).assertExists()

        // "Skip" button should replace "Restart"
        composeTestRule.onNodeWithText("Skip").assertExists()
        composeTestRule.onNodeWithText("Restart").assertDoesNotExist()
    }

    @Test
    fun gameScreen_undoButtonInitiallyDisabled() {
        composeTestRule.setContent {
            MathMasterTheme {
                val viewModel = AdditionGameViewModel()
                AdditionGameScreen(
                    viewModel = viewModel,
                    onBackPressed = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Undo").assertIsNotEnabled()
    }

    @Test
    fun gameScreen_restartButtonClickable() {
        composeTestRule.setContent {
            MathMasterTheme {
                val viewModel = AdditionGameViewModel()
                AdditionGameScreen(
                    viewModel = viewModel,
                    onBackPressed = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Restart").assertIsEnabled()
        composeTestRule.onNodeWithText("Restart").performClick()
    }

    @Test
    fun gameScreen_explainButtonClickable() {
        composeTestRule.setContent {
            MathMasterTheme {
                val viewModel = AdditionGameViewModel()
                AdditionGameScreen(
                    viewModel = viewModel,
                    onBackPressed = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Explain").assertIsEnabled()
        composeTestRule.onNodeWithText("Explain").performClick()

        // Explanation dialog should appear
        composeTestRule.onNodeWithText("Solution").assertExists()
    }

    @Test
    fun gameScreen_newPuzzleButtonClickable() {
        composeTestRule.setContent {
            MathMasterTheme {
                val viewModel = AdditionGameViewModel()
                AdditionGameScreen(
                    viewModel = viewModel,
                    onBackPressed = {}
                )
            }
        }

        composeTestRule.onNodeWithText("New").assertIsEnabled()
        composeTestRule.onNodeWithText("New").performClick()
    }

    @Test
    fun gameScreen_backButtonWorks() {
        var backPressed = false

        composeTestRule.setContent {
            MathMasterTheme {
                val viewModel = AdditionGameViewModel()
                AdditionGameScreen(
                    viewModel = viewModel,
                    onBackPressed = { backPressed = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backPressed)
    }

    @Test
    fun gameScreen_moveCounterDisplays() {
        composeTestRule.setContent {
            MathMasterTheme {
                val viewModel = AdditionGameViewModel()
                AdditionGameScreen(
                    viewModel = viewModel,
                    onBackPressed = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Moves: 0").assertExists()
    }
}
