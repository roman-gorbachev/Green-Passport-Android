package com.smartcity.greenpassport.feature.games.presentation.sorting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.feature.games.R

private const val SORTING_BIN_COLUMNS = 2

@Composable
fun WasteSortingScreen(
    modifier: Modifier = Modifier,
    viewModel: WasteSortingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.SpacingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (uiState.isFinished) {
            Text(
                text = stringResource(R.string.sorting_finished_format, uiState.score),
                style = MaterialTheme.typography.titleLarge,
            )
            Button(
                onClick = viewModel::onRestart,
                modifier = Modifier.padding(top = Dimens.SpacingMedium),
            ) {
                Text(stringResource(R.string.game_play_again))
            }
            return@Column
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.sorting_score_format, uiState.score),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.sorting_time_format, uiState.secondsRemaining),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Text(
            text = stringResource(R.string.sorting_instructions),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = Dimens.SpacingSmall),
        )

        uiState.currentItem?.let { item ->
            Text(
                text = stringResource(item.nameRes),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = Dimens.SpacingExtraLarge),
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(SORTING_BIN_COLUMNS),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        ) {
            items(WasteCategory.entries) { category ->
                Button(
                    onClick = { viewModel.onCategorySelected(category) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(category.binLabelRes))
                }
            }
        }
    }
}
