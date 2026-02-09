package com.mathmaster.app.ui.games.division

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mathmaster.app.ui.games.BaseGameScaffold

@Composable
fun divisionGameScreen(
    viewModel: divisionGameViewModel,
    onBackPressed: () -> Unit
) {
    val difficulty by viewModel.difficulty.collectAsState()

    BaseGameScaffold(
        title = "division",
        selectedDifficulty = difficulty,
        onDifficultyChanged = viewModel::setDifficulty,
        onBackPressed = onBackPressed
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "division Game - ${difficulty.displayName}\n(Coming Soon)",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
