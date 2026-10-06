package com.smartcity.greenpassport.feature.community.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.core.model.community.ChatSettings
import com.smartcity.greenpassport.core.model.community.ChatSummary
import com.smartcity.greenpassport.feature.community.domain.ObserveChatSettingsUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveCommunitySessionUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveLatestForumPostAtUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveMyGroupsUseCase
import com.smartcity.greenpassport.feature.community.domain.UpdateChatSettingsUseCase
import com.smartcity.greenpassport.feature.community.presentation.state.ChatListAction
import com.smartcity.greenpassport.feature.community.presentation.state.ChatListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChatListViewModel @Inject constructor(
    observeSession: ObserveCommunitySessionUseCase,
    private val observeMyGroups: ObserveMyGroupsUseCase,
    private val observeLatestForumPostAt: ObserveLatestForumPostAtUseCase,
    private val observeChatSettings: ObserveChatSettingsUseCase,
    private val updateChatSettings: UpdateChatSettingsUseCase,
) : ViewModel() {

    private val userId = observeSession()
        .map { it?.userId }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val pendingSettings = MutableStateFlow<Map<ChatId, ChatSettings>>(emptyMap())

    val uiState = observeChatListUiState().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        ChatListUiState(),
    )

    fun onChatAction(action: ChatListAction, chat: ChatSummary) {
        val currentUserId = userId.value ?: return
        val updated = when (action) {
            ChatListAction.TOGGLE_PIN -> chat.settings.copy(isPinned = !chat.settings.isPinned)
            ChatListAction.TOGGLE_MUTE -> chat.settings.copy(isMuted = !chat.settings.isMuted)
            ChatListAction.TOGGLE_ARCHIVE -> chat.settings.copy(isArchived = !chat.settings.isArchived)
        }
        pendingSettings.update { it + (chat.chatId to updated) }
        viewModelScope.launch {
            runCatching { updateChatSettings(updated, currentUserId) }
                .onFailure { error -> Log.w(TAG, "Failed to update chat settings", error) }
            pendingSettings.update { it - chat.chatId }
        }
    }

    private fun observeChatListUiState(): Flow<ChatListUiState> {
        val forumLastMessageAt = observeLatestForumPostAt().catch { emit(null) }.onStart { emit(null) }
        val userData = userId.flatMapLatest { id ->
            if (id == null) {
                flowOf(UserChats(groups = emptyList(), settings = emptyMap(), isLoaded = true))
            } else {
                combine(
                    observeMyGroups(id).catch { emit(emptyList()) },
                    observeChatSettings(id).catch { emit(emptyMap()) }.onStart { emit(emptyMap()) },
                ) { groups, settings -> UserChats(groups = groups, settings = settings, isLoaded = true) }
            }
        }
        return combine(userData, forumLastMessageAt, pendingSettings) { chats, forumAt, pending ->
            val all = ChatSummary.list(
                groups = chats.groups,
                forumLastMessageAtEpochMillis = forumAt,
                settings = chats.settings + pending,
            )
            ChatListUiState(
                activeChats = all.filterNot { it.settings.isArchived },
                archivedChats = all.filter { it.settings.isArchived },
                isLoading = !chats.isLoaded,
            )
        }
    }

    private data class UserChats(
        val groups: List<CommunityGroup>,
        val settings: Map<ChatId, ChatSettings>,
        val isLoaded: Boolean,
    )

    companion object {
        private const val TAG = "ChatListViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
