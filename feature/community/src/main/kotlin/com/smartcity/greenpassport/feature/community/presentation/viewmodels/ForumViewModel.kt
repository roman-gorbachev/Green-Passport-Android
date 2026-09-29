package com.smartcity.greenpassport.feature.community.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.moderation.ReportReason
import com.smartcity.greenpassport.core.moderation.ContentRejectedException
import com.smartcity.greenpassport.feature.community.domain.ObserveCommunitySessionUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveForumPostsUseCase
import com.smartcity.greenpassport.feature.community.domain.PostToForumUseCase
import com.smartcity.greenpassport.feature.community.domain.ReportPostUseCase
import com.smartcity.greenpassport.feature.community.presentation.state.ForumUiState
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
        _uiState.update { it.copy(draft = text, isTextRejected = false) }
    }

    fun onPost() {
        val authorId = currentUserId ?: return
        val text = _uiState.value.draft.trim()
        if (text.isEmpty() || _uiState.value.isPosting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isPosting = true) }
            runCatching {
                postToForum(authorId, text)
                _uiState.update { it.copy(isPosting = false, draft = "") }
            }.onFailure { error ->
                _uiState.update { it.copy(isPosting = false, isTextRejected = error is ContentRejectedException) }
            }
        }
    }

    fun onReport(postId: String, reason: ReportReason) {
        val reporterId = currentUserId ?: return
        if (postId in _uiState.value.reportedPostIds) return
        viewModelScope.launch {
            runCatching { reportPost(postId, reporterId, reason) }
                .onSuccess { _uiState.update { it.copy(reportedPostIds = it.reportedPostIds + postId) } }
                .onFailure { error -> Log.w("ForumViewModel", "Failed to report post", error) }
        }
    }
}
