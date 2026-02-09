package com.mathmaster.app.ui.games

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.mathmaster.app.ui.theme.MathMasterTheme
import org.junit.Rule
import org.junit.Test

class BaseGameScaffoldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun scaffold_displaysTitle() {
        composeTestRule.setContent {
            MathMasterTheme {
                BaseGameScaffold(
                    title = "Test Game",
                    selectedDifficulty = Difficulty.EASY,
                    onDifficultyChanged = {},
                    onBackPressed = {}
                ) {
                    Box(Modifier.fillMaxSize())
                }
            }
        }

        composeTestRule.onNodeWithText("Test Game").assertIsDisplayed()
    }

    @Test
    fun scaffold_displaysBackButton() {
        composeTestRule.setContent {
            MathMasterTheme {
                BaseGameScaffold(
                    title = "Test Game",
                    selectedDifficulty = Difficulty.EASY,
                    onDifficultyChanged = {},
                    onBackPressed = {}
                ) {
                    Box(Modifier.fillMaxSize())
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Back to menu").assertIsDisplayed()
    }

    @Test
    fun scaffold_displaysSelectedDifficulty() {
        composeTestRule.setContent {
            MathMasterTheme {
                BaseGameScaffold(
                    title = "Test Game",
                    selectedDifficulty = Difficulty.MEDIUM,
                    onDifficultyChanged = {},
                    onBackPressed = {}
                ) {
                    Box(Modifier.fillMaxSize())
                }
            }
        }

        composeTestRule.onNodeWithText("Medium").assertIsDisplayed()
    }

    @Test
    fun backButton_triggersCallback() {
        var backPressed = false

        composeTestRule.setContent {
            MathMasterTheme {
                BaseGameScaffold(
                    title = "Test Game",
                    selectedDifficulty = Difficulty.EASY,
                    onDifficultyChanged = {},
                    onBackPressed = { backPressed = true }
                ) {
                    Box(Modifier.fillMaxSize())
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Back to menu").performClick()

        assert(backPressed)
    }

    @Test
    fun difficultySelector_opensMenu() {
        composeTestRule.setContent {
            MathMasterTheme {
                BaseGameScaffold(
                    title = "Test Game",
                    selectedDifficulty = Difficulty.EASY,
                    onDifficultyChanged = {},
                    onBackPressed = {}
                ) {
                    Box(Modifier.fillMaxSize())
                }
            }
        }

        // Click on difficulty selector
        composeTestRule.onNodeWithText("Easy").performClick()

        // Check that all difficulty options are shown
        composeTestRule.onAllNodesWithText("Easy").assertCountEquals(2)
        composeTestRule.onNodeWithText("Medium").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hard").assertIsDisplayed()
    }

    @Test
    fun difficultySelector_changesdifficulty() {
        var selectedDifficulty = Difficulty.EASY

        composeTestRule.setContent {
            MathMasterTheme {
                BaseGameScaffold(
                    title = "Test Game",
                    selectedDifficulty = selectedDifficulty,
                    onDifficultyChanged = { selectedDifficulty = it },
                    onBackPressed = {}
                ) {
                    Box(Modifier.fillMaxSize())
                }
            }
        }

        // Open difficulty menu
        composeTestRule.onNodeWithText("Easy").performClick()

        // Select Hard
        composeTestRule.onAllNodesWithText("Hard")[0].performClick()

        assert(selectedDifficulty == Difficulty.HARD)
    }

    @Test
    fun scaffold_rendersContent() {
        composeTestRule.setContent {
            MathMasterTheme {
                BaseGameScaffold(
                    title = "Test Game",
                    selectedDifficulty = Difficulty.EASY,
                    onDifficultyChanged = {},
                    onBackPressed = {}
                ) { padding ->
                    Text("Game Content")
                }
            }
        }

        composeTestRule.onNodeWithText("Game Content").assertIsDisplayed()
    }
}
