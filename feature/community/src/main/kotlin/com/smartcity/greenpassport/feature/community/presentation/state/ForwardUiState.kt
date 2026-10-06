package com.smartcity.greenpassport.feature.community.presentation.state

import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.core.model.community.ChatId

data class ForwardUiState(
    val groups: List<CommunityGroup> = emptyList(),
    val sendingChatId: ChatId? = null,
    val isSendFailed: Boolean = false,
    val isTextRejected: Boolean = false,
    val isSent: Boolean = false,
)
