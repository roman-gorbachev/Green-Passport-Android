package com.smartcity.greenpassport.feature.community.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.feature.community.domain.CreateGroupUseCase
import com.smartcity.greenpassport.feature.community.domain.JoinGroupUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveCommunitySessionUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveGroupsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GroupsViewModel @Inject constructor(
    observeGroups: ObserveGroupsUseCase,
    private val createGroup: CreateGroupUseCase,
    private val joinGroup: JoinGroupUseCase,
    observeSession: ObserveCommunitySessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState: StateFlow<GroupsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeSession().collectLatest { session ->
                _uiState.update { it.copy(currentUserId = session?.userId) }
            }
        }
        viewModelScope.launch {
            observeGroups().collectLatest { groups ->
                _uiState.update { it.copy(groups = groups, isLoading = false) }
            }
        }
    }

    fun onDraftNameChanged(name: String) {
        _uiState.update { it.copy(draftName = name) }
    }

    fun onCreateGroup() {
        val creatorId = _uiState.value.currentUserId ?: return
        val name = _uiState.value.draftName.trim()
        if (name.isEmpty() || _uiState.value.isCreating) return

        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true) }
            try {
                createGroup(name, creatorId)
                _uiState.update { it.copy(isCreating = false, draftName = "") }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(isCreating = false) }
            }
        }
    }

    fun onJoinGroup(group: CommunityGroup) {
        val userId = _uiState.value.currentUserId ?: return
        if (group.memberIds.contains(userId)) return
        if (_uiState.value.joiningGroupId != null) return

        viewModelScope.launch {
            _uiState.update { it.copy(joiningGroupId = group.id) }
            try {
                joinGroup(group.id, userId)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Unit
            } finally {
                _uiState.update { it.copy(joiningGroupId = null) }
            }
        }
    }
}
