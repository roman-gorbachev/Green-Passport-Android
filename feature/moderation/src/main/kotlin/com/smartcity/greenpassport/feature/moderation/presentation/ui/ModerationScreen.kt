package com.smartcity.greenpassport.feature.moderation.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.GpPrimaryButton
import com.smartcity.greenpassport.core.designsystem.component.GpSurfaceCard
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.NetworkImage
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.ForumPost
import com.smartcity.greenpassport.core.model.moderation.ModerationAction
import com.smartcity.greenpassport.feature.moderation.R
import com.smartcity.greenpassport.feature.moderation.domain.SubmissionItem
import com.smartcity.greenpassport.feature.moderation.presentation.state.ModerationTab
import com.smartcity.greenpassport.feature.moderation.presentation.viewmodels.ModerationViewModel
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun ModerationScreen(
    modifier: Modifier = Modifier,
    viewModel: ModerationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf(ModerationTab.PHOTOS) }

    when {
        uiState.isLoading -> LoadingContent(modifier = modifier)
        !uiState.isModerator -> EmptyContent(
            message = stringResource(R.string.moderators_only_msg),
            modifier = modifier,
        )
        else -> Column(modifier = modifier.fillMaxSize()) {
            PrimaryTabRow(selectedTabIndex = selectedTab.ordinal) {
                ModerationTab.entries.forEach { tab ->
                    val count = if (tab == ModerationTab.PHOTOS) uiState.submissions.size else uiState.flaggedPosts.size
                    Tab(
                        selected = tab == selectedTab,
                        onClick = { selectedTab = tab },
                        text = { Text(stringResource(tabLabelRes(tab), count)) },
                    )
                }
            }
            if (uiState.hasActionError) {
                Text(
                    text = stringResource(R.string.action_failed_msg),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(Dimens.SpacingMedium),
                )
            }
            when (selectedTab) {
                ModerationTab.PHOTOS -> SubmissionsList(
                    submissions = uiState.submissions,
                    processingIds = uiState.processingIds,
                    onApprove = viewModel::onApprove,
                    onReject = viewModel::onReject,
                )

                ModerationTab.REPORTS -> FlaggedPostsList(
                    posts = uiState.flaggedPosts,
                    processingIds = uiState.processingIds,
                    onAction = viewModel::onModeratePost,
                )
            }
        }
    }
}

@Composable
private fun SubmissionsList(
    submissions: List<SubmissionItem>,
    processingIds: Set<String>,
    onApprove: (String) -> Unit,
    onReject: (String, String) -> Unit,
) {
    if (submissions.isEmpty()) {
        EmptyContent(message = stringResource(R.string.no_photos_to_review))
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(Dimens.ScreenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
    ) {
        items(submissions, key = { it.submission.id }) { item ->
            SubmissionCard(
                item = item,
                isProcessing = item.submission.id in processingIds,
                onApprove = { onApprove(item.submission.id) },
                onReject = { reason -> onReject(item.submission.id, reason) },
            )
        }
    }
}

@Composable
private fun SubmissionCard(
    item: SubmissionItem,
    isProcessing: Boolean,
    onApprove: () -> Unit,
    onReject: (String) -> Unit,
) {
    GpSurfaceCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.CardPadding)) {
            NetworkImage(
                url = item.photoUrl,
                contentDescription = null,
                fallback = painterResource(CoreR.drawable.event_placeholder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.HeroCardHeight)
                    .clip(MaterialTheme.shapes.medium),
            )
            Text(
                text = item.taskTitle ?: item.submission.taskId,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            )
            Text(
                text = item.submission.userName ?: stringResource(R.string.no_name),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            ) {
                GpPrimaryButton(
                    text = stringResource(R.string.approve),
                    onClick = onApprove,
                    isLoading = isProcessing,
                    modifier = Modifier.weight(1f),
                )
                RejectMenu(enabled = !isProcessing, onReject = onReject)
            }
        }
    }
}

@Composable
private fun RejectMenu(
    enabled: Boolean,
    onReject: (String) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val reasons = listOf(
        stringResource(R.string.task_not_visible_in_photo),
        stringResource(R.string.photo_not_taken_by_user),
        stringResource(R.string.inappropriate_photo),
    )
    Box(modifier = Modifier.padding(start = Dimens.SpacingSmall)) {
        TextButton(onClick = { isExpanded = true }, enabled = enabled) {
            Text(text = stringResource(R.string.reject), color = MaterialTheme.colorScheme.error)
        }
        DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            reasons.forEach { reason ->
                DropdownMenuItem(
                    text = { Text(reason) },
                    onClick = {
                        isExpanded = false
                        onReject(reason)
                    },
                )
            }
        }
    }
}

@Composable
private fun FlaggedPostsList(
    posts: List<ForumPost>,
    processingIds: Set<String>,
    onAction: (String, ModerationAction) -> Unit,
) {
    if (posts.isEmpty()) {
        EmptyContent(message = stringResource(R.string.no_reports))
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(Dimens.ScreenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing),
    ) {
        items(posts, key = { it.id }) { post ->
            FlaggedPostCard(
                post = post,
                isProcessing = post.id in processingIds,
                onAction = { action -> onAction(post.id, action) },
            )
        }
    }
}

@Composable
private fun FlaggedPostCard(
    post: ForumPost,
    isProcessing: Boolean,
    onAction: (ModerationAction) -> Unit,
) {
    GpSurfaceCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.CardPadding)) {
            Text(
                text = post.authorName ?: stringResource(R.string.no_name),
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = stringResource(
                    if (post.isHidden) R.string.hidden_reports_count else R.string.visible_reports_count,
                    post.reportCount,
                ),
                style = MaterialTheme.typography.labelMedium,
                color = if (post.isHidden) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
            )
            Text(
                text = post.text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            )
            Row(modifier = Modifier.padding(top = Dimens.SpacingSmall)) {
                val toggleAction = if (post.isHidden) ModerationAction.RESTORE else ModerationAction.HIDE
                TextButton(onClick = { onAction(toggleAction) }, enabled = !isProcessing) {
                    Text(stringResource(if (post.isHidden) R.string.restore else R.string.hide))
                }
                TextButton(onClick = { onAction(ModerationAction.DELETE) }, enabled = !isProcessing) {
                    Text(text = stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

private fun tabLabelRes(tab: ModerationTab): Int = when (tab) {
    ModerationTab.PHOTOS -> R.string.task_photos_count
    ModerationTab.REPORTS -> R.string.reports_count
}
