package com.mathmaster.app.ui.games

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Base scaffold for all game screens.
 * Provides consistent top bar with back button and difficulty selector.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseGameScaffold(
    title: String,
    selectedDifficulty: Difficulty,
    onDifficultyChanged: (Difficulty) -> Unit,
    onBackPressed: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to menu"
                        )
                    }
                },
                actions = {
                    DifficultySelector(
                        selectedDifficulty = selectedDifficulty,
                        onDifficultyChanged = onDifficultyChanged
                    )
                }
            )
        }
    ) { padding ->
        content(padding)
    }
}

@Composable
private fun DifficultySelector(
    selectedDifficulty: Difficulty,
    onDifficultyChanged: (Difficulty) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(end = 8.dp)) {
        TextButton(onClick = { expanded = true }) {
            Text(selectedDifficulty.displayName)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Difficulty.entries.forEach { difficulty ->
                DropdownMenuItem(
                    text = { Text(difficulty.displayName) },
                    onClick = {
                        onDifficultyChanged(difficulty)
                        expanded = false
                    },
                    leadingIcon = if (difficulty == selectedDifficulty) {
                        { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) }
                    } else null
                )
            }
        }
    }
}
