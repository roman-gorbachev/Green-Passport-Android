package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ListSection
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.feature.community.presentation.viewmodels.ChatListViewModel

@Composable
fun ArchivedChatsScreen(
    onChatSelected: (ChatId) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: ChatListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ListSection(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingMedium),
    ) {
        uiState.archivedChats.forEachIndexed { index, chat ->
            ChatListRow(
                chat = chat,
                onOpen = { onChatSelected(chat.chatId) },
                onAction = { action -> viewModel.onChatAction(action, chat) },
                showDivider = index < uiState.archivedChats.lastIndex,
            )
        }
    }
}
