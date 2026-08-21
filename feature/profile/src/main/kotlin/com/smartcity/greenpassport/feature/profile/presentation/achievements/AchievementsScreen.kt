package com.smartcity.greenpassport.feature.profile.presentation.achievements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.Achievement
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun AchievementsScreen(
    modifier: Modifier = Modifier,
    viewModel: AchievementsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.hasError) {
        ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = viewModel::retry,
            modifier = modifier,
        )
        return
    }

    if (uiState.isLoading) {
        LoadingContent(modifier = modifier)
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimens.SpacingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
    ) {
        items(uiState.achievements) { achievement ->
            AchievementRow(
                achievement
            )
        }
    }
}

@Composable
private fun AchievementRow(achievement: Achievement) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.SpacingMedium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (achievement.isUnlocked) Icons.Filled.EmojiEvents else Icons.Filled.Lock,
                    contentDescription = null,
                    tint = if (achievement.isUnlocked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    },
                )
                Text(
                    text = stringResource(
                        achievementTitleRes(
                            achievement.id
                        )
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = Dimens.SpacingSmall),
                )
            }
            Text(
                text = stringResource(
                    achievementDescriptionRes(
                        achievement.id
                    )
                ),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = Dimens.SpacingExtraSmall),
            )
        }
    }
}
