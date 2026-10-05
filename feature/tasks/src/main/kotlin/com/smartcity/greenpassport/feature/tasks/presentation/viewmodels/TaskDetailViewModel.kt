package com.smartcity.greenpassport.feature.tasks.presentation.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.rewards.RewardFailure
import com.smartcity.greenpassport.core.model.rewards.RewardFailureException
import com.smartcity.greenpassport.core.model.rewards.RewardResult
import com.smartcity.greenpassport.core.model.verification.SubmissionStatus
import com.smartcity.greenpassport.feature.tasks.domain.CompleteTaskUseCase
import com.smartcity.greenpassport.feature.tasks.domain.GetTaskProgressUseCase
import com.smartcity.greenpassport.feature.tasks.domain.ObserveFavoriteTaskIdsUseCase
import com.smartcity.greenpassport.feature.tasks.domain.ObserveTaskSubmissionsUseCase
import com.smartcity.greenpassport.feature.tasks.domain.ObserveTasksSessionUseCase
import com.smartcity.greenpassport.feature.tasks.domain.ScanTaskCodeUseCase
import com.smartcity.greenpassport.feature.tasks.domain.SubmitTaskPhotoUseCase
import com.smartcity.greenpassport.feature.tasks.domain.ToggleTaskFavoriteUseCase
import com.smartcity.greenpassport.feature.tasks.presentation.state.TaskDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTaskProgress: GetTaskProgressUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val scanTaskCode: ScanTaskCodeUseCase,
    private val submitTaskPhoto: SubmitTaskPhotoUseCase,
    private val observeTaskSubmissions: ObserveTaskSubmissionsUseCase,
    private val observeFavoriteTaskIds: ObserveFavoriteTaskIdsUseCase,
    private val toggleTaskFavorite: ToggleTaskFavoriteUseCase,
    observeSession: ObserveTasksSessionUseCase,
) : ViewModel() {

    private val taskId: String = checkNotNull(savedStateHandle["taskId"])
    private var currentUserId: String? = null

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeSession().collectLatest { session ->
                currentUserId = session?.userId
                loadTask()
                if (session != null) {
                    observeTaskSubmissions(session.userId)
                        .catch { error -> Log.w(TAG, "Failed to observe submissions", error) }
                        .collectLatest { submissions ->
                            val submission = submissions.firstOrNull { it.taskId == taskId }
                            _uiState.update {
                                it.copy(
                                    submission = submission,
                                    isCompleted = it.isCompleted || submission?.status == SubmissionStatus.APPROVED,
                                )
                            }
                        }
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            observeSession().collectLatest { session ->
                if (session == null) {
                    _uiState.update { it.copy(isFavorite = false) }
                    return@collectLatest
                }
                observeFavoriteTaskIds(session.userId)
                    .catch { error -> Log.w(TAG, "Failed to observe favorites", error) }
                    .collect { favoriteIds -> _uiState.update { it.copy(isFavorite = taskId in favoriteIds) } }
            }
        }
    }

    fun onToggleFavorite() {
        val userId = currentUserId ?: return
        val isFavorite = !_uiState.value.isFavorite
        _uiState.update { it.copy(isFavorite = isFavorite) }
        viewModelScope.launch {
            runCatching { toggleTaskFavorite(userId, taskId, isFavorite) }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    Log.w(TAG, "Failed to toggle favorite", error)
                    _uiState.update { it.copy(isFavorite = !isFavorite) }
                }
        }
    }

    fun retry() {
        viewModelScope.launch { loadTask() }
    }

    fun onCompleteTask() {
        val task = _uiState.value.task ?: return
        runRewardAction { completeTaskUseCase(task) }
    }

    fun onScanCode(activityContext: Context) {
        runRewardAction { scanTaskCode(activityContext) }
    }

    fun onPhotoPicked(photo: Uri) {
        val userId = currentUserId ?: return
        if (_uiState.value.isSubmitting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, failure = null) }
            runCatching { submitTaskPhoto(userId, taskId, photo) }
                .onSuccess { submission -> _uiState.update { it.copy(isSubmitting = false, submission = submission) } }
                .onFailure { error -> handleFailure(error) }
        }
    }

    private fun runRewardAction(action: suspend () -> RewardResult?) {
        val state = _uiState.value
        if (state.isCompleted || state.isSubmitting || currentUserId == null) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, failure = null) }
            runCatching { action() }
                .onSuccess { reward ->
                    _uiState.update {
                        if (reward == null) {
                            it.copy(isSubmitting = false)
                        } else {
                            it.copy(
                                isSubmitting = false,
                                isCompleted = true,
                                earnedPoints = reward.points,
                                streakBonus = reward.streakBonus,
                            )
                        }
                    }
                }
                .onFailure { error -> handleFailure(error) }
        }
    }

    private suspend fun handleFailure(error: Throwable) {
        if (error is CancellationException) {
            currentCoroutineContext().ensureActive()
            _uiState.update { it.copy(isSubmitting = false) }
            return
        }
        Log.w(TAG, "Task confirmation failed", error)
        val failure = (error as? RewardFailureException)?.failure ?: RewardFailure.UNKNOWN
        _uiState.update {
            it.copy(
                isSubmitting = false,
                failure = failure,
                isCompleted = it.isCompleted || failure == RewardFailure.ALREADY_COMPLETED,
            )
        }
    }

    private suspend fun loadTask() {
        _uiState.update { it.copy(isLoading = true, hasError = false) }
        runCatching {
            val progress = getTaskProgress(taskId, currentUserId)
            _uiState.update { it.copy(task = progress.task, isCompleted = progress.isCompleted, isLoading = false) }
        }.onFailure { error ->
            if (error is CancellationException) throw error
            _uiState.update { it.copy(isLoading = false, hasError = true) }
        }
    }

    companion object {
        private const val TAG = "TaskDetailViewModel"
    }
}
