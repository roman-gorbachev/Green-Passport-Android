package com.smartcity.greenpassport.feature.tasks.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.feature.tasks.domain.CompleteTaskUseCase
import com.smartcity.greenpassport.feature.tasks.domain.GetCompletedTaskIdsUseCase
import com.smartcity.greenpassport.feature.tasks.domain.GetTasksUseCase
import com.smartcity.greenpassport.feature.tasks.domain.ObserveTasksSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTasks: GetTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val getCompletedTaskIds: GetCompletedTaskIdsUseCase,
    observeSession: ObserveTasksSessionUseCase,
) : ViewModel() {

    private val taskId: String = checkNotNull(savedStateHandle["taskId"])
    private var currentUserId: String? = null

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeSession().collectLatest { session ->
                currentUserId = session?.userId
                loadTask()
            }
        }
    }

    private suspend fun loadTask() {
        _uiState.update { it.copy(isLoading = true) }
        val task = getTasks().firstOrNull { it.id == taskId }
        val completed = currentUserId?.let { getCompletedTaskIds(it) }?.contains(taskId) ?: false
        _uiState.update { it.copy(task = task, isCompleted = completed, isLoading = false) }
    }

    fun onCompleteTask() {
        val state = _uiState.value
        val task = state.task ?: return
        val userId = currentUserId ?: return
        if (state.isCompleted || state.isSubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            completeTaskUseCase(userId, task)
            _uiState.update { it.copy(isSubmitting = false, isCompleted = true) }
        }
    }
}
