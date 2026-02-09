package com.mathmaster.app.ui.games.addition

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionGameScreen(
    viewModel: AdditionGameViewModel = viewModel(),
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    AdditionGameContent(
        uiState = uiState,
        onBackPressed = onBackPressed,
        onNumberClick = viewModel::selectNumber,
        onOperationClick = viewModel::selectOperation,
        onUndo = viewModel::undo,
        onRestart = viewModel::restart,
        onNewPuzzle = viewModel::newPuzzle,
        onSkip = viewModel::skipPuzzle,
        onShowExplanation = viewModel::showExplanation,
        onHideExplanation = viewModel::hideExplanation,
        onDismissWinOverlay = viewModel::dismissWinOverlay,
        onDismissTimeoutOverlay = viewModel::dismissTimeoutOverlay,
        onDismissChallengeResults = viewModel::dismissChallengeResults,
        onDifficultyChange = viewModel::setDifficulty,
        onGameModeChange = viewModel::setGameMode
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionGameContent(
    uiState: AdditionGameUiState,
    onBackPressed: () -> Unit,
    onNumberClick: (Int) -> Unit,
    onOperationClick: (Operation) -> Unit,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    onNewPuzzle: () -> Unit,
    onSkip: () -> Unit,
    onShowExplanation: () -> Unit,
    onHideExplanation: () -> Unit,
    onDismissWinOverlay: () -> Unit,
    onDismissTimeoutOverlay: () -> Unit,
    onDismissChallengeResults: () -> Unit,
    onDifficultyChange: (Difficulty) -> Unit,
    onGameModeChange: (GameMode) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Digits") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode selector
            GameModeSelector(
                currentMode = uiState.gameMode,
                onModeChange = onGameModeChange
            )

            // Difficulty selector
            DifficultySelector(
                currentDifficulty = uiState.difficulty,
                onDifficultyChange = onDifficultyChange
            )

            // Timer (for Timer and Challenge modes)
            if (uiState.timeRemaining != null) {
                TimerDisplay(timeRemaining = uiState.timeRemaining)
            }

            // Target display
            TargetDisplay(target = uiState.gameState.target)

            // Message/preview
            if (uiState.gameState.message.isNotEmpty()) {
                Text(
                    text = uiState.gameState.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else if (uiState.gameState.selectedIndices.size == 2 && uiState.gameState.selectedOperation != null) {
                Text(
                    text = uiState.gameState.getPreviewMessage(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            // Number tiles
            NumberTiles(
                numbers = uiState.gameState.numbers,
                selectedIndices = uiState.gameState.selectedIndices,
                onNumberClick = onNumberClick
            )

            // Operation buttons
            OperationButtons(
                allowedOperations = uiState.difficulty.allowedOperations,
                selectedOperation = uiState.gameState.selectedOperation,
                onOperationClick = onOperationClick
            )

            // Move counter
            Text(
                text = "Moves: ${uiState.gameState.moveCount}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Action buttons
            ActionButtons(
                canUndo = uiState.gameState.history.isNotEmpty(),
                gameMode = uiState.gameMode,
                onUndo = onUndo,
                onRestart = onRestart,
                onSkip = onSkip,
                onExplain = onShowExplanation,
                onNewPuzzle = onNewPuzzle
            )
        }
    }

    // Overlays
    if (uiState.showWinOverlay) {
        WinOverlay(
            moveCount = uiState.gameState.moveCount,
            gameMode = uiState.gameMode,
            onDismiss = onDismissWinOverlay,
            onNewPuzzle = onNewPuzzle
        )
    }

    if (uiState.showTimeoutOverlay) {
        TimeoutOverlay(
            onDismiss = onDismissTimeoutOverlay,
            onNewPuzzle = onNewPuzzle
        )
    }

    if (uiState.showChallengeResults) {
        ChallengeResultsOverlay(
            stats = uiState.challengeStats,
            onDismiss = onDismissChallengeResults
        )
    }

    if (uiState.showExplanation) {
        ExplanationOverlay(
            solution = uiState.gameState.solution,
            onDismiss = onHideExplanation
        )
    }
}

@Composable
fun GameModeSelector(
    currentMode: GameMode,
    onModeChange: (GameMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GameMode.entries.forEach { mode ->
            val isSelected = mode == currentMode
            FilterChip(
                selected = isSelected,
                onClick = { onModeChange(mode) },
                label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DifficultySelector(
    currentDifficulty: Difficulty,
    onDifficultyChange: (Difficulty) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Difficulty.entries.forEach { difficulty ->
            val isSelected = difficulty == currentDifficulty
            FilterChip(
                selected = isSelected,
                onClick = { onDifficultyChange(difficulty) },
                label = { Text(difficulty.name.lowercase().replaceFirstChar { it.uppercase() }) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TimerDisplay(timeRemaining: Int) {
    val color = when {
        timeRemaining <= 10 -> MaterialTheme.colorScheme.error
        timeRemaining <= 30 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Text(
        text = "Time: ${timeRemaining}s",
        style = MaterialTheme.typography.titleMedium,
        color = color,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun TargetDisplay(target: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Target",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = target.toString(),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun NumberTiles(
    numbers: List<Int>,
    selectedIndices: List<Int>,
    onNumberClick: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        numbers.chunked(3).forEach { rowNumbers ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                rowNumbers.forEachIndexed { _, number ->
                    val index = numbers.indexOf(number)
                    val isSelected = index in selectedIndices
                    val selectionOrder = selectedIndices.indexOf(index).takeIf { it >= 0 }

                    NumberTile(
                        number = number,
                        isSelected = isSelected,
                        selectionOrder = selectionOrder,
                        onClick = { onNumberClick(index) }
                    )
                }
            }
        }
    }
}

@Composable
fun NumberTile(
    number: Int,
    isSelected: Boolean,
    selectionOrder: Int?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (selectionOrder != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (selectionOrder + 1).toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OperationButtons(
    allowedOperations: Set<Operation>,
    selectedOperation: Operation?,
    onOperationClick: (Operation) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        allowedOperations.forEach { operation ->
            val isSelected = operation == selectedOperation
            Button(
                onClick = { onOperationClick(operation) },
                modifier = Modifier.size(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = operation.symbol,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ActionButtons(
    canUndo: Boolean,
    gameMode: GameMode,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    onSkip: () -> Unit,
    onExplain: () -> Unit,
    onNewPuzzle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onUndo,
            enabled = canUndo,
            modifier = Modifier.weight(1f)
        ) {
            Text("Undo")
        }

        OutlinedButton(
            onClick = if (gameMode == GameMode.CHALLENGE) onSkip else onRestart,
            modifier = Modifier.weight(1f)
        ) {
            Text(if (gameMode == GameMode.CHALLENGE) "Skip" else "Restart")
        }

        OutlinedButton(
            onClick = onExplain,
            modifier = Modifier.weight(1f)
        ) {
            Text("Explain")
        }

        if (gameMode != GameMode.CHALLENGE) {
            OutlinedButton(
                onClick = onNewPuzzle,
                modifier = Modifier.weight(1f)
            ) {
                Text("New")
            }
        }
    }
}

@Composable
fun WinOverlay(
    moveCount: Int,
    gameMode: GameMode,
    onDismiss: () -> Unit,
    onNewPuzzle: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🎉 Solved!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "You solved it in $moveCount moves!",
                    style = MaterialTheme.typography.bodyLarge
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (gameMode == GameMode.CHALLENGE) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Continue")
                        }
                    } else {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Close")
                        }

                        Button(
                            onClick = {
                                onDismiss()
                                onNewPuzzle()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("New Puzzle")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimeoutOverlay(
    onDismiss: () -> Unit,
    onNewPuzzle: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "⏰ Time's Up!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Don't worry, try another puzzle!",
                    style = MaterialTheme.typography.bodyLarge
                )

                Button(
                    onClick = {
                        onDismiss()
                        onNewPuzzle()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("New Puzzle")
                }
            }
        }
    }
}

@Composable
fun ChallengeResultsOverlay(
    stats: ChallengeStats,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Challenge Complete!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Puzzles Solved: ${stats.puzzlesSolved}")
                    Text("Total Time: ${stats.totalTime}s")
                    Text("Best Streak: ${stats.currentStreak}")
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun ExplanationOverlay(
    solution: List<SolutionStep>?,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Solution",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                if (solution.isNullOrEmpty()) {
                    Text(
                        text = "No solution found or puzzle already solved!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(solution) { step ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = step.description,
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close")
                }
            }
        }
    }
}
