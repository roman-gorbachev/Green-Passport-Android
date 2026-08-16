package com.smartcity.greenpassport.feature.tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.TaskCategory
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
class TasksListViewModel @Inject constructor(
    private val getTasks: GetTasksUseCase,
    private val getCompletedTaskIds: GetCompletedTaskIdsUseCase,
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
            }
        }
    }

    fun onCategorySelected(category: TaskCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val tasks = getTasks()
            val completedIds = currentUserId?.let { getCompletedTaskIds(it) } ?: emptySet()
            _uiState.update {
                it.copy(tasks = tasks, completedTaskIds = completedIds, isLoading = false)
            }
        }
    }
}
