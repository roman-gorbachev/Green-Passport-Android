package com.smartcity.greenpassport.feature.community.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ListSection
import com.smartcity.greenpassport.core.designsystem.component.ListSectionRow
import com.smartcity.greenpassport.core.designsystem.component.SymbolTile
import com.smartcity.greenpassport.core.designsystem.component.SymbolTileStyle
import com.smartcity.greenpassport.core.designsystem.layout.plus
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.core.model.community.GroupSearchResult
import com.smartcity.greenpassport.feature.community.R
import com.smartcity.greenpassport.feature.community.presentation.state.ChatListAction
import com.smartcity.greenpassport.feature.community.presentation.state.CommunityHubUiState
import com.smartcity.greenpassport.feature.community.presentation.viewmodels.CommunityHubViewModel

@Composable
fun CommunityHubScreen(
    onChatSelected: (ChatId) -> Unit,
    onArchiveSelected: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: CommunityHubViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.openedGroupId) {
        val groupId = uiState.openedGroupId ?: return@LaunchedEffect
        onChatSelected(ChatId.Group(groupId))
        viewModel.onOpenedGroupShown()
    }
    HubDialogs(uiState = uiState, viewModel = viewModel)

    val listState = rememberLazyListState()
    var isArchiveRevealed by rememberSaveable { mutableStateOf(false) }
    val archiveConnection = rememberArchiveRevealConnection(
        onReveal = { isArchiveRevealed = true },
        onHide = { isArchiveRevealed = false },
    )

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge),
        contentPadding = contentPadding + PaddingValues(
            horizontal = Dimens.ScreenHorizontalPadding,
            vertical = Dimens.SpacingMedium,
        ),
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(archiveConnection),
    ) {
        item(key = SEARCH_KEY) {
            SearchField(query = uiState.query, onQueryChange = viewModel::onQueryChanged)
        }
        if (uiState.isSearching) {
            item(key = RESULTS_KEY) {
                SearchResults(uiState = uiState, onChatSelected = onChatSelected)
            }
        } else {
            item(key = ARCHIVE_KEY) {
                AnimatedVisibility(
                    visible = isArchiveRevealed && uiState.archivedCount > 0,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    ListSection {
                        ListSectionRow(
                            title = stringResource(R.string.archived_chats),
                            subtitle = uiState.archivedCount.toString(),
                            leading = { SymbolTile(icon = Icons.Filled.Archive, style = SymbolTileStyle.Muted) },
                            onClick = onArchiveSelected,
                        )
                    }
                }
            }
            item(key = FORUM_KEY) {
                ListSection(header = stringResource(R.string.community_forum_title)) {
                    ChatListRow(
                        chat = uiState.forum,
                        onOpen = { onChatSelected(ChatId.Forum) },
                        onAction = { action -> viewModel.onChatAction(action, uiState.forum) },
                        showDivider = false,
                        actions = forumActions(uiState.currentUserId),
                    )
                }
            }
            item(key = GROUPS_KEY) {
                MyGroupsSection(uiState = uiState, onChatSelected = onChatSelected, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun MyGroupsSection(
    uiState: CommunityHubUiState,
    onChatSelected: (ChatId) -> Unit,
    viewModel: CommunityHubViewModel,
) {
    ListSection(header = stringResource(R.string.my_groups)) {
        if (uiState.groups.isEmpty() && !uiState.isLoading) {
            Text(
                text = stringResource(R.string.my_groups_empty_msg),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(Dimens.CardPadding),
            )
        }
        uiState.groups.forEachIndexed { index, chat ->
            ChatListRow(
                chat = chat,
                onOpen = { onChatSelected(chat.chatId) },
                onAction = { action -> viewModel.onChatAction(action, chat) },
                showDivider = index < uiState.groups.lastIndex,
            )
        }
    }
}

@Composable
private fun SearchResults(uiState: CommunityHubUiState, onChatSelected: (ChatId) -> Unit) {
    if (uiState.myGroupMatches.isEmpty() && uiState.otherGroupMatches.isEmpty()) {
        Text(
            text = stringResource(R.string.groups_not_found),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = Dimens.CardPadding),
        )
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge)) {
        SearchSection(R.string.my_groups, uiState.myGroupMatches, onChatSelected)
        SearchSection(R.string.other_groups, uiState.otherGroupMatches, onChatSelected)
    }
}

@Composable
private fun SearchSection(titleRes: Int, results: List<GroupSearchResult>, onChatSelected: (ChatId) -> Unit) {
    if (results.isEmpty()) return
    ListSection(header = stringResource(titleRes)) {
        results.forEachIndexed { index, result ->
            val chatId = ChatId.Group(result.group.id)
            ChatRow(
                chatId = chatId,
                title = result.group.name,
                subtitle = result.matchedMemberName?.let { stringResource(R.string.member_match_format, it) }
                    ?: stringResource(R.string.groups_member_count_format, result.group.memberIds.size),
                onClick = { onChatSelected(chatId) },
                showDivider = index < results.lastIndex,
            )
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(R.string.search_groups_placeholder)) },
        leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = null)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun HubDialogs(uiState: CommunityHubUiState, viewModel: CommunityHubViewModel) {
    val bannedWords = stringResource(R.string.text_contains_banned_words)
    val notFound = stringResource(R.string.group_not_found_msg)
    if (uiState.isCreateDialogVisible) {
        TextInputDialog(
            content = TextInputDialogContent(
                title = stringResource(R.string.create_group),
                label = stringResource(R.string.groups_draft_label),
                confirmLabel = stringResource(R.string.groups_create_button),
                errorMessage = bannedWords.takeIf { uiState.isGroupNameRejected },
                isLoading = uiState.isCreatingGroup,
            ),
            value = uiState.groupDraftName,
            onValueChange = viewModel::onGroupDraftNameChanged,
            onConfirm = viewModel::onCreateGroup,
            onDismiss = { viewModel.onCreateDialogVisibilityChanged(false) },
        )
    }
    if (uiState.isCodeDialogVisible) {
        TextInputDialog(
            content = TextInputDialogContent(
                title = stringResource(R.string.join_by_code),
                label = stringResource(R.string.invite_code),
                confirmLabel = stringResource(R.string.groups_join_button),
                errorMessage = notFound.takeIf { uiState.isInviteCodeNotFound },
                isLoading = uiState.isJoiningByCode,
                capitalization = KeyboardCapitalization.Characters,
            ),
            value = uiState.inviteCodeDraft,
            onValueChange = viewModel::onInviteCodeChanged,
            onConfirm = viewModel::onJoinByCode,
            onDismiss = { viewModel.onCodeDialogVisibilityChanged(false) },
        )
    }
}

private fun forumActions(currentUserId: String?): List<ChatListAction> =
    if (currentUserId != null) listOf(ChatListAction.TOGGLE_MUTE) else emptyList()

@Composable
private fun rememberArchiveRevealConnection(
    onReveal: () -> Unit,
    onHide: () -> Unit,
): NestedScrollConnection {
    val density = LocalDensity.current
    val revealDistance = with(density) { Dimens.ArchiveRevealDistance.toPx() }
    val hideDistance = with(density) { Dimens.ArchiveHideDistance.toPx() }
    return remember(revealDistance, hideDistance) {
        object : NestedScrollConnection {
            private var pulled = 0f
            private var pushed = 0f

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                val delta = consumed.y + available.y
                if (delta > 0f && available.y > 0f) {
                    pushed = 0f
                    pulled += available.y
                    if (pulled > revealDistance) onReveal()
                } else if (delta < 0f) {
                    pulled = 0f
                    pushed -= delta
                    if (pushed > hideDistance) onHide()
                }
                return Offset.Zero
            }
        }
    }
}

private const val SEARCH_KEY = "search"
private const val RESULTS_KEY = "results"
private const val ARCHIVE_KEY = "archive"
private const val FORUM_KEY = "forum"
private const val GROUPS_KEY = "groups"
