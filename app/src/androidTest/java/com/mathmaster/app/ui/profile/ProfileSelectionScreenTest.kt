package com.mathmaster.app.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.mathmaster.app.data.db.ProfileEntity
import com.mathmaster.app.ui.theme.MathMasterTheme
import org.junit.Rule
import org.junit.Test

class ProfileSelectionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_showsEmptyMessage() {
        val uiState = ProfileSelectionUiState(profiles = emptyList())

        composeTestRule.setContent {
            MathMasterTheme {
                ProfileSelectionContent(
                    uiState = uiState,
                    onProfileClick = {},
                    onDeleteProfile = {},
                    onCreateProfileClick = {},
                    onDismissDialog = {},
                    onCreateProfile = { _, _ -> }
                )
            }
        }

        composeTestRule
            .onNodeWithText("No profiles yet. Create one to get started!")
            .assertIsDisplayed()
    }

    @Test
    fun withProfiles_displaysProfileList() {
        val profiles = listOf(
            ProfileEntity(id = 1, name = "John Doe", initials = "JD"),
            ProfileEntity(id = 2, name = "Jane Smith", initials = "JS")
        )
        val uiState = ProfileSelectionUiState(profiles = profiles)

        composeTestRule.setContent {
            MathMasterTheme {
                ProfileSelectionContent(
                    uiState = uiState,
                    onProfileClick = {},
                    onDeleteProfile = {},
                    onCreateProfileClick = {},
                    onDismissDialog = {},
                    onCreateProfile = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jane Smith").assertIsDisplayed()
        composeTestRule.onNodeWithText("JD").assertIsDisplayed()
        composeTestRule.onNodeWithText("JS").assertIsDisplayed()
    }

    @Test
    fun clickingProfile_triggersCallback() {
        val profiles = listOf(
            ProfileEntity(id = 1, name = "John Doe", initials = "JD")
        )
        val uiState = ProfileSelectionUiState(profiles = profiles)
        var clickedProfile: ProfileEntity? = null

        composeTestRule.setContent {
            MathMasterTheme {
                ProfileSelectionContent(
                    uiState = uiState,
                    onProfileClick = { clickedProfile = it },
                    onDeleteProfile = {},
                    onCreateProfileClick = {},
                    onDismissDialog = {},
                    onCreateProfile = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("John Doe").performClick()

        assert(clickedProfile != null)
        assert(clickedProfile?.name == "John Doe")
    }

    @Test
    fun createProfileDialog_isShown_whenStateIsTrue() {
        val uiState = ProfileSelectionUiState(showCreateDialog = true)

        composeTestRule.setContent {
            MathMasterTheme {
                ProfileSelectionContent(
                    uiState = uiState,
                    onProfileClick = {},
                    onDeleteProfile = {},
                    onCreateProfileClick = {},
                    onDismissDialog = {},
                    onCreateProfile = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("Create New Profile").assertIsDisplayed()
        composeTestRule.onNodeWithText("Name").assertIsDisplayed()
    }

    @Test
    fun createProfileDialog_showsError_whenPresent() {
        val uiState = ProfileSelectionUiState(
            showCreateDialog = true,
            error = "Profile name already exists"
        )

        composeTestRule.setContent {
            MathMasterTheme {
                ProfileSelectionContent(
                    uiState = uiState,
                    onProfileClick = {},
                    onDeleteProfile = {},
                    onCreateProfileClick = {},
                    onDismissDialog = {},
                    onCreateProfile = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("Profile name already exists").assertIsDisplayed()
    }

    @Test
    fun createButton_isDisabled_whenNameIsEmpty() {
        val uiState = ProfileSelectionUiState(showCreateDialog = true)

        composeTestRule.setContent {
            MathMasterTheme {
                ProfileSelectionContent(
                    uiState = uiState,
                    onProfileClick = {},
                    onDeleteProfile = {},
                    onCreateProfileClick = {},
                    onDismissDialog = {},
                    onCreateProfile = { _, _ -> }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Create")
            .assertIsNotEnabled()
    }

    @Test
    fun floatingActionButton_triggersCreateDialog() {
        val uiState = ProfileSelectionUiState(profiles = emptyList())
        var createClicked = false

        composeTestRule.setContent {
            MathMasterTheme {
                ProfileSelectionContent(
                    uiState = uiState,
                    onProfileClick = {},
                    onDeleteProfile = {},
                    onCreateProfileClick = { createClicked = true },
                    onDismissDialog = {},
                    onCreateProfile = { _, _ -> }
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Create Profile")
            .performClick()

        assert(createClicked)
    }
}
