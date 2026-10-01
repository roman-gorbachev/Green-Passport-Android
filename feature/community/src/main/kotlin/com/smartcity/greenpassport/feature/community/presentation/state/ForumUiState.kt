package com.smartcity.greenpassport.feature.community.presentation.state

import com.smartcity.greenpassport.core.model.ForumPost

data class ForumUiState(
    val posts: List<ForumPost> = emptyList(),
    val draft: String = "",
    val isLoading: Boolean = true,
    val isPosting: Boolean = false,
    val isTextRejected: Boolean = false,
    val isSendFailed: Boolean = false,
    val currentUserId: String? = null,
    val reportedPostIds: Set<String> = emptySet(),
)
