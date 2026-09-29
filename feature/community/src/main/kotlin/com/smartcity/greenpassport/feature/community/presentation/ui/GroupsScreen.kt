package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.GpSurfaceCard
import com.smartcity.greenpassport.core.designsystem.component.GpTextField
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.feature.community.R
import com.smartcity.greenpassport.feature.community.presentation.viewmodels.GroupsViewModel

@Composable
fun GroupsScreen(
    modifier: Modifier = Modifier,
    viewModel: GroupsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SpacingMedium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GpTextField(
                value = uiState.draftName,
                onValueChange = viewModel::onDraftNameChanged,
                label = { Text(stringResource(R.string.groups_draft_label)) },
                isError = uiState.isNameRejected,
                supportingText = if (uiState.isNameRejected) {
                    { Text(stringResource(R.string.text_contains_banned_words)) }
                } else {
                    null
                },
                modifier = Modifier.weight(1f),
            )
            if (uiState.isCreating) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(start = Dimens.SpacingSmall)
                        .size(Dimens.IconSizeMedium),
                )
            } else {
                Button(
                    onClick = viewModel::onCreateGroup,
                    modifier = Modifier.padding(start = Dimens.SpacingSmall),
                ) {
                    Text(stringResource(R.string.groups_create_button))
                }
            }
        }

        when {
            uiState.isLoading -> LoadingContent(modifier = Modifier.weight(1f))
            uiState.groups.isEmpty() -> EmptyContent(
                message = stringResource(R.string.groups_empty),
                modifier = Modifier.weight(1f),
            )

            else -> LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = Dimens.ScreenHorizontalPadding,
                    vertical = Dimens.SpacingSmall,
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
            ) {
                items(uiState.groups) { group ->
                    GroupCard(
                        group = group,
                        isMember = uiState.currentUserId != null && group.memberIds.contains(uiState.currentUserId),
                        isJoining = uiState.joiningGroupId == group.id,
                        onJoin = { viewModel.onJoinGroup(group) },
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupCard(
    group: CommunityGroup,
    isMember: Boolean,
    isJoining: Boolean,
    onJoin: () -> Unit,
) {
    GpSurfaceCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SpacingMedium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = group.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(R.string.groups_member_count_format, group.memberIds.size),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            when {
                isMember -> Text(
                    text = stringResource(R.string.groups_joined_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                isJoining -> CircularProgressIndicator(modifier = Modifier.size(Dimens.IconSizeMedium))

                else -> Button(onClick = onJoin) {
                    Text(stringResource(R.string.groups_join_button))
                }
            }
        }
    }
}
