package com.mathmaster.app.ui.menu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.mathmaster.app.navigation.Routes

enum class GameType {
    ADDITION,
    SUBTRACTION,
    MULTIPLICATION,
    DIVISION,
    MIXED,
    SPEED
}

data class GameDefinition(
    val id: GameType,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String
)

val allGames = listOf(
    GameDefinition(
        id = GameType.ADDITION,
        title = "Addition",
        description = "Practice adding numbers",
        icon = Icons.Default.Add,
        route = Routes.GAME_ADDITION
    ),
    GameDefinition(
        id = GameType.SUBTRACTION,
        title = "Subtraction",
        description = "Practice subtracting numbers",
        icon = Icons.Default.Remove,
        route = Routes.GAME_SUBTRACTION
    ),
    GameDefinition(
        id = GameType.MULTIPLICATION,
        title = "Multiplication",
        description = "Practice multiplying numbers",
        icon = Icons.Default.Close,
        route = Routes.GAME_MULTIPLICATION
    ),
    GameDefinition(
        id = GameType.DIVISION,
        title = "Division",
        description = "Practice dividing numbers",
        icon = Icons.Default.PieChart,
        route = Routes.GAME_DIVISION
    ),
    GameDefinition(
        id = GameType.MIXED,
        title = "Mixed Operations",
        description = "Practice all operations",
        icon = Icons.Default.GridOn,
        route = Routes.GAME_MIXED
    ),
    GameDefinition(
        id = GameType.SPEED,
        title = "Speed Round",
        description = "Fast-paced math challenges",
        icon = Icons.Default.Timer,
        route = Routes.GAME_SPEED
    )
)
