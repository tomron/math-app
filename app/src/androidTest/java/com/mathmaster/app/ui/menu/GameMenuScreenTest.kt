package com.mathmaster.app.ui.menu

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.mathmaster.app.ui.theme.MathMasterTheme
import org.junit.Rule
import org.junit.Test

class GameMenuScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun allGames_areDisplayed() {
        val uiState = GameMenuUiState()

        composeTestRule.setContent {
            MathMasterTheme {
                GameMenuContent(
                    uiState = uiState,
                    onGameSelected = {},
                    onBackPressed = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Addition").assertIsDisplayed()
        composeTestRule.onNodeWithText("Subtraction").assertIsDisplayed()
        composeTestRule.onNodeWithText("Multiplication").assertIsDisplayed()
        composeTestRule.onNodeWithText("Division").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mixed Operations").assertIsDisplayed()
        composeTestRule.onNodeWithText("Speed Round").assertIsDisplayed()
    }

    @Test
    fun gameDescriptions_areDisplayed() {
        val uiState = GameMenuUiState()

        composeTestRule.setContent {
            MathMasterTheme {
                GameMenuContent(
                    uiState = uiState,
                    onGameSelected = {},
                    onBackPressed = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Practice adding numbers").assertIsDisplayed()
        composeTestRule.onNodeWithText("Practice subtracting numbers").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fast-paced math challenges").assertIsDisplayed()
    }

    @Test
    fun profileName_isDisplayed_whenSet() {
        val uiState = GameMenuUiState(selectedProfile = "John Doe")

        composeTestRule.setContent {
            MathMasterTheme {
                GameMenuContent(
                    uiState = uiState,
                    onGameSelected = {},
                    onBackPressed = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Playing as: John Doe").assertIsDisplayed()
    }

    @Test
    fun clickingGame_triggersCallback() {
        val uiState = GameMenuUiState()
        var selectedGame: GameDefinition? = null

        composeTestRule.setContent {
            MathMasterTheme {
                GameMenuContent(
                    uiState = uiState,
                    onGameSelected = { selectedGame = it },
                    onBackPressed = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Addition").performClick()

        assert(selectedGame != null)
        assert(selectedGame?.id == GameType.ADDITION)
    }

    @Test
    fun backButton_triggersCallback() {
        val uiState = GameMenuUiState()
        var backPressed = false

        composeTestRule.setContent {
            MathMasterTheme {
                GameMenuContent(
                    uiState = uiState,
                    onGameSelected = {},
                    onBackPressed = { backPressed = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        assert(backPressed)
    }

    @Test
    fun gameCards_displayIcons() {
        val uiState = GameMenuUiState()

        composeTestRule.setContent {
            MathMasterTheme {
                GameMenuContent(
                    uiState = uiState,
                    onGameSelected = {},
                    onBackPressed = {}
                )
            }
        }

        // Check that icons are displayed (one for each game)
        composeTestRule.onAllNodesWithContentDescription("Addition").assertCountEquals(1)
        composeTestRule.onAllNodesWithContentDescription("Speed Round").assertCountEquals(1)
    }
}
