package com.smartcity.greenpassport.feature.games.presentation.maze

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.feature.games.R

private const val CELL_SIZE_DP = 48

@Composable
fun MazeScreen(
    modifier: Modifier = Modifier,
    viewModel: MazeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.SpacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.maze_score_format, uiState.score),
            style = MaterialTheme.typography.titleMedium,
        )

        if (uiState.isFinished) {
            Text(
                text = stringResource(R.string.maze_finished_format, uiState.score),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = Dimens.SpacingMedium),
            )
            Button(
                onClick = viewModel::onRestart,
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            ) {
                Text(stringResource(R.string.game_play_again))
            }
        }

        Column(modifier = Modifier.padding(top = Dimens.SpacingMedium)) {
            uiState.grid.forEachIndexed { rowIndex, rowCells ->
                Row {
                    rowCells.forEachIndexed { colIndex, cell ->
                        val position = MazePosition(rowIndex, colIndex)
                        MazeCell(
                            cell = cell,
                            isPlayerHere = uiState.playerPosition == position,
                            isCollected = position in uiState.collectedItems,
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(top = Dimens.SpacingExtraLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconButton(onClick = { viewModel.onMove(MazeDirection.UP) }) {
                Icon(Icons.Filled.ArrowUpward, contentDescription = null)
            }
            Row {
                IconButton(onClick = { viewModel.onMove(MazeDirection.LEFT) }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
                IconButton(onClick = { viewModel.onMove(MazeDirection.DOWN) }) {
                    Icon(Icons.Filled.ArrowDownward, contentDescription = null)
                }
                IconButton(onClick = { viewModel.onMove(MazeDirection.RIGHT) }) {
                    Icon(Icons.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun MazeCell(
    cell: MazeCellType,
    isPlayerHere: Boolean,
    isCollected: Boolean,
) {
    val backgroundColor = when (cell) {
        MazeCellType.WALL -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    Column(
        modifier = Modifier
            .size(CELL_SIZE_DP.dp)
            .aspectRatio(1f)
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        when {
            isPlayerHere -> Icon(
                imageVector = Icons.Filled.Eco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            cell == MazeCellType.ITEM && !isCollected -> Icon(
                imageVector = Icons.Filled.Eco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}
