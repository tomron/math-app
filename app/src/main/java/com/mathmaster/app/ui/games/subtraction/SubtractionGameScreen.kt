package com.mathmaster.app.ui.games.subtraction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mathmaster.app.ui.games.BaseGameScaffold

@Composable
fun SubtractionGameScreen(
    viewModel: SubtractionGameViewModel,
    onBackPressed: () -> Unit
) {
    val difficulty by viewModel.difficulty.collectAsState()

    BaseGameScaffold(
        title = "Subtraction",
        selectedDifficulty = difficulty,
        onDifficultyChanged = viewModel::setDifficulty,
        onBackPressed = onBackPressed
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Subtraction Game - ${difficulty.displayName}\n(Coming Soon)",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
