package com.mathmaster.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mathmaster.app.MathMasterApplication
import com.mathmaster.app.ui.profile.ProfileSelectionScreen
import com.mathmaster.app.ui.profile.ProfileSelectionViewModel
import com.mathmaster.app.ui.profile.ProfileSelectionViewModelFactory

@Composable
fun MathMasterNavGraph(
    navController: NavHostController,
    application: MathMasterApplication,
    startDestination: String = Routes.PROFILE_SELECTION
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.PROFILE_SELECTION) {
            val viewModel: ProfileSelectionViewModel = viewModel(
                factory = ProfileSelectionViewModelFactory(
                    application.container.profileRepository
                )
            )
            ProfileSelectionScreen(
                viewModel = viewModel,
                onProfileSelected = { profile ->
                    navController.navigate(Routes.GAME_MENU)
                }
            )
        }

        composable(Routes.GAME_MENU) {
            // Game menu screen will be added in Phase 3
        }
    }
}
