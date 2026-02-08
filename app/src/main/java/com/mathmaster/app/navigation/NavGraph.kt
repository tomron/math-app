package com.mathmaster.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MathMasterNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.PROFILE_SELECTION
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.PROFILE_SELECTION) {
            // Profile selection screen will be added in Phase 2
        }

        composable(Routes.GAME_MENU) {
            // Game menu screen will be added in Phase 3
        }
    }
}
