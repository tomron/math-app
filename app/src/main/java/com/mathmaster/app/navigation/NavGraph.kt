package com.mathmaster.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mathmaster.app.MathMasterApplication
import com.mathmaster.app.ui.games.digits.DigitsGameScreen
import com.mathmaster.app.ui.games.digits.DigitsGameViewModel
import com.mathmaster.app.ui.games.division.DivisionGameScreen
import com.mathmaster.app.ui.games.division.DivisionGameViewModel
import com.mathmaster.app.ui.games.mixed.MixedGameScreen
import com.mathmaster.app.ui.games.mixed.MixedGameViewModel
import com.mathmaster.app.ui.games.multiplication.MultiplicationGameScreen
import com.mathmaster.app.ui.games.multiplication.MultiplicationGameViewModel
import com.mathmaster.app.ui.games.speed.SpeedGameScreen
import com.mathmaster.app.ui.games.speed.SpeedGameViewModel
import com.mathmaster.app.ui.games.subtraction.SubtractionGameScreen
import com.mathmaster.app.ui.games.subtraction.SubtractionGameViewModel
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

        // Game screens
        composable(Routes.GAME_ADDITION) {
            val viewModel: DigitsGameViewModel = viewModel()
            DigitsGameScreen(
                viewModel = viewModel,
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.GAME_SUBTRACTION) {
            val viewModel: SubtractionGameViewModel = viewModel()
            SubtractionGameScreen(
                viewModel = viewModel,
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(Routes.GAME_MULTIPLICATION) {
            val viewModel: MultiplicationGameViewModel = viewModel()
            MultiplicationGameScreen(
                viewModel = viewModel,
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(Routes.GAME_DIVISION) {
            val viewModel: DivisionGameViewModel = viewModel()
            DivisionGameScreen(
                viewModel = viewModel,
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(Routes.GAME_MIXED) {
            val viewModel: MixedGameViewModel = viewModel()
            MixedGameScreen(
                viewModel = viewModel,
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(Routes.GAME_SPEED) {
            val viewModel: SpeedGameViewModel = viewModel()
            SpeedGameScreen(
                viewModel = viewModel,
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}
