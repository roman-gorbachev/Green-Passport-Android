package com.smartcity.greenpassport.feature.community.presentation.state

import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.core.model.GroupMember
import com.smartcity.greenpassport.core.model.GroupMessage

data class GroupDetailUiState(
    val group: CommunityGroup? = null,
    val messages: List<GroupMessage> = emptyList(),
    val members: List<GroupMember> = emptyList(),
    val draft: String = "",
    val currentUserId: String? = null,
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val isLoadingMessages: Boolean = true,
    val hasMessagesError: Boolean = false,
    val isLoadingMembers: Boolean = false,
    val isMembersVisible: Boolean = false,
    val isLeaveConfirmationVisible: Boolean = false,
    val isSending: Boolean = false,
    val isTextRejected: Boolean = false,
    val isSendFailed: Boolean = false,
    val isJoining: Boolean = false,
    val isLeaving: Boolean = false,
) {
    val isMember: Boolean
        get() {
            val userId = currentUserId ?: return false
            return group?.memberIds?.contains(userId) == true
        }
}
