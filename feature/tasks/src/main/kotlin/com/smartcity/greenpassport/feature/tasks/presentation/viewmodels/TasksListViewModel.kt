package com.smartcity.greenpassport.feature.tasks.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.profile.UserProfile
import com.smartcity.greenpassport.core.model.verification.SubmissionStatus
import com.smartcity.greenpassport.feature.tasks.domain.ObserveCompletedTaskIdsUseCase
import com.smartcity.greenpassport.feature.tasks.domain.ObserveFavoriteTaskIdsUseCase
import com.smartcity.greenpassport.feature.tasks.domain.ObserveTaskSubmissionsUseCase
import com.smartcity.greenpassport.feature.tasks.domain.ObserveTasksSessionUseCase
import com.smartcity.greenpassport.feature.tasks.domain.ObserveTasksUseCase
import com.smartcity.greenpassport.feature.tasks.domain.ObserveUserProfileUseCase
import com.smartcity.greenpassport.feature.tasks.domain.ToggleTaskFavoriteUseCase
import com.smartcity.greenpassport.feature.tasks.presentation.state.TaskFilters
import com.smartcity.greenpassport.feature.tasks.presentation.state.TasksListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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
class TasksListViewModel @Inject constructor(
    private val observeTasks: ObserveTasksUseCase,
    private val observeCompletedTaskIds: ObserveCompletedTaskIdsUseCase,
    private val observeFavoriteTaskIds: ObserveFavoriteTaskIdsUseCase,
    private val toggleTaskFavorite: ToggleTaskFavoriteUseCase,
    private val observeUserProfile: ObserveUserProfileUseCase,
    private val observeTaskSubmissions: ObserveTaskSubmissionsUseCase,
    observeSession: ObserveTasksSessionUseCase,
) : ViewModel() {

    private val filters = MutableStateFlow(FilterState())

    private val retryRequests = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private var currentUserId: String? = null

    val uiState = observeTasksListUiState(observeSession()).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        TasksListUiState(),
    )

    fun retry() {
        retryRequests.tryEmit(Unit)
    }

    fun onFiltersChanged(newFilters: TaskFilters) {
        filters.update { it.copy(filters = newFilters, isSheetVisible = false) }
    }

    fun onFilterSheetVisibilityChanged(isVisible: Boolean) {
        filters.update { it.copy(isSheetVisible = isVisible) }
    }

    fun onToggleFavorite(taskId: String) {
        val userId = currentUserId ?: return
        val isFavorite = uiState.value.favoriteTaskIds.contains(taskId)
        viewModelScope.launch {
            runCatching { toggleTaskFavorite(userId, taskId, !isFavorite) }
                .onFailure { error -> Log.w(TAG, "Failed to toggle favorite", error) }
        }
    }

    private fun observeTasksListUiState(sessions: Flow<AuthSession?>): Flow<TasksListUiState> {
        val data = combine(sessions, retryRequests.onStart { emit(Unit) }) { session, _ -> session }
            .flatMapLatest { session ->
                currentUserId = session?.userId
                observeTasksData(session?.userId)
            }
        return combine(data, filters) { tasksData, filterState ->
            TasksListUiState(
                tasks = tasksData.tasks,
                completedTaskIds = tasksData.completedIds,
                favoriteTaskIds = tasksData.favoriteIds,
                pendingTaskIds = tasksData.pendingIds,
                profile = tasksData.profile,
                filters = filterState.filters,
                isFilterSheetVisible = filterState.isSheetVisible,
                isLoading = tasksData.isLoading,
                hasError = tasksData.hasError,
            )
        }
    }

    private fun observeTasksData(userId: String?): Flow<TasksData> {
        val completedIds = userId?.let { observeCompletedTaskIds(it).catch { emit(emptySet()) } } ?: flowOf(emptySet())
        val favoriteIds = userId?.let { observeFavoriteTaskIds(it).catch { emit(emptySet()) } } ?: flowOf(emptySet())
        val pendingIds = userId?.let { id ->
            observeTaskSubmissions(id)
                .map { submissions ->
                    submissions.filter { it.status == SubmissionStatus.PENDING }.map { it.taskId }.toSet()
                }
                .catch { emit(emptySet()) }
        } ?: flowOf(emptySet())
        val profile = userId?.let { observeUserProfile(it).onStart { emit(null) }.catch { emit(null) } } ?: flowOf(null)
        val tasks = observeTasks()
            .map { TasksLoad(tasks = it) }
            .catch { error ->
                Log.w(TAG, "Failed to observe tasks", error)
                emit(TasksLoad(hasError = true))
            }
        return combine(
            tasks,
            completedIds,
            favoriteIds,
            pendingIds,
            profile
        ) { load, completed, favorites, pending, currentProfile ->
            TasksData(
                tasks = load.tasks,
                completedIds = completed,
                favoriteIds = favorites,
                pendingIds = pending,
                profile = currentProfile,
                isLoading = false,
                hasError = load.hasError,
            )
        }.onStart { emit(TasksData()) }
    }

    private data class FilterState(
        val filters: TaskFilters = TaskFilters(),
        val isSheetVisible: Boolean = false,
    )

    private data class TasksLoad(
        val tasks: List<Task> = emptyList(),
        val hasError: Boolean = false,
    )

    private data class TasksData(
        val tasks: List<Task> = emptyList(),
        val completedIds: Set<String> = emptySet(),
        val favoriteIds: Set<String> = emptySet(),
        val pendingIds: Set<String> = emptySet(),
        val profile: UserProfile? = null,
        val isLoading: Boolean = true,
        val hasError: Boolean = false,
    )

    companion object {
        private const val TAG = "TasksListViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
