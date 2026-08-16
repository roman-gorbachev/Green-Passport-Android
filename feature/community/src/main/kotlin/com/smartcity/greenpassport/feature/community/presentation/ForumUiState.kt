package com.smartcity.greenpassport.feature.community.presentation

import com.smartcity.greenpassport.core.model.ForumPost

data class ForumUiState(
    val posts: List<ForumPost> = emptyList(),
    val draft: String = "",
    val isLoading: Boolean = true,
    val isPosting: Boolean = false,
)
