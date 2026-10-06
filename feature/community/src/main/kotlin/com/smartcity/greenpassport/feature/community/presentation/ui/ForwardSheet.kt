package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.GpSheetScaffold
import com.smartcity.greenpassport.core.designsystem.component.ListSection
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.feature.community.R
import com.smartcity.greenpassport.feature.community.presentation.state.MessageTarget
import com.smartcity.greenpassport.feature.community.presentation.viewmodels.ForwardViewModel

@Composable
fun ForwardSheet(
    message: MessageTarget,
    onDismiss: () -> Unit,
    viewModel: ForwardViewModel = hiltViewModel(key = "forward_${message.id}"),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.isSent) {
        if (uiState.isSent) onDismiss()
    }
    GpSheetScaffold(onDismiss = onDismiss) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingCompact)) {
            Text(text = stringResource(R.string.forward_to), style = MaterialTheme.typography.titleLarge)
            ListSection {
                val forum: Pair<ChatId, String> = ChatId.Forum to stringResource(R.string.community_forum_title)
                val chats = listOf(forum) + uiState.groups.map { ChatId.Group(it.id) to it.name }
                chats.forEachIndexed { index, (chatId, title) ->
                    ChatRow(
                        chatId = chatId,
                        title = title,
                        isLoading = uiState.sendingChatId == chatId,
                        onClick = { viewModel.onForward(message, chatId) },
                        showDivider = index < chats.lastIndex,
                    )
                }
            }
            val failure = when {
                uiState.isTextRejected -> stringResource(R.string.text_contains_banned_words)
                uiState.isSendFailed -> stringResource(R.string.message_not_sent_msg)
                else -> null
            }
            if (failure != null) {
                Text(
                    text = failure,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = Dimens.CardPadding),
                )
            }
        }
    }
}
