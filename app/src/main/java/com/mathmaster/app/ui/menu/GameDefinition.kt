package com.mathmaster.app.ui.menu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

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
        route = "game_addition"
    ),
    GameDefinition(
        id = GameType.SUBTRACTION,
        title = "Subtraction",
        description = "Practice subtracting numbers",
        icon = Icons.Default.Remove,
        route = "game_subtraction"
    ),
    GameDefinition(
        id = GameType.MULTIPLICATION,
        title = "Multiplication",
        description = "Practice multiplying numbers",
        icon = Icons.Default.Close,
        route = "game_multiplication"
    ),
    GameDefinition(
        id = GameType.DIVISION,
        title = "Division",
        description = "Practice dividing numbers",
        icon = Icons.Default.CallSplit,
        route = "game_division"
    ),
    GameDefinition(
        id = GameType.MIXED,
        title = "Mixed Operations",
        description = "Practice all operations",
        icon = Icons.Default.Apps,
        route = "game_mixed"
    ),
    GameDefinition(
        id = GameType.SPEED,
        title = "Speed Round",
        description = "Fast-paced math challenges",
        icon = Icons.Default.Speed,
        route = "game_speed"
    )
)
