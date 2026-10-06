package com.smartcity.greenpassport.core.model.community

interface MessageEditingRepository {
    suspend fun editMessage(chat: ChatId, messageId: String, text: String)
    suspend fun deleteMessage(chat: ChatId, messageId: String)
}
