package com.smartcity.greenpassport.feature.profile.presentation.achievements

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.ListRowContent
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.SymbolTile
import com.smartcity.greenpassport.core.designsystem.component.SymbolTileStyle
import com.smartcity.greenpassport.core.designsystem.component.listSectionItems
import com.smartcity.greenpassport.core.designsystem.layout.plus
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.Achievement
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun AchievementsScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: AchievementsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.hasError) {
        ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = viewModel::retry,
            modifier = modifier.padding(contentPadding),
        )
        return
    }

    if (uiState.isLoading) {
        LoadingContent(modifier = modifier.padding(contentPadding))
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding + PaddingValues(
            horizontal = Dimens.ScreenHorizontalPadding,
            vertical = Dimens.SpacingMedium,
        ),
    ) {
        listSectionItems(uiState.achievements) { achievement -> AchievementRow(achievement) }
    }
}

@Composable
private fun AchievementRow(achievement: Achievement) {
    ListRowContent(
        title = stringResource(achievementTitleRes(achievement.id)),
        subtitle = stringResource(achievementDescriptionRes(achievement.id)),
        leading = {
            SymbolTile(
                icon = if (achievement.isUnlocked) Icons.Filled.EmojiEvents else Icons.Filled.Lock,
                style = if (achievement.isUnlocked) SymbolTileStyle.Accent else SymbolTileStyle.Muted,
                size = Dimens.TileSizeMedium,
            )
        },
        trailing = {},
        modifier = Modifier.alpha(if (achievement.isUnlocked) 1f else LOCKED_ALPHA),
    )
}

private const val LOCKED_ALPHA = 0.6f
