package com.smartcity.greenpassport.core.model

import kotlinx.coroutines.flow.Flow

data class ForumPost(
    val id: String,
    val authorId: String,
    val text: String,
    val createdAtEpochMillis: Long,
)

data class CommunityGroup(
    val id: String,
    val name: String,
    val memberIds: List<String>,
)

data class ChatMessage(
    val id: String,
    val senderId: String,
    val text: String,
    val sentAtEpochMillis: Long,
)

interface CommunityRepository {
    fun observeForumPosts(): Flow<List<ForumPost>>
    suspend fun postToForum(authorId: String, text: String)

    fun observeGroups(): Flow<List<CommunityGroup>>
    suspend fun createGroup(name: String, creatorId: String)
    suspend fun joinGroup(groupId: String, userId: String)

    fun observeChatMessages(chatId: String): Flow<List<ChatMessage>>
    suspend fun sendChatMessage(chatId: String, senderId: String, text: String)
}
