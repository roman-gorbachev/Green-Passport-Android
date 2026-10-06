package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.MessageComposer
import com.smartcity.greenpassport.core.designsystem.component.ProfileAvatar
import com.smartcity.greenpassport.core.designsystem.component.listSectionItems
import com.smartcity.greenpassport.core.designsystem.layout.plus
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.ForumPost
import com.smartcity.greenpassport.core.model.moderation.ReportReason
import com.smartcity.greenpassport.core.model.profile.AvatarStyle
import com.smartcity.greenpassport.feature.community.R
import com.smartcity.greenpassport.feature.community.presentation.state.ForumUiState
import com.smartcity.greenpassport.feature.community.presentation.state.MessageAction
import com.smartcity.greenpassport.feature.community.presentation.state.MessageTarget
import com.smartcity.greenpassport.feature.community.presentation.viewmodels.ForumViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun ForumScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: ForumViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listPadding = PaddingValues(top = contentPadding.calculateTopPadding())
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val copyToClipboard = rememberCopyToClipboard()
    var forwardedMessage by remember { mutableStateOf<MessageTarget?>(null) }
    val onMessageAction = { action: MessageAction, target: MessageTarget ->
        when (action) {
            MessageAction.Copy -> copyToClipboard(target.text)
            MessageAction.Forward -> forwardedMessage = target
            else -> viewModel.onMessageAction(action, target)
        }
    }
    val onQuoteClick = { messageId: String ->
        val index = uiState.posts.indexOfFirst { it.id == messageId }
        if (index >= 0) scope.launch { listState.animateScrollToItem(index) }
    }

    uiState.pendingDeletion?.let {
        DeleteMessageDialog(onConfirm = viewModel::onDeletionConfirmed, onDismiss = viewModel::onDeletionDismissed)
    }
    forwardedMessage?.let { message ->
        ForwardSheet(message = message, onDismiss = { forwardedMessage = null })
    }

    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            when {
                uiState.isLoading -> LoadingContent(modifier = Modifier.padding(listPadding))
                uiState.posts.isEmpty() -> EmptyContent(
                    message = stringResource(R.string.forum_empty),
                    modifier = Modifier.padding(listPadding),
                )

                else -> ForumPostList(
                    uiState = uiState,
                    listState = listState,
                    contentPadding = listPadding,
                    onMessageAction = onMessageAction,
                    onReport = viewModel::onReport,
                    onQuoteClick = onQuoteClick,
                )
            }
        }
        MessageComposer(
            draft = uiState.draft,
            onDraftChange = viewModel::onDraftChanged,
            placeholder = stringResource(R.string.forum_draft_label),
            sendLabel = stringResource(R.string.forum_post_button),
            isSending = uiState.isPosting,
            onSend = viewModel::onPost,
            errorMessage = when {
                uiState.isTextRejected -> stringResource(R.string.text_contains_banned_words)
                uiState.isSendFailed -> stringResource(R.string.message_not_sent_msg)
                else -> null
            },
            banner = composerBanner(uiState.composerMode),
            cancelBannerLabel = stringResource(CoreR.string.cancel),
            onCancelBanner = viewModel::onCancelComposerMode,
            modifier = Modifier.windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars)),
        )
    }
}

@Composable
private fun ForumPostList(
    uiState: ForumUiState,
    listState: LazyListState,
    contentPadding: PaddingValues,
    onMessageAction: (MessageAction, MessageTarget) -> Unit,
    onReport: (String, ReportReason) -> Unit,
    onQuoteClick: (String) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding + PaddingValues(
            horizontal = Dimens.ScreenHorizontalPadding,
            vertical = Dimens.SpacingMedium,
        ),
    ) {
        listSectionItems(uiState.posts, key = { it.id }) { post ->
            val target = MessageTarget.of(post, uiState.currentUserId)
            MessageActionsBox(target = target, onAction = onMessageAction) {
                ForumPostRow(
                    post = post,
                    canReport = target.canReport && !post.isDeleted,
                    isReported = post.id in uiState.reportedPostIds,
                    onReport = { reason -> onReport(post.id, reason) },
                    onQuoteClick = onQuoteClick,
                )
            }
        }
    }
}

@Composable
private fun ForumPostRow(
    post: ForumPost,
    canReport: Boolean,
    isReported: Boolean,
    onReport: (ReportReason) -> Unit,
    onQuoteClick: (String) -> Unit,
) {
    val timestamp = DateFormat.getDateTimeInstance().format(Date(post.createdAtEpochMillis))
    Column(modifier = Modifier.padding(horizontal = Dimens.CardPadding, vertical = Dimens.SpacingCompact)) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfileAvatar(style = post.authorAvatar ?: AvatarStyle.LIME, size = Dimens.HistoryTileSize)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = Dimens.SpacingCompact),
                ) {
                    Text(
                        text = post.authorName ?: stringResource(R.string.guest),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = if (post.isEdited && !post.isDeleted) {
                            stringResource(R.string.date_time, timestamp, stringResource(R.string.edited))
                        } else {
                            timestamp
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                when {
                    isReported -> Text(
                        text = stringResource(R.string.report_sent),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                    )

                    canReport -> ReportMenu(onReport = onReport)
                }
            }
            MessageContent(
                text = post.text,
                isDeleted = post.isDeleted,
                replyTo = post.replyTo,
                forwardedFrom = post.forwardedFrom,
                onQuoteClick = onQuoteClick,
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            )
        }
    }
}

@Composable
private fun ReportMenu(onReport: (ReportReason) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { isExpanded = true }) {
            Icon(
                imageVector = Icons.Outlined.Flag,
                contentDescription = stringResource(R.string.report),
                tint = MaterialTheme.colorScheme.outline,
            )
        }
        DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            ReportReason.entries.forEach { reason ->
                DropdownMenuItem(
                    text = { Text(stringResource(reportReasonLabelRes(reason))) },
                    onClick = {
                        isExpanded = false
                        onReport(reason)
                    },
                )
            }
        }
    }
}
