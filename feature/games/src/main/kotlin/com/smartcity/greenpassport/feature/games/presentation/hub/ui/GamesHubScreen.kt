package com.smartcity.greenpassport.feature.games.presentation.hub.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.layout.plus
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.feature.games.R
import com.smartcity.greenpassport.feature.games.presentation.hub.viewmodels.GamesHubViewModel
import com.smartcity.greenpassport.core.R as CoreR

private const val COLUMN_COUNT = 2

@Composable
fun GamesHubScreen(
    onGameSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: GamesHubViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val language = LocalLocale.current.platformLocale.language
    val visibleGames = uiState.visibleGames(language)

    when {
        uiState.isLoading -> LoadingContent(modifier = modifier.padding(contentPadding))
        uiState.hasError -> ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = viewModel::retry,
            modifier = modifier.padding(contentPadding),
        )
        uiState.games.isEmpty() -> EmptyContent(
            message = stringResource(CoreR.string.error_generic_message),
            modifier = modifier.padding(contentPadding),
        )
        visibleGames.isEmpty() -> EmptyContent(
            message = stringResource(CoreR.string.nothing_found),
            modifier = modifier.padding(contentPadding),
        )

        else -> LazyVerticalGrid(
            columns = GridCells.Fixed(COLUMN_COUNT),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge),
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding + PaddingValues(
                horizontal = Dimens.ScreenHorizontalPadding,
                vertical = Dimens.SpacingMedium,
            ),
        ) {
            items(visibleGames, key = { it.id }) { game ->
                val bestScore = uiState.bestScores[game.id]
                GameTile(
                    game = game,
                    title = game.title(language),
                    bestScore = bestScore?.let { stringResource(R.string.games_best_score_format, it) },
                    onClick = { onGameSelected(game.id) },
                )
            }
        }
    }
}
