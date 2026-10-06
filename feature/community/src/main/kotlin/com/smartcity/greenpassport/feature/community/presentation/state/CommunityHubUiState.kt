package com.smartcity.greenpassport.feature.community.presentation.state

import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.core.model.community.ChatSettings
import com.smartcity.greenpassport.core.model.community.ChatSummary
import com.smartcity.greenpassport.core.model.community.GroupSearchResult

data class CommunityHubUiState(
    val forum: ChatSummary = ChatSummary(ChatId.Forum, null, null, ChatSettings.defaults(ChatId.Forum)),
    val groups: List<ChatSummary> = emptyList(),
    val archivedCount: Int = 0,
    val query: String = "",
    val myGroupMatches: List<GroupSearchResult> = emptyList(),
    val otherGroupMatches: List<GroupSearchResult> = emptyList(),
    val currentUserId: String? = null,
    val isLoading: Boolean = true,
    val isCreateDialogVisible: Boolean = false,
    val groupDraftName: String = "",
    val isCreatingGroup: Boolean = false,
    val isGroupNameRejected: Boolean = false,
    val isCodeDialogVisible: Boolean = false,
    val inviteCodeDraft: String = "",
    val isJoiningByCode: Boolean = false,
    val isInviteCodeNotFound: Boolean = false,
    val openedGroupId: String? = null,
) {
    val isSearching: Boolean
        get() = query.isNotBlank()
}
