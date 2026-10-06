package com.smartcity.greenpassport.feature.community.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.core.model.moderation.ReportReason
import com.smartcity.greenpassport.core.moderation.ContentRejectedException
import com.smartcity.greenpassport.feature.community.domain.ChangeMessageUseCase
import com.smartcity.greenpassport.feature.community.domain.MessageChange
import com.smartcity.greenpassport.feature.community.domain.ObserveCommunitySessionUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveForumPostsUseCase
import com.smartcity.greenpassport.feature.community.domain.PostToForumUseCase
import com.smartcity.greenpassport.feature.community.domain.ReportPostUseCase
import com.smartcity.greenpassport.feature.community.presentation.state.ComposerMode
import com.smartcity.greenpassport.feature.community.presentation.state.ForumUiState
import com.smartcity.greenpassport.feature.community.presentation.state.MessageAction
import com.smartcity.greenpassport.feature.community.presentation.state.MessageTarget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    observeForumPosts: ObserveForumPostsUseCase,
    private val postToForum: PostToForumUseCase,
    private val reportPost: ReportPostUseCase,
    private val changeMessage: ChangeMessageUseCase,
    observeSession: ObserveCommunitySessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForumUiState())
    val uiState: StateFlow<ForumUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            observeSession().collectLatest { session ->
                currentUserId = session?.userId
                _uiState.update { it.copy(currentUserId = session?.userId) }
            }
        }
        viewModelScope.launch {
            observeForumPosts().collectLatest { posts ->
                _uiState.update { it.copy(posts = posts, isLoading = false) }
            }
        }
    }

    fun onDraftChanged(text: String) {
        _uiState.update { it.copy(draft = text, isTextRejected = false, isSendFailed = false) }
    }

    fun onPost() {
        val authorId = currentUserId ?: return
        val text = _uiState.value.draft.trim()
        if (text.isEmpty() || _uiState.value.isPosting) return
        val mode = _uiState.value.composerMode

        viewModelScope.launch {
            _uiState.update { it.copy(isPosting = true, isSendFailed = false) }
            runCatching {
                when (mode) {
                    ComposerMode.New -> postToForum(authorId, text)
                    is ComposerMode.Reply -> postToForum(authorId, text, replyTo = mode.quote)
                    is ComposerMode.Edit -> changeMessage(ChatId.Forum, mode.messageId, MessageChange.Edit(text))
                }
                _uiState.update { it.copy(isPosting = false, draft = "", composerMode = ComposerMode.New) }
            }.onFailure { error ->
                val isRejected = error is ContentRejectedException
                _uiState.update { it.copy(isPosting = false, isTextRejected = isRejected, isSendFailed = !isRejected) }
            }
        }
    }

    fun onMessageAction(action: MessageAction, target: MessageTarget) {
        when (action) {
            MessageAction.Reply -> _uiState.update { it.copy(composerMode = ComposerMode.Reply(target.quote)) }
            MessageAction.Edit -> _uiState.update {
                it.copy(composerMode = ComposerMode.Edit(target.id), draft = target.text, isTextRejected = false)
            }
            MessageAction.Delete -> _uiState.update { it.copy(pendingDeletion = target) }
            is MessageAction.Report -> onReport(target.id, action.reason)
            MessageAction.Copy, MessageAction.Forward -> Unit
        }
    }

    fun onCancelComposerMode() {
        _uiState.update { state ->
            val draft = if (state.composerMode is ComposerMode.Edit) "" else state.draft
            state.copy(composerMode = ComposerMode.New, draft = draft)
        }
    }

    fun onDeletionDismissed() {
        _uiState.update { it.copy(pendingDeletion = null) }
    }

    fun onDeletionConfirmed() {
        val target = _uiState.value.pendingDeletion ?: return
        _uiState.update { state ->
            val isEditingTarget = (state.composerMode as? ComposerMode.Edit)?.messageId == target.id
            state.copy(
                pendingDeletion = null,
                composerMode = if (isEditingTarget) ComposerMode.New else state.composerMode,
                draft = if (isEditingTarget) "" else state.draft,
            )
        }
        viewModelScope.launch {
            runCatching { changeMessage(ChatId.Forum, target.id, MessageChange.Delete) }
                .onFailure { error -> Log.w(TAG, "Failed to delete post", error) }
        }
    }

    fun onReport(postId: String, reason: ReportReason) {
        val reporterId = currentUserId ?: return
        if (postId in _uiState.value.reportedPostIds) return
        viewModelScope.launch {
            runCatching { reportPost(postId, reporterId, reason) }
                .onSuccess { _uiState.update { it.copy(reportedPostIds = it.reportedPostIds + postId) } }
                .onFailure { error -> Log.w(TAG, "Failed to report post", error) }
        }
    }

    companion object {
        private const val TAG = "ForumViewModel"
    }
}
