package com.smartcity.greenpassport.feature.tasks.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.TaskCategory
import com.smartcity.greenpassport.feature.tasks.domain.GetCompletedTaskIdsUseCase
import com.smartcity.greenpassport.feature.tasks.domain.GetTasksUseCase
import com.smartcity.greenpassport.feature.tasks.domain.ObserveFavoriteTaskIdsUseCase
import com.smartcity.greenpassport.feature.tasks.domain.ObserveTasksSessionUseCase
import com.smartcity.greenpassport.feature.tasks.domain.ToggleTaskFavoriteUseCase
import com.smartcity.greenpassport.feature.tasks.presentation.state.TasksListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksListViewModel @Inject constructor(
    private val getTasks: GetTasksUseCase,
    private val getCompletedTaskIds: GetCompletedTaskIdsUseCase,
    private val observeFavoriteTaskIds: ObserveFavoriteTaskIdsUseCase,
    private val toggleTaskFavorite: ToggleTaskFavoriteUseCase,
    observeSession: ObserveTasksSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksListUiState())
    val uiState: StateFlow<TasksListUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            observeSession().collectLatest { session ->
                currentUserId = session?.userId
                refresh()
                if (session != null) {
                    observeFavoriteTaskIds(session.userId).collectLatest { favoriteIds ->
                        _uiState.update { it.copy(favoriteTaskIds = favoriteIds) }
                    }
                }
            }
        }
    }

    fun onCategorySelected(category: TaskCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onToggleFavorite(taskId: String) {
        val userId = currentUserId ?: return
        val isFavorite = _uiState.value.favoriteTaskIds.contains(taskId)
        viewModelScope.launch {
            runCatching {
                toggleTaskFavorite(userId, taskId, !isFavorite)
            }.onFailure { error ->
                Log.e("TasksListViewModel", error.message.orEmpty())
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            runCatching {
                val tasks = getTasks()
                val completedIds = currentUserId?.let { getCompletedTaskIds(it) } ?: emptySet()
                _uiState.update {
                    it.copy(tasks = tasks, completedTaskIds = completedIds, isLoading = false)
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }
}
