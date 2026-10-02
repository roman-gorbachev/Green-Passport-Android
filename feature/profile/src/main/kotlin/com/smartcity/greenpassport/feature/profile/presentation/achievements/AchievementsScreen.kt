package com.smartcity.greenpassport.feature.profile.presentation.achievements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpSurfaceCard
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.SymbolTile
import com.smartcity.greenpassport.core.designsystem.component.SymbolTileStyle
import com.smartcity.greenpassport.core.designsystem.layout.plus
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.Achievement
import com.smartcity.greenpassport.feature.profile.R
import com.smartcity.greenpassport.core.R as CoreR

private const val COLUMN_COUNT = 2

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

    val achievements = uiState.achievements
    LazyVerticalGrid(
        columns = GridCells.Fixed(COLUMN_COUNT),
        horizontalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
        verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
        contentPadding = contentPadding + PaddingValues(
            horizontal = Dimens.ScreenHorizontalPadding,
            vertical = Dimens.SpacingMedium,
        ),
        modifier = modifier.fillMaxSize(),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = stringResource(
                    R.string.achievements_unlocked_format,
                    achievements.count { it.isUnlocked },
                    achievements.size,
                ),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        items(achievements, key = { it.id }) { achievement -> AchievementCard(achievement) }
    }
}

@Composable
private fun AchievementCard(achievement: Achievement) {
    GpSurfaceCard(
        color = if (achievement.isUnlocked) {
            MaterialTheme.colorScheme.surfaceContainerHigh
        } else {
            MaterialTheme.colorScheme.surface
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.AchievementCardMinHeight)
            .semantics(mergeDescendants = true) {},
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
            modifier = Modifier
                .fillMaxSize()
                .heightIn(min = Dimens.AchievementCardMinHeight)
                .padding(Dimens.CardPadding),
        ) {
            SymbolTile(
                icon = achievementIcon(achievement.id),
                style = if (achievement.isUnlocked) SymbolTileStyle.Prominent else SymbolTileStyle.Muted,
                size = Dimens.TileSizeMedium,
            )
            Text(
                text = stringResource(achievementTitleRes(achievement.id)),
                style = MaterialTheme.typography.titleMedium,
                color = if (achievement.isUnlocked) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
            Text(
                text = stringResource(achievementDescriptionRes(achievement.id)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            if (achievement.isUnlocked) {
                UnlockedLabel()
            } else {
                AchievementProgress(achievement)
            }
        }
    }
}

@Composable
private fun UnlockedLabel() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.IconSizeExtraSmall),
        )
        Text(
            text = stringResource(R.string.achievement_unlocked_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun AchievementProgress(achievement: Achievement) {
    LinearProgressIndicator(
        progress = { achievement.clampedProgress.toFloat() / achievement.target },
        modifier = Modifier.fillMaxWidth(),
    )
    Text(
        text = stringResource(R.string.achievement_progress_format, achievement.clampedProgress, achievement.target),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
