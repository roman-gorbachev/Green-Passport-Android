package com.smartcity.greenpassport.feature.moderation.presentation.state

import com.smartcity.greenpassport.core.model.ForumPost
import com.smartcity.greenpassport.feature.moderation.domain.SubmissionItem

data class ModerationUiState(
    val isLoading: Boolean = true,
    val isModerator: Boolean = false,
    val submissions: List<SubmissionItem> = emptyList(),
    val flaggedPosts: List<ForumPost> = emptyList(),
    val processingIds: Set<String> = emptySet(),
    val hasActionError: Boolean = false,
)
