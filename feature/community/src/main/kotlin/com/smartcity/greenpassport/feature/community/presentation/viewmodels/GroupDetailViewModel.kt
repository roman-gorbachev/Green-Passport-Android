package com.smartcity.greenpassport.feature.community.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.core.model.GroupMessage
import com.smartcity.greenpassport.core.moderation.ContentRejectedException
import com.smartcity.greenpassport.feature.community.domain.FetchGroupMembersUseCase
import com.smartcity.greenpassport.feature.community.domain.JoinGroupUseCase
import com.smartcity.greenpassport.feature.community.domain.LeaveGroupUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveCommunitySessionUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveGroupMessagesUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveGroupUseCase
import com.smartcity.greenpassport.feature.community.domain.SendGroupMessageUseCase
import com.smartcity.greenpassport.feature.community.presentation.state.GroupDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
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
class GroupDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeGroup: ObserveGroupUseCase,
    observeSession: ObserveCommunitySessionUseCase,
    private val observeMessages: ObserveGroupMessagesUseCase,
    private val sendMessage: SendGroupMessageUseCase,
    private val joinGroup: JoinGroupUseCase,
    private val leaveGroup: LeaveGroupUseCase,
    private val fetchMembers: FetchGroupMembersUseCase,
) : ViewModel() {

    private val groupId: String = checkNotNull(savedStateHandle["groupId"])

    private val local = MutableStateFlow(GroupDetailUiState())

    private val groupData = combine(
        observeGroup(groupId).map<CommunityGroup?, GroupLoad> { GroupLoad.Loaded(it) }.catch { emit(GroupLoad.Failed) },
        observeSession(),
    ) { load, session -> load to session?.userId }

    val uiState = observeGroupDetailUiState().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        GroupDetailUiState(),
    )

    fun onDraftChanged(text: String) {
        local.update { it.copy(draft = text, isTextRejected = false, isSendFailed = false) }
    }

    fun onSend() {
        val state = uiState.value
        val userId = state.currentUserId ?: return
        val text = state.draft.trim()
        if (!state.isMember || text.isEmpty() || state.isSending) return
        viewModelScope.launch {
            local.update { it.copy(isSending = true, isSendFailed = false) }
            runCatching { sendMessage(groupId, userId, text) }
                .onSuccess { local.update { it.copy(isSending = false, draft = "") } }
                .onFailure { error ->
                    Log.w(TAG, "Failed to send message", error)
                    val isRejected = error is ContentRejectedException
                    local.update { it.copy(isSending = false, isTextRejected = isRejected, isSendFailed = !isRejected) }
                }
        }
    }

    fun onJoin() {
        val userId = uiState.value.currentUserId ?: return
        if (uiState.value.isJoining) return
        viewModelScope.launch {
            local.update { it.copy(isJoining = true) }
            runCatching { joinGroup(groupId, userId) }
                .onFailure { error -> Log.w(TAG, "Failed to join group", error) }
            local.update { it.copy(isJoining = false) }
        }
    }

    fun onLeaveConfirmationVisibilityChanged(isVisible: Boolean) {
        local.update { it.copy(isLeaveConfirmationVisible = isVisible) }
    }

    fun onLeave() {
        val userId = uiState.value.currentUserId ?: return
        if (uiState.value.isLeaving) return
        viewModelScope.launch {
            local.update { it.copy(isLeaving = true, isLeaveConfirmationVisible = false) }
            runCatching { leaveGroup(groupId, userId) }
                .onFailure { error -> Log.w(TAG, "Failed to leave group", error) }
            local.update { it.copy(isLeaving = false) }
        }
    }

    fun onMembersVisibilityChanged(isVisible: Boolean) {
        local.update { it.copy(isMembersVisible = isVisible) }
        val memberIds = uiState.value.group?.memberIds ?: return
        if (!isVisible) return
        viewModelScope.launch {
            local.update { it.copy(isLoadingMembers = true) }
            val members = runCatching { fetchMembers(memberIds) }.getOrDefault(emptyList())
            local.update { it.copy(isLoadingMembers = false, members = members) }
        }
    }

    private fun observeGroupDetailUiState(): Flow<GroupDetailUiState> {
        val canReadChat = combine(groupData, local) { (load, userId), state ->
            val group = (load as? GroupLoad.Loaded)?.group
            userId != null && group?.memberIds?.contains(userId) == true && !state.isJoining && !state.isLeaving
        }.distinctUntilChanged()
        val messages = canReadChat.flatMapLatest { canRead ->
            if (canRead) {
                observeMessages(groupId)
                    .map<List<GroupMessage>, MessagesLoad> { MessagesLoad.Loaded(it) }
                    .onStart { emit(MessagesLoad.Loading) }
                    .catch { emit(MessagesLoad.Failed) }
            } else {
                flowOf(MessagesLoad.Loading)
            }
        }
        return combine(groupData, messages, local) { (load, userId), messagesLoad, state ->
            val group = (load as? GroupLoad.Loaded)?.group
            state.copy(
                group = group,
                currentUserId = userId,
                isLoading = false,
                hasError = load is GroupLoad.Failed || group == null,
                messages = (messagesLoad as? MessagesLoad.Loaded)?.messages.orEmpty(),
                isLoadingMessages = messagesLoad is MessagesLoad.Loading,
                hasMessagesError = messagesLoad is MessagesLoad.Failed,
            )
        }
    }

    private sealed interface GroupLoad {
        data class Loaded(val group: CommunityGroup?) : GroupLoad

        data object Failed : GroupLoad
    }

    private sealed interface MessagesLoad {
        data object Loading : MessagesLoad

        data class Loaded(val messages: List<GroupMessage>) : MessagesLoad

        data object Failed : MessagesLoad
    }

    companion object {
        private const val TAG = "GroupDetailViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
