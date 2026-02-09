package com.mathmaster.app.ui.games.magicsquare

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mathmaster.app.ui.games.Difficulty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagicSquareScreen(
    viewModel: MagicSquareViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Magic Square") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    DifficultyDropdown(
                        currentDifficulty = uiState.difficulty,
                        onDifficultySelected = { viewModel.setDifficulty(it) }
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Magic constant display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "Target sum: ${uiState.gameState.magicConstant}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Grid
            MagicSquareGrid(
                gameState = uiState.gameState,
                onCellClick = { row, col -> viewModel.selectCell(row, col) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Number pad
            NumberPad(
                maxNumber = uiState.gameState.size * uiState.gameState.size,
                onNumberClick = { viewModel.enterNumber(it) },
                onClearClick = { viewModel.clearSelectedCell() }
            )

            // New puzzle button
            Button(
                onClick = { viewModel.newPuzzle() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("New Puzzle")
            }
        }

        // Win overlay
        if (uiState.showWinOverlay) {
            WinOverlay(
                onDismiss = { viewModel.dismissWinOverlay() },
                onNewPuzzle = { viewModel.newPuzzle() }
            )
        }
    }
}

@Composable
fun MagicSquareGrid(
    gameState: MagicSquareState,
    onCellClick: (Int, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        gameState.grid.forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEachIndexed { colIndex, cell ->
                    GridCell(
                        cell = cell,
                        isSelected = gameState.selectedCell == Pair(rowIndex, colIndex),
                        onClick = { onCellClick(rowIndex, colIndex) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Row and column sum indicators
    SumIndicators(gameState)
}

@Composable
fun GridCell(
    cell: CellState,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        cell is CellState.Fixed -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = cell.isEditable()) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        val value = cell.getValue()
        if (value != null) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = if (cell is CellState.Fixed) FontWeight.Bold else FontWeight.Normal,
                color = if (cell is CellState.Fixed) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}

@Composable
fun SumIndicators(gameState: MagicSquareState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Row sums
        Text(
            text = "Row sums:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0 until gameState.size) {
                val sum = MagicSquareGame.getRowSum(gameState, i)
                SumChip(
                    label = "R${i + 1}",
                    sum = sum,
                    target = gameState.magicConstant
                )
            }
        }

        // Column sums
        Text(
            text = "Column sums:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0 until gameState.size) {
                val sum = MagicSquareGame.getColSum(gameState, i)
                SumChip(
                    label = "C${i + 1}",
                    sum = sum,
                    target = gameState.magicConstant
                )
            }
        }
    }
}

@Composable
fun SumChip(
    label: String,
    sum: Int?,
    target: Int
) {
    val color = when {
        sum == null -> MaterialTheme.colorScheme.surfaceVariant
        sum == target -> Color(0xFF4CAF50) // Green
        sum > target -> Color(0xFFE57373) // Red
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        color = color,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (sum == target) Color.White else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = sum?.toString() ?: "?",
                fontSize = 12.sp,
                color = if (sum == target) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun NumberPad(
    maxNumber: Int,
    onNumberClick: (Int) -> Unit,
    onClearClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Create rows of number buttons
        val numbersPerRow = 5
        val rows = (maxNumber + numbersPerRow - 1) / numbersPerRow

        for (rowIndex in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 0 until numbersPerRow) {
                    val number = rowIndex * numbersPerRow + i + 1
                    if (number <= maxNumber) {
                        Button(
                            onClick = { onNumberClick(number) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(number.toString())
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Clear button
        OutlinedButton(
            onClick = onClearClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Clear")
        }
    }
}

@Composable
fun DifficultyDropdown(
    currentDifficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(currentDifficulty.name)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Difficulty.entries.forEach { difficulty ->
                DropdownMenuItem(
                    text = { Text(difficulty.name) },
                    onClick = {
                        onDifficultySelected(difficulty)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun WinOverlay(
    onDismiss: () -> Unit,
    onNewPuzzle: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Congratulations!") },
        text = { Text("You solved the magic square!") },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onNewPuzzle()
            }) {
                Text("New Puzzle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
