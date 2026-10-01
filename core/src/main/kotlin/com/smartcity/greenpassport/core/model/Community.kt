package com.smartcity.greenpassport.core.model

import com.smartcity.greenpassport.core.model.profile.AvatarStyle
import kotlinx.coroutines.flow.Flow

data class ForumPost(
    val id: String,
    val authorId: String,
    val authorName: String?,
    val authorAvatar: AvatarStyle?,
    val text: String,
    val createdAtEpochMillis: Long,
    val isHidden: Boolean = false,
    val reportCount: Int = 0,
)

data class CommunityGroup(
    val id: String,
    val name: String,
    val memberIds: List<String>,
    val ownerId: String? = null,
    val inviteCode: String? = null,
)

interface CommunityRepository {
    fun observeForumPosts(): Flow<List<ForumPost>>
    suspend fun postToForum(authorId: String, authorName: String?, authorAvatar: AvatarStyle?, text: String)

    fun observeGroups(): Flow<List<CommunityGroup>>
    suspend fun createGroup(name: String, creatorId: String)
    suspend fun joinGroup(groupId: String, userId: String)

    fun observeGroup(groupId: String): Flow<CommunityGroup?>
    suspend fun leaveGroup(groupId: String, userId: String)
    suspend fun findGroup(inviteCode: String): CommunityGroup?
    fun observeMessages(groupId: String): Flow<List<GroupMessage>>
    suspend fun sendMessage(
        groupId: String,
        senderId: String,
        senderName: String?,
        senderAvatar: AvatarStyle?,
        text: String,
    )
    suspend fun fetchMembers(ids: List<String>): List<GroupMember>
}
