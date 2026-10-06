package com.smartcity.greenpassport.feature.community.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.core.model.community.ChatSettings
import com.smartcity.greenpassport.core.model.community.ChatSummary
import com.smartcity.greenpassport.core.model.community.GroupSearch
import com.smartcity.greenpassport.core.moderation.ContentRejectedException
import com.smartcity.greenpassport.feature.community.domain.CreateGroupUseCase
import com.smartcity.greenpassport.feature.community.domain.FetchGroupMembersUseCase
import com.smartcity.greenpassport.feature.community.domain.JoinGroupByCodeUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveChatSettingsUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveCommunitySessionUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveGroupsUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveLatestForumPostAtUseCase
import com.smartcity.greenpassport.feature.community.domain.UpdateChatSettingsUseCase
import com.smartcity.greenpassport.feature.community.presentation.state.ChatListAction
import com.smartcity.greenpassport.feature.community.presentation.state.CommunityHubUiState
import com.smartcity.greenpassport.feature.community.presentation.state.applying
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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CommunityHubViewModel @Inject constructor(
    observeSession: ObserveCommunitySessionUseCase,
    private val observeGroups: ObserveGroupsUseCase,
    private val observeLatestForumPostAt: ObserveLatestForumPostAtUseCase,
    private val observeChatSettings: ObserveChatSettingsUseCase,
    private val updateChatSettings: UpdateChatSettingsUseCase,
    private val fetchMembers: FetchGroupMembersUseCase,
    private val createGroup: CreateGroupUseCase,
    private val joinGroupByCode: JoinGroupByCodeUseCase,
) : ViewModel() {

    private val userId = observeSession()
        .map { it?.userId }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val local = MutableStateFlow(CommunityHubUiState())
    private val pendingSettings = MutableStateFlow<Map<ChatId, ChatSettings>>(emptyMap())
    private val memberNames = MutableStateFlow<Map<String, String>>(emptyMap())
    private val requestedMemberIds = mutableSetOf<String>()
    private var allGroups: List<CommunityGroup> = emptyList()

    val uiState = observeHubUiState().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        CommunityHubUiState(),
    )

    fun onQueryChanged(query: String) {
        local.update { it.copy(query = query) }
        if (query.isNotBlank()) loadMissingMemberNames()
    }

    fun onChatAction(action: ChatListAction, chat: ChatSummary) {
        val currentUserId = userId.value ?: return
        val updated = chat.settings.applying(action)
        pendingSettings.update { it + (chat.chatId to updated) }
        viewModelScope.launch {
            runCatching { updateChatSettings(updated, currentUserId) }
                .onFailure { error -> Log.w(TAG, "Failed to update chat settings", error) }
            pendingSettings.update { it - chat.chatId }
        }
    }

    fun onCreateDialogVisibilityChanged(isVisible: Boolean) {
        local.update { it.copy(isCreateDialogVisible = isVisible, groupDraftName = "", isGroupNameRejected = false) }
    }

    fun onGroupDraftNameChanged(name: String) {
        local.update { it.copy(groupDraftName = name, isGroupNameRejected = false) }
    }

    fun onCreateGroup() {
        val creatorId = userId.value ?: return
        val name = local.value.groupDraftName.trim()
        if (name.isEmpty() || local.value.isCreatingGroup) return
        viewModelScope.launch {
            local.update { it.copy(isCreatingGroup = true) }
            runCatching { createGroup(name, creatorId) }
                .onSuccess {
                    local.update {
                        it.copy(
                            isCreatingGroup = false,
                            isCreateDialogVisible = false,
                            groupDraftName = ""
                        )
                    }
                }
                .onFailure { error ->
                    Log.w(TAG, "Failed to create group", error)
                    local.update {
                        it.copy(isCreatingGroup = false, isGroupNameRejected = error is ContentRejectedException)
                    }
                }
        }
    }

    fun onCodeDialogVisibilityChanged(isVisible: Boolean) {
        local.update { it.copy(isCodeDialogVisible = isVisible, inviteCodeDraft = "", isInviteCodeNotFound = false) }
    }

    fun onInviteCodeChanged(code: String) {
        local.update { it.copy(inviteCodeDraft = code, isInviteCodeNotFound = false) }
    }

    fun onJoinByCode() {
        val currentUserId = userId.value ?: return
        if (local.value.isJoiningByCode) return
        viewModelScope.launch {
            local.update { it.copy(isJoiningByCode = true) }
            runCatching { joinGroupByCode(local.value.inviteCodeDraft, currentUserId) }
                .onSuccess { group ->
                    local.update {
                        it.copy(
                            isJoiningByCode = false,
                            isCodeDialogVisible = false,
                            inviteCodeDraft = "",
                            openedGroupId = group.id,
                        )
                    }
                }
                .onFailure { error ->
                    Log.w(TAG, "Failed to join by code", error)
                    local.update { it.copy(isJoiningByCode = false, isInviteCodeNotFound = true) }
                }
        }
    }

    fun onOpenedGroupShown() {
        local.update { it.copy(openedGroupId = null) }
    }

    private fun observeHubUiState(): Flow<CommunityHubUiState> {
        val groups = observeGroups()
            .onStart { emit(emptyList()) }
            .catch { emit(emptyList()) }
            .onEach { list ->
                allGroups = list
                if (local.value.isSearching) loadMissingMemberNames()
            }
        val settings = userId.flatMapLatest { id ->
            if (id == null) flowOf(emptyMap()) else observeChatSettings(id).catch { emit(emptyMap()) }
        }.onStart { emit(emptyMap()) }
        val forumAt = observeLatestForumPostAt().catch { emit(null) }.onStart { emit(null) }
        val allSettings = combine(settings, pendingSettings) { stored, pending -> stored + pending }
        return combine(local, groups, allSettings, forumAt, memberNames) { state, all, chatSettings, forumTime, names ->
            val currentUserId = userId.value
            val mine = all.filter { group -> currentUserId != null && group.memberIds.contains(currentUserId) }
            val myIds = mine.map { it.id }.toSet()
            val summaries = ChatSummary.groups(mine, chatSettings)
            val matches = GroupSearch.matches(all, state.query, names)
            state.copy(
                forum = ChatSummary.forum(forumTime, chatSettings),
                groups = summaries.filterNot { it.settings.isArchived },
                archivedCount = summaries.count { it.settings.isArchived },
                myGroupMatches = matches.filter { it.group.id in myIds },
                otherGroupMatches = matches.filterNot { it.group.id in myIds },
                currentUserId = currentUserId,
                isLoading = false,
            )
        }
    }

    private fun loadMissingMemberNames() {
        val missing = allGroups.flatMap { it.memberIds }.toSet() - requestedMemberIds
        if (missing.isEmpty()) return
        requestedMemberIds += missing
        viewModelScope.launch {
            runCatching { fetchMembers(missing.toList()) }
                .onSuccess { members ->
                    val names = members.mapNotNull { member -> member.name?.let { member.id to it } }
                    memberNames.update { it + names }
                }
                .onFailure { error ->
                    Log.w(TAG, "Failed to load member names", error)
                    requestedMemberIds -= missing
                }
        }
    }

    companion object {
        private const val TAG = "CommunityHubViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
