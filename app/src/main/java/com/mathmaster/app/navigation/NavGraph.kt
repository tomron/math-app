package com.mathmaster.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mathmaster.app.MathMasterApplication
import com.mathmaster.app.ui.games.addition.AdditionGameScreen
import com.mathmaster.app.ui.games.addition.AdditionGameViewModel
import com.mathmaster.app.ui.menu.GameMenuScreen
import com.mathmaster.app.ui.menu.GameMenuViewModel
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
                    navController.navigate("${Routes.GAME_MENU}/${profile.name}")
                }
            )
        }

        composable(
            route = "${Routes.GAME_MENU}/{profileName}",
            arguments = listOf(
                navArgument("profileName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val profileName = backStackEntry.arguments?.getString("profileName") ?: ""
            val viewModel: GameMenuViewModel = viewModel()
            viewModel.setSelectedProfile(profileName)

            GameMenuScreen(
                viewModel = viewModel,
                onGameSelected = { game ->
                    navController.navigate(game.route)
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.GAME_ADDITION) {
            val viewModel: AdditionGameViewModel = viewModel()
            AdditionGameScreen(
                viewModel = viewModel,
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}
