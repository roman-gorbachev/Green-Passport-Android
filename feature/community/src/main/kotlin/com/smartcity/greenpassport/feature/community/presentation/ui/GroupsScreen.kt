package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
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
import com.smartcity.greenpassport.core.designsystem.component.GpTextField
import com.smartcity.greenpassport.core.designsystem.component.ListRowContent
import com.smartcity.greenpassport.core.designsystem.component.ListSection
import com.smartcity.greenpassport.core.designsystem.component.ListSectionDivider
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.SymbolTile
import com.smartcity.greenpassport.core.designsystem.layout.plus
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.feature.community.R
import com.smartcity.greenpassport.feature.community.presentation.viewmodels.GroupsViewModel

@Composable
fun GroupsScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: GroupsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        contentPadding = contentPadding + PaddingValues(
            horizontal = Dimens.ScreenHorizontalPadding,
            vertical = Dimens.SpacingMedium,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge),
    ) {
        item {
            CreateGroupSection(
                draftName = uiState.draftName,
                isCreating = uiState.isCreating,
                isNameRejected = uiState.isNameRejected,
                onDraftNameChange = viewModel::onDraftNameChanged,
                onCreate = viewModel::onCreateGroup,
            )
        }
        when {
            uiState.isLoading -> item { LoadingContent() }
            uiState.groups.isEmpty() -> item { EmptyContent(message = stringResource(R.string.groups_empty)) }
            else -> item {
                ListSection {
                    uiState.groups.forEachIndexed { index, group ->
                        GroupRow(
                            group = group,
                            isMember = uiState.currentUserId != null && group.memberIds.contains(uiState.currentUserId),
                            isJoining = uiState.joiningGroupId == group.id,
                            onJoin = { viewModel.onJoinGroup(group) },
                        )
                        if (index < uiState.groups.lastIndex) {
                            ListSectionDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateGroupSection(
    draftName: String,
    isCreating: Boolean,
    isNameRejected: Boolean,
    onDraftNameChange: (String) -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListSection(
        modifier = modifier,
        footer = if (isNameRejected) {
            {
                Text(
                    text = stringResource(R.string.text_contains_banned_words),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        } else {
            null
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = Dimens.CardPadding),
        ) {
            GpTextField(
                value = draftName,
                onValueChange = onDraftNameChange,
                label = { Text(stringResource(R.string.groups_draft_label)) },
                isError = isNameRejected,
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            if (isCreating) {
                CircularProgressIndicator(modifier = Modifier.size(Dimens.IconSizeMedium))
            } else {
                Button(
                    onClick = onCreate,
                    enabled = draftName.isNotBlank(),
                    shape = RoundedCornerShape(Dimens.CornerRadiusPill),
                ) {
                    Text(
                        text = stringResource(R.string.groups_create_button),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupRow(
    group: CommunityGroup,
    isMember: Boolean,
    isJoining: Boolean,
    onJoin: () -> Unit,
) {
    ListRowContent(
        title = group.name,
        subtitle = stringResource(R.string.groups_member_count_format, group.memberIds.size),
        leading = { SymbolTile(icon = Icons.Filled.Groups) },
        trailing = {
            when {
                isMember -> Text(
                    text = stringResource(R.string.groups_joined_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                isJoining -> CircularProgressIndicator(modifier = Modifier.size(Dimens.IconSizeMedium))

                else -> FilledTonalButton(onClick = onJoin, shape = RoundedCornerShape(Dimens.CornerRadiusPill)) {
                    Text(
                        text = stringResource(R.string.groups_join_button),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        },
    )
}
