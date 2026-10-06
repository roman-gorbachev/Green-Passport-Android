package com.smartcity.greenpassport.core.model.community

import com.smartcity.greenpassport.core.model.CommunityGroup

data class ChatSummary(
    val chatId: ChatId,
    val groupName: String?,
    val lastMessageAtEpochMillis: Long?,
    val settings: ChatSettings,
) {
    companion object {
        fun forum(lastMessageAtEpochMillis: Long?, settings: Map<ChatId, ChatSettings>) = ChatSummary(
            chatId = ChatId.Forum,
            groupName = null,
            lastMessageAtEpochMillis = lastMessageAtEpochMillis,
            settings = settings[ChatId.Forum] ?: ChatSettings.defaults(ChatId.Forum),
        )

        fun groups(groups: List<CommunityGroup>, settings: Map<ChatId, ChatSettings>): List<ChatSummary> =
            groups.map { group ->
                val chatId = ChatId.Group(group.id)
                ChatSummary(
                    chatId = chatId,
                    groupName = group.name,
                    lastMessageAtEpochMillis = group.lastMessageAtEpochMillis,
                    settings = settings[chatId] ?: ChatSettings.defaults(chatId),
                )
            }.sortedWith(
                compareByDescending<ChatSummary> { it.settings.isPinned }
                    .thenByDescending { it.lastMessageAtEpochMillis ?: Long.MIN_VALUE },
            )
    }
}
