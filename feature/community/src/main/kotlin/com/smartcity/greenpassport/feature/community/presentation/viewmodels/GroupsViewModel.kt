package com.smartcity.greenpassport.feature.community.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.core.moderation.ContentRejectedException
import com.smartcity.greenpassport.feature.community.domain.CreateGroupUseCase
import com.smartcity.greenpassport.feature.community.domain.JoinGroupByCodeUseCase
import com.smartcity.greenpassport.feature.community.domain.JoinGroupUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveCommunitySessionUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveGroupsUseCase
import com.smartcity.greenpassport.feature.community.presentation.state.GroupsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    observeGroups: ObserveGroupsUseCase,
    private val createGroup: CreateGroupUseCase,
    private val joinGroup: JoinGroupUseCase,
    private val joinGroupByCode: JoinGroupByCodeUseCase,
    observeSession: ObserveCommunitySessionUseCase,
) : ViewModel() {

    private val form = MutableStateFlow(GroupsUiState())

    val uiState = combine(
        form,
        observeSession(),
        observeGroups().onStart { emit(emptyList()) }.catch { emit(emptyList()) },
    ) { formState, session, groups ->
        formState.copy(groups = groups, currentUserId = session?.userId, isLoading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), GroupsUiState())

    fun onDraftNameChanged(name: String) {
        form.update { it.copy(draftName = name, isNameRejected = false) }
    }

    fun onCreateGroup() {
        val creatorId = uiState.value.currentUserId ?: return
        val name = form.value.draftName.trim()
        if (name.isEmpty() || form.value.isCreating) return
        viewModelScope.launch {
            form.update { it.copy(isCreating = true) }
            runCatching { createGroup(name, creatorId) }
                .onSuccess { form.update { it.copy(isCreating = false, draftName = "") } }
                .onFailure { error ->
                    Log.w(TAG, "Failed to create group", error)
                    form.update { it.copy(isCreating = false, isNameRejected = error is ContentRejectedException) }
                }
        }
    }

    fun onJoinGroup(group: CommunityGroup) {
        val userId = uiState.value.currentUserId ?: return
        if (group.memberIds.contains(userId) || form.value.joiningGroupId != null) return
        viewModelScope.launch {
            form.update { it.copy(joiningGroupId = group.id) }
            runCatching { joinGroup(group.id, userId) }
                .onFailure { error -> Log.w(TAG, "Failed to join group", error) }
            form.update { it.copy(joiningGroupId = null) }
        }
    }

    fun onCodeDialogVisibilityChanged(isVisible: Boolean) {
        form.update { it.copy(isCodeDialogVisible = isVisible, inviteCodeDraft = "", isInviteCodeNotFound = false) }
    }

    fun onInviteCodeChanged(code: String) {
        form.update { it.copy(inviteCodeDraft = code, isInviteCodeNotFound = false) }
    }

    fun onJoinByCode() {
        val userId = uiState.value.currentUserId ?: return
        if (form.value.isJoiningByCode) return
        viewModelScope.launch {
            form.update { it.copy(isJoiningByCode = true) }
            runCatching { joinGroupByCode(form.value.inviteCodeDraft, userId) }
                .onSuccess { group ->
                    form.update {
                        it.copy(
                            isJoiningByCode = false,
                            isCodeDialogVisible = false,
                            inviteCodeDraft = "",
                            openedGroupId = group.id
                        )
                    }
                }
                .onFailure { error ->
                    Log.w(TAG, "Failed to join by code", error)
                    form.update { it.copy(isJoiningByCode = false, isInviteCodeNotFound = true) }
                }
        }
    }

    fun onOpenedGroupShown() {
        form.update { it.copy(openedGroupId = null) }
    }

    companion object {
        private const val TAG = "GroupsViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
