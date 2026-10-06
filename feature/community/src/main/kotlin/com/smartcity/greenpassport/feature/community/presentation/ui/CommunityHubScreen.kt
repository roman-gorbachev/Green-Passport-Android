package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Groups
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ListSection
import com.smartcity.greenpassport.core.designsystem.component.ListSectionRow
import com.smartcity.greenpassport.core.designsystem.component.SymbolTile
import com.smartcity.greenpassport.core.designsystem.component.SymbolTileStyle
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.feature.community.R
import com.smartcity.greenpassport.feature.community.presentation.viewmodels.ChatListViewModel

@Composable
fun CommunityHubScreen(
    onChatSelected: (ChatId) -> Unit,
    onArchiveSelected: () -> Unit,
    onGroupsSelected: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: ChatListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingCompact),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = Dimens.SpacingMedium),
    ) {
        ListSection(header = stringResource(R.string.chats)) {
            uiState.activeChats.forEachIndexed { index, chat ->
                ChatListRow(
                    chat = chat,
                    onOpen = { onChatSelected(chat.chatId) },
                    onAction = { action -> viewModel.onChatAction(action, chat) },
                    showDivider = index < uiState.activeChats.lastIndex,
                )
            }
        }
        ListSection {
            if (uiState.archivedChats.isNotEmpty()) {
                ListSectionRow(
                    title = stringResource(R.string.archived_chats),
                    subtitle = uiState.archivedChats.size.toString(),
                    leading = { SymbolTile(icon = Icons.Filled.Archive, style = SymbolTileStyle.Muted) },
                    onClick = onArchiveSelected,
                    showDivider = true,
                )
            }
            ListSectionRow(
                title = stringResource(R.string.community_groups_title),
                leading = { SymbolTile(icon = Icons.Filled.Groups, style = SymbolTileStyle.Muted) },
                onClick = onGroupsSelected,
            )
        }
    }
}
