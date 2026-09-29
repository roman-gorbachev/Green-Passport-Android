package com.smartcity.greenpassport.feature.moderation.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.model.moderation.ModerationAction
import com.smartcity.greenpassport.feature.moderation.domain.ModeratePostUseCase
import com.smartcity.greenpassport.feature.moderation.domain.ObserveFlaggedPostsUseCase
import com.smartcity.greenpassport.feature.moderation.domain.ObserveIsModeratorUseCase
import com.smartcity.greenpassport.feature.moderation.domain.ObserveModerationQueueUseCase
import com.smartcity.greenpassport.feature.moderation.domain.ObserveModerationSessionUseCase
import com.smartcity.greenpassport.feature.moderation.domain.ReviewSubmissionUseCase
import com.smartcity.greenpassport.feature.moderation.presentation.state.ModerationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ModerationViewModel @Inject constructor(
    observeSession: ObserveModerationSessionUseCase,
    private val observeIsModerator: ObserveIsModeratorUseCase,
    private val observeModerationQueue: ObserveModerationQueueUseCase,
    private val observeFlaggedPosts: ObserveFlaggedPostsUseCase,
    private val reviewSubmission: ReviewSubmissionUseCase,
    private val moderatePost: ModeratePostUseCase,
) : ViewModel() {

    private val processingIds = MutableStateFlow(emptySet<String>())
    private val hasActionError = MutableStateFlow(false)

    val uiState = observeModerationUiState(observeSession()).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        ModerationUiState(),
    )

    fun onApprove(submissionId: String) {
        runAction(submissionId) { reviewSubmission(submissionId, approve = true, reason = null) }
    }

    fun onReject(submissionId: String, reason: String) {
        runAction(submissionId) { reviewSubmission(submissionId, approve = false, reason = reason) }
    }

    fun onModeratePost(postId: String, action: ModerationAction) {
        runAction(postId) { moderatePost(postId, action) }
    }

    private fun runAction(itemId: String, action: suspend () -> Unit) {
        if (itemId in processingIds.value) return
        viewModelScope.launch {
            processingIds.update { it + itemId }
            hasActionError.value = false
            runCatching { action() }
                .onFailure { error ->
                    Log.w(TAG, "Moderation action failed", error)
                    hasActionError.value = true
                }
            processingIds.update { it - itemId }
        }
    }

    private fun observeModerationUiState(sessions: Flow<AuthSession?>): Flow<ModerationUiState> =
        sessions.flatMapLatest { session ->
            if (session == null) {
                flowOf(ModerationUiState(isLoading = false))
            } else {
                observeIsModerator(session.userId).flatMapLatest { isModerator ->
                    if (isModerator) observeQueues() else flowOf(ModerationUiState(isLoading = false))
                }
            }
        }

    private fun observeQueues(): Flow<ModerationUiState> =
        combine(
            observeModerationQueue().catch { emit(emptyList()) },
            observeFlaggedPosts(),
            processingIds,
            hasActionError,
        ) { submissions, flaggedPosts, processing, actionError ->
            ModerationUiState(
                isLoading = false,
                isModerator = true,
                submissions = submissions,
                flaggedPosts = flaggedPosts,
                processingIds = processing,
                hasActionError = actionError,
            )
        }

    companion object {
        private const val TAG = "ModerationViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
