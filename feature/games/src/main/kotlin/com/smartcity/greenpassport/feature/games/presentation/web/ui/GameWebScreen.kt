package com.smartcity.greenpassport.feature.games.presentation.web.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpCloseButton
import com.smartcity.greenpassport.core.designsystem.component.PointsChip
import com.smartcity.greenpassport.core.designsystem.component.ScreenHeader
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.rewards.RewardFailure
import com.smartcity.greenpassport.feature.games.R
import com.smartcity.greenpassport.feature.games.presentation.web.state.GameWebUiState
import com.smartcity.greenpassport.feature.games.presentation.web.state.gameUrl
import com.smartcity.greenpassport.feature.games.presentation.web.viewmodels.GameWebViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import com.smartcity.greenpassport.core.R as CoreR

private const val BANNER_VISIBLE_MILLIS = 2_000L
private const val DARK_LUMINANCE_THRESHOLD = 0.5f

@Composable
fun GameWebScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GameWebViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isPageLoading by remember { mutableStateOf(true) }
    var hasPageError by remember { mutableStateOf(false) }
    var reloadId by remember { mutableIntStateOf(0) }
    val isDark = MaterialTheme.colorScheme.background.luminance() < DARK_LUMINANCE_THRESHOLD
    val language = LocalLocale.current.platformLocale.language

    BackHandler {}

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        val game = uiState.game
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(
                title = game?.title(language).orEmpty(),
                navigationButton = { GpCloseButton(onClick = onClose) },
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
                    ),
            ) {
                when {
                    hasPageError || uiState.isGameMissing && game == null -> ErrorContent(
                        message = stringResource(CoreR.string.error_generic_message),
                        retryLabel = stringResource(CoreR.string.retry_button),
                        onRetry = {
                            hasPageError = false
                            reloadId++
                        },
                    )

                    game != null -> key(reloadId) {
                        GameWebView(
                            url = gameUrl(game, language, isDark),
                            onFinish = viewModel::onFinish,
                            onClose = onClose,
                            onLoadingChange = { isPageLoading = it },
                            onFailure = { hasPageError = true },
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
                if (isPageLoading && !hasPageError) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
        RewardBanner(uiState = uiState, modifier = Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun RewardBanner(
    uiState: GameWebUiState,
    modifier: Modifier = Modifier,
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.rewardCount) {
        if (uiState.rewardCount == 0) return@LaunchedEffect
        isVisible = true
        delay(BANNER_VISIBLE_MILLIS.milliseconds)
        isVisible = false
    }
    if (!isVisible) return
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        modifier = modifier
            .statusBarsPadding()
            .padding(top = Dimens.SpacingSmall)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(Dimens.CornerRadiusPill))
            .padding(horizontal = Dimens.CardPadding, vertical = Dimens.SpacingSmall),
    ) {
        val failure = uiState.rewardFailure
        val reward = uiState.lastReward
        if (failure != null) {
            Text(
                text = stringResource(
                    if (failure == RewardFailure.NETWORK) {
                        R.string.no_internet_for_points_msg
                    } else {
                        R.string.something_went_wrong_msg
                    },
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
            )
        } else if (reward != null) {
            PointsChip(points = reward.points)
            if (reward.streakBonus > 0) {
                Text(
                    text = stringResource(R.string.streak_bonus_msg, reward.streakBonus),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
