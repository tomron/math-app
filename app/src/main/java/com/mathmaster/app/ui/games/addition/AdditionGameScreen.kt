package com.mathmaster.app.ui.games.addition

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
fun AdditionGameScreen(
    viewModel: AdditionGameViewModel,
    onBackPressed: () -> Unit
) {
    val difficulty by viewModel.difficulty.collectAsState()

    BaseGameScaffold(
        title = "Addition",
        selectedDifficulty = difficulty,
        onDifficultyChanged = viewModel::setDifficulty,
        onBackPressed = onBackPressed
    ) { padding ->
        AdditionGameContent(padding, difficulty.displayName)
    }
}

@Composable
private fun AdditionGameContent(
    padding: PaddingValues,
    difficultyName: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Addition Game - $difficultyName\n(Coming Soon)",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
