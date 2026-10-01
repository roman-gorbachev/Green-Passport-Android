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
import com.smartcity.greenpassport.feature.community.presentation.viewmodels.ForumViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun ForumScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: ForumViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listPadding = PaddingValues(top = contentPadding.calculateTopPadding())

    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            when {
                uiState.isLoading -> LoadingContent(modifier = Modifier.padding(listPadding))
                uiState.posts.isEmpty() -> EmptyContent(
                    message = stringResource(R.string.forum_empty),
                    modifier = Modifier.padding(listPadding),
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = listPadding + PaddingValues(
                        horizontal = Dimens.ScreenHorizontalPadding,
                        vertical = Dimens.SpacingMedium,
                    ),
                ) {
                    listSectionItems(uiState.posts, key = { it.id }) { post ->
                        ForumPostRow(
                            post = post,
                            canReport = uiState.currentUserId != null && post.authorId != uiState.currentUserId,
                            isReported = post.id in uiState.reportedPostIds,
                            onReport = { reason -> viewModel.onReport(post.id, reason) },
                        )
                    }
                }
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
            modifier = Modifier.windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars)),
        )
    }
}

@Composable
private fun ForumPostRow(
    post: ForumPost,
    canReport: Boolean,
    isReported: Boolean,
    onReport: (ReportReason) -> Unit,
) {
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
                        text = DateFormat.getDateTimeInstance().format(Date(post.createdAtEpochMillis)),
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
            Text(
                text = post.text,
                style = MaterialTheme.typography.bodyLarge,
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

private fun reportReasonLabelRes(reason: ReportReason): Int = when (reason) {
    ReportReason.OFFENSIVE -> R.string.insults_or_obscenity
    ReportReason.SPAM -> R.string.spam
    ReportReason.INAPPROPRIATE_IMAGE -> R.string.inappropriate_content
    ReportReason.OTHER -> R.string.other
}
