package com.smartcity.greenpassport.feature.games.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.GpListRow
import com.smartcity.greenpassport.core.designsystem.component.IconCircle
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import com.smartcity.greenpassport.feature.games.R
import com.smartcity.greenpassport.feature.games.domain.GameId

@Composable
fun GamesHubScreen(
    onGameSelected: (GameId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GamesHubViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = Dimens.ScreenHorizontalPadding,
            vertical = Dimens.SpacingSmall,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
    ) {
        items(GameId.entries) { gameId ->
            GameRow(
                gameId = gameId,
                bestScore = uiState.bestScores[gameId.storageId],
                onClick = { onGameSelected(gameId) },
            )
        }
    }
}

@Composable
private fun GameRow(
    gameId: GameId,
    bestScore: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GpListRow(
        title = stringResource(gameTitleRes(gameId)),
        leading = { IconCircle(icon = gameIcon(gameId), color = gameColor(gameId)) },
        trailing = {
            if (bestScore != null) {
                Text(
                    text = stringResource(R.string.games_best_score_format, bestScore),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                )
            }
        },
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun gameColor(gameId: GameId): Color {
    val sectionColors = GreenPassportTheme.sectionColors
    return when (gameId) {
        GameId.ECO_PUZZLE -> sectionColors.games
        GameId.WASTE_SORTING -> sectionColors.community
        GameId.ECO_MAZE -> sectionColors.calendar
        GameId.ECO_QUIZ -> sectionColors.tips
    }
}
