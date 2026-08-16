package com.smartcity.greenpassport.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.MascotWidget
import com.smartcity.greenpassport.core.designsystem.component.PillListItem
import com.smartcity.greenpassport.core.designsystem.component.PointsBadge
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.feature.profile.R

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        LoadingContent(modifier = modifier)
        return
    }

    ProfileContent(
        uiState = uiState,
        onNotificationsToggle = viewModel::onNotificationsToggle,
        onSignOut = viewModel::onSignOut,
        modifier = modifier,
    )
}

private data class ProfileMenuEntry(
    val labelRes: Int,
    val icon: ImageVector,
)

private val profileMenuEntries = listOf(
    ProfileMenuEntry(R.string.profile_achievements, Icons.Filled.EmojiEvents),
    ProfileMenuEntry(R.string.profile_cards, Icons.Filled.Style),
    ProfileMenuEntry(R.string.profile_history, Icons.Filled.History),
    ProfileMenuEntry(R.string.profile_favorites, Icons.Filled.Favorite),
    ProfileMenuEntry(R.string.profile_bookmarks, Icons.Filled.Bookmark),
    ProfileMenuEntry(R.string.profile_exchange, Icons.Filled.SwapHoriz),
)

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onNotificationsToggle: (Boolean) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimens.SpacingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium),
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MascotWidget(size = Dimens.MascotSizeMedium)

                Text(
                    text = uiState.userId?.takeIf { !uiState.isAnonymous }
                        ?: stringResource(R.string.profile_anonymous_label),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = Dimens.SpacingSmall),
                )

                uiState.level?.let { level ->
                    Text(
                        text = stringResource(R.string.profile_level_format, level.number),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = Dimens.SpacingMedium),
                    )
                    LinearProgressIndicator(
                        progress = { level.currentXp.toFloat() / level.xpForNextLevel.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimens.SpacingSmall),
                    )
                    Text(
                        text = stringResource(R.string.profile_xp_format, level.currentXp, level.xpForNextLevel),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = Dimens.SpacingExtraSmall),
                    )
                }

                PointsBadge(
                    points = uiState.points,
                    modifier = Modifier.padding(top = Dimens.SpacingMedium),
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.profile_notifications_label),
                    style = MaterialTheme.typography.titleMedium,
                )
                Switch(
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = onNotificationsToggle,
                )
            }
            HorizontalDivider(modifier = Modifier.padding(top = Dimens.SpacingSmall))
        }

        items(profileMenuEntries) { entry ->
            PillListItem(
                title = stringResource(entry.labelRes),
                leadingIcon = entry.icon,
                onClick = {},
            )
        }

        item {
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.padding(end = Dimens.SpacingSmall),
                )
                Text(stringResource(R.string.profile_sign_out))
            }
        }
    }
}
