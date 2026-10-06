package com.smartcity.greenpassport.feature.community.presentation.state

import com.smartcity.greenpassport.core.model.community.ChatSummary

data class ChatListUiState(
    val archivedChats: List<ChatSummary> = emptyList(),
    val isLoading: Boolean = true,
)
