package com.smartcity.greenpassport.core.model.community

import com.smartcity.greenpassport.core.model.CommunityGroup

data class ChatSummary(
    val chatId: ChatId,
    val groupName: String?,
    val lastMessageAtEpochMillis: Long?,
    val settings: ChatSettings,
) {
    companion object {
        fun list(
            groups: List<CommunityGroup>,
            forumLastMessageAtEpochMillis: Long?,
            settings: Map<ChatId, ChatSettings>,
        ): List<ChatSummary> {
            val forum = ChatSummary(
                chatId = ChatId.Forum,
                groupName = null,
                lastMessageAtEpochMillis = forumLastMessageAtEpochMillis,
                settings = settings[ChatId.Forum] ?: ChatSettings.defaults(ChatId.Forum),
            )
            val groupChats = groups.map { group ->
                val chatId = ChatId.Group(group.id)
                ChatSummary(
                    chatId = chatId,
                    groupName = group.name,
                    lastMessageAtEpochMillis = group.lastMessageAtEpochMillis,
                    settings = settings[chatId] ?: ChatSettings.defaults(chatId),
                )
            }
            return (listOf(forum) + groupChats).sortedWith(
                compareByDescending<ChatSummary> { it.settings.isPinned }
                    .thenByDescending { it.lastMessageAtEpochMillis ?: Long.MIN_VALUE },
            )
        }
    }
}
