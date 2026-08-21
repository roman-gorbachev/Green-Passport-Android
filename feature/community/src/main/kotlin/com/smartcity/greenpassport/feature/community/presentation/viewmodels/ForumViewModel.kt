package com.smartcity.greenpassport.feature.community.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.feature.community.domain.ObserveCommunitySessionUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveForumPostsUseCase
import com.smartcity.greenpassport.feature.community.domain.PostToForumUseCase
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
    observeSession: ObserveCommunitySessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForumUiState())
    val uiState: StateFlow<ForumUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            observeSession().collectLatest { session -> currentUserId = session?.userId }
        }
        viewModelScope.launch {
            observeForumPosts().collectLatest { posts ->
                _uiState.update { it.copy(posts = posts, isLoading = false) }
            }
        }
    }

    fun onDraftChanged(text: String) {
        _uiState.update { it.copy(draft = text) }
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
            }.onFailure {
                _uiState.update { it.copy(isPosting = false) }
            }
        }
    }
}
