package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.GpPrimaryButton
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.MessageComposer
import com.smartcity.greenpassport.core.designsystem.component.ProfileAvatar
import com.smartcity.greenpassport.core.designsystem.component.SymbolTile
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.core.model.GroupMessage
import com.smartcity.greenpassport.core.model.profile.AvatarStyle
import com.smartcity.greenpassport.feature.community.R
import com.smartcity.greenpassport.feature.community.presentation.state.GroupDetailUiState
import com.smartcity.greenpassport.feature.community.presentation.state.MessageAction
import com.smartcity.greenpassport.feature.community.presentation.state.MessageTarget
import com.smartcity.greenpassport.feature.community.presentation.viewmodels.GroupDetailViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun GroupDetailScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: GroupDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val group = uiState.group

    if (uiState.isLeaveConfirmationVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.onLeaveConfirmationVisibilityChanged(false) },
            title = { Text(stringResource(R.string.leave_group)) },
            text = { Text(stringResource(R.string.leave_group_confirm_msg)) },
            confirmButton = {
                TextButton(onClick = viewModel::onLeave) {
                    Text(text = stringResource(R.string.leave_group), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onLeaveConfirmationVisibilityChanged(false) }) {
                    Text(stringResource(CoreR.string.cancel))
                }
            },
        )
    }
    uiState.pendingDeletion?.let {
        DeleteMessageDialog(onConfirm = viewModel::onDeletionConfirmed, onDismiss = viewModel::onDeletionDismissed)
    }
    if (uiState.isMembersVisible) {
        GroupMembersSheet(
            members = uiState.members,
            isLoading = uiState.isLoadingMembers,
            onDismiss = { viewModel.onMembersVisibilityChanged(false) },
        )
    }

    when {
        uiState.isLoading -> LoadingContent(modifier = modifier.padding(contentPadding))
        uiState.hasError || group == null -> ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = {},
            modifier = modifier.padding(contentPadding),
        )
        uiState.isMember -> GroupChat(
            uiState = uiState,
            onDraftChange = viewModel::onDraftChanged,
            onSend = viewModel::onSend,
            onMessageAction = viewModel::onMessageAction,
            onCancelComposerMode = viewModel::onCancelComposerMode,
            contentPadding = contentPadding,
            modifier = modifier,
        )
        else -> JoinPrompt(
            group = group,
            isJoining = uiState.isJoining,
            onJoin = viewModel::onJoin,
            contentPadding = contentPadding,
            modifier = modifier,
        )
    }
}

@Composable
private fun GroupChat(
    uiState: GroupDetailUiState,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    onMessageAction: (MessageAction, MessageTarget) -> Unit,
    onCancelComposerMode: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val copyToClipboard = rememberCopyToClipboard()
    var forwardedMessage by remember { mutableStateOf<MessageTarget?>(null) }
    val handleAction = { action: MessageAction, target: MessageTarget ->
        when (action) {
            MessageAction.Copy -> copyToClipboard(target.text)
            MessageAction.Forward -> forwardedMessage = target
            else -> onMessageAction(action, target)
        }
    }
    val onQuoteClick = { messageId: String ->
        val index = uiState.messages.indexOfFirst { it.id == messageId }
        if (index >= 0) scope.launch { listState.animateScrollToItem(index) }
    }
    forwardedMessage?.let { message ->
        ForwardSheet(message = message, onDismiss = { forwardedMessage = null })
    }
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) listState.scrollToItem(uiState.messages.lastIndex)
    }
    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            val topPadding = PaddingValues(top = contentPadding.calculateTopPadding())
            when {
                uiState.hasMessagesError || uiState.isLoadingMessages -> LoadingContent(
                    modifier = Modifier.padding(topPadding)
                )
                uiState.messages.isEmpty() -> EmptyContent(
                    message = stringResource(R.string.group_chat_empty_msg),
                    modifier = Modifier.padding(topPadding),
                )
                else -> LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
                    contentPadding = PaddingValues(
                        start = Dimens.ScreenHorizontalPadding,
                        end = Dimens.ScreenHorizontalPadding,
                        top = contentPadding.calculateTopPadding() + Dimens.SpacingCompact,
                        bottom = Dimens.SpacingCompact,
                    ),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(uiState.messages, key = { it.id }) { message ->
                        MessageRow(
                            message = message,
                            target = MessageTarget.of(message, uiState.currentUserId),
                            onAction = handleAction,
                            onQuoteClick = onQuoteClick,
                        )
                    }
                }
            }
        }
        MessageComposer(
            draft = uiState.draft,
            onDraftChange = onDraftChange,
            placeholder = stringResource(R.string.forum_draft_label),
            sendLabel = stringResource(R.string.forum_post_button),
            isSending = uiState.isSending,
            onSend = onSend,
            errorMessage = when {
                uiState.isTextRejected -> stringResource(R.string.text_contains_banned_words)
                uiState.isSendFailed -> stringResource(R.string.message_not_sent_msg)
                else -> null
            },
            banner = composerBanner(uiState.composerMode),
            cancelBannerLabel = stringResource(CoreR.string.cancel),
            onCancelBanner = onCancelComposerMode,
            modifier = Modifier.windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars)),
        )
    }
}

@Composable
private fun MessageRow(
    message: GroupMessage,
    target: MessageTarget,
    onAction: (MessageAction, MessageTarget) -> Unit,
    onQuoteClick: (String) -> Unit,
) {
    val isOwn = target.isOwn
    val sentAt = DateFormat.getTimeInstance(DateFormat.SHORT, LocalLocale.current.platformLocale)
        .format(Date(message.sentAtEpochMillis))
    val time = if (message.isEdited && !message.isDeleted) {
        stringResource(R.string.date_time, stringResource(R.string.edited), sentAt)
    } else {
        sentAt
    }
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (isOwn) {
            Spacer(modifier = Modifier.weight(1f))
        } else {
            ProfileAvatar(style = message.senderAvatar ?: AvatarStyle.LIME, size = Dimens.MessageAvatarSize)
        }
        MessageActionsBox(target = target, onAction = onAction) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingHairline),
                modifier = Modifier
                    .widthIn(max = Dimens.MessageBubbleMaxWidth)
                    .background(
                        color = if (isOwn) {
                            MaterialTheme.colorScheme.surfaceContainerHighest
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
                    )
                    .padding(horizontal = Dimens.CardPadding, vertical = Dimens.SpacingSmall),
            ) {
                if (!isOwn) {
                    Text(
                        text = message.senderName ?: stringResource(R.string.guest),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                MessageContent(
                    text = message.text,
                    isDeleted = message.isDeleted,
                    replyTo = message.replyTo,
                    forwardedFrom = message.forwardedFrom,
                    onQuoteClick = onQuoteClick,
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (!isOwn) Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun JoinPrompt(
    group: CommunityGroup,
    isJoining: Boolean,
    onJoin: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(Dimens.ScreenHorizontalPadding),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium, Alignment.CenterVertically),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            SymbolTile(icon = Icons.Filled.Groups, size = Dimens.SheetMascotSize)
            Text(text = group.name, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            Text(
                text = stringResource(R.string.groups_member_count_format, group.memberIds.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.join_group_to_chat_msg),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        GpPrimaryButton(text = stringResource(R.string.groups_join_button), onClick = onJoin, isLoading = isJoining)
    }
}
