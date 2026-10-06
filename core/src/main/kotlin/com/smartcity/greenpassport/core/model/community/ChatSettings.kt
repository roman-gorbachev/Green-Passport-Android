package com.smartcity.greenpassport.core.model.community

data class ChatSettings(
    val chatId: ChatId,
    val isPinned: Boolean,
    val isArchived: Boolean,
    val isMuted: Boolean,
) {
    companion object {
        fun defaults(chatId: ChatId) = ChatSettings(
            chatId = chatId,
            isPinned = false,
            isArchived = false,
            isMuted = chatId == ChatId.Forum,
        )
    }
}
