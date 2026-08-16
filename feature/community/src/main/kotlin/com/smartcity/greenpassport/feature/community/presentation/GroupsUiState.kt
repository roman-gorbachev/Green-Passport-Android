package com.smartcity.greenpassport.feature.community.presentation

import com.smartcity.greenpassport.core.model.CommunityGroup

data class GroupsUiState(
    val groups: List<CommunityGroup> = emptyList(),
    val draftName: String = "",
    val isLoading: Boolean = true,
    val isCreating: Boolean = false,
    val joiningGroupId: String? = null,
    val currentUserId: String? = null,
)
