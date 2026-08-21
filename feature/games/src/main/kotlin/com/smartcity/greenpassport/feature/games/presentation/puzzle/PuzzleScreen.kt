package com.smartcity.greenpassport.feature.games.presentation.puzzle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.feature.games.R

private const val PUZZLE_GRID_COLUMNS = 4

private val cardIcons: List<ImageVector> = listOf(
    Icons.Filled.Eco,
    Icons.Filled.Recycling,
    Icons.Filled.WaterDrop,
    Icons.Filled.Park,
    Icons.Filled.Grass,
    Icons.Filled.WbSunny,
)

@Composable
fun PuzzleScreen(
    modifier: Modifier = Modifier,
    viewModel: PuzzleViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.puzzle_instructions),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(Dimens.SpacingMedium),
        )

        if (uiState.isFinished) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.SpacingMedium),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.puzzle_finished_format, uiState.moves, uiState.score),
                    style = MaterialTheme.typography.titleLarge,
                )
                Button(
                    onClick = viewModel::onRestart,
                    modifier = Modifier.padding(top = Dimens.SpacingMedium),
                ) {
                    Text(stringResource(R.string.game_play_again))
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(PUZZLE_GRID_COLUMNS),
            contentPadding = PaddingValues(Dimens.SpacingMedium),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        ) {
            items(uiState.cards) { card ->
                Card(
                    onClick = { viewModel.onCardClick(card.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (card.isFaceUp || card.isMatched) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                    ),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        if (card.isFaceUp || card.isMatched) {
                            Icon(
                                imageVector = cardIcons[card.iconIndex],
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
        }
    }
}
