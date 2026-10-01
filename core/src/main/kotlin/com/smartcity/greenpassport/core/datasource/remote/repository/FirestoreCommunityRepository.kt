package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.datasource.remote.InviteCodeGenerator
import com.smartcity.greenpassport.core.datasource.remote.cacheFirstSnapshots
import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.core.model.CommunityRepository
import com.smartcity.greenpassport.core.model.ForumPost
import com.smartcity.greenpassport.core.model.GroupMember
import com.smartcity.greenpassport.core.model.GroupMessage
import com.smartcity.greenpassport.core.model.profile.AvatarStyle
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val FIELD_AUTHOR_ID = "authorId"
private const val FIELD_AUTHOR_NAME = "authorName"
private const val FIELD_AUTHOR_AVATAR = "authorAvatar"
private const val FIELD_TEXT = "text"
private const val FIELD_CREATED_AT = "createdAtEpochMillis"
private const val FIELD_HIDDEN = "hidden"
private const val FIELD_REPORT_COUNT = "reportCount"

private const val FIELD_NAME = "name"
private const val FIELD_MEMBER_IDS = "memberIds"
private const val FIELD_OWNER_ID = "ownerId"
private const val FIELD_INVITE_CODE = "inviteCode"

private const val FIELD_SENDER_ID = "senderId"
private const val FIELD_SENDER_NAME = "senderName"
private const val FIELD_SENDER_AVATAR = "senderAvatar"
private const val FIELD_FIRST_NAME = "firstName"
private const val FIELD_LAST_NAME = "lastName"
private const val FIELD_AVATAR = "avatar"
private const val MESSAGES_LIMIT = 200L
private const val MEMBERS_QUERY_CHUNK_SIZE = 30

class FirestoreCommunityRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : CommunityRepository {

    override fun observeForumPosts(): Flow<List<ForumPost>> = callbackFlow {
        val query = FirestoreCollections.posts(firestore).orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING)
        val registration = query.addSnapshotListener { snapshot, _ ->
            trySend(snapshot?.documents.orEmpty().mapNotNull { it.toForumPost() }.filterNot { it.isHidden })
        }
        awaitClose { registration.remove() }
    }

    override suspend fun postToForum(
        authorId: String,
        authorName: String?,
        authorAvatar: AvatarStyle?,
        text: String,
    ) {
        val data = mapOf(
            FIELD_AUTHOR_ID to authorId,
            FIELD_AUTHOR_NAME to authorName,
            FIELD_AUTHOR_AVATAR to authorAvatar?.name,
            FIELD_TEXT to text,
            FIELD_CREATED_AT to System.currentTimeMillis(),
        )
        FirestoreCollections.posts(firestore).add(data).await()
    }

    override fun observeGroups(): Flow<List<CommunityGroup>> = callbackFlow {
        val registration = FirestoreCollections.groups(firestore).addSnapshotListener { snapshot, _ ->
            trySend(snapshot?.documents.orEmpty().mapNotNull { it.toCommunityGroup() })
        }
        awaitClose { registration.remove() }
    }

    override suspend fun createGroup(name: String, creatorId: String) {
        val data = mapOf(
            FIELD_NAME to name,
            FIELD_MEMBER_IDS to listOf(creatorId),
            FIELD_OWNER_ID to creatorId,
            FIELD_CREATED_AT to System.currentTimeMillis(),
            FIELD_INVITE_CODE to InviteCodeGenerator.generate(),
        )
        FirestoreCollections.groups(firestore).add(data).await()
    }

    override suspend fun joinGroup(groupId: String, userId: String) {
        FirestoreCollections.groups(firestore).document(groupId)
            .update(FIELD_MEMBER_IDS, FieldValue.arrayUnion(userId))
            .await()
    }

    override fun observeGroup(groupId: String): Flow<CommunityGroup?> =
        FirestoreCollections.groups(firestore).document(groupId).cacheFirstSnapshots()
            .map { snapshot -> snapshot.toCommunityGroup() }
            .distinctUntilChanged()

    override suspend fun leaveGroup(groupId: String, userId: String) {
        FirestoreCollections.groups(firestore).document(groupId)
            .update(FIELD_MEMBER_IDS, FieldValue.arrayRemove(userId))
            .await()
    }

    override suspend fun findGroup(inviteCode: String): CommunityGroup? =
        FirestoreCollections.groups(firestore)
            .whereEqualTo(FIELD_INVITE_CODE, inviteCode)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.toCommunityGroup()

    override fun observeMessages(groupId: String): Flow<List<GroupMessage>> =
        FirestoreCollections.chatMessages(firestore, groupId)
            .orderBy(FIELD_CREATED_AT, Query.Direction.ASCENDING)
            .limitToLast(MESSAGES_LIMIT)
            .cacheFirstSnapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.toGroupMessage() } }
            .distinctUntilChanged()

    override suspend fun sendMessage(
        groupId: String,
        senderId: String,
        senderName: String?,
        senderAvatar: AvatarStyle?,
        text: String,
    ) {
        val data = mapOf(
            FIELD_SENDER_ID to senderId,
            FIELD_SENDER_NAME to senderName,
            FIELD_SENDER_AVATAR to senderAvatar?.name,
            FIELD_TEXT to text,
            FIELD_CREATED_AT to System.currentTimeMillis(),
        )
        FirestoreCollections.chatMessages(firestore, groupId).add(data).await()
    }

    override suspend fun fetchMembers(ids: List<String>): List<GroupMember> {
        val membersById = ids.chunked(MEMBERS_QUERY_CHUNK_SIZE).flatMap { chunk ->
            FirestoreCollections.users(firestore)
                .whereIn(FieldPath.documentId(), chunk)
                .get()
                .await()
                .documents
                .map { it.toGroupMember() }
        }.associateBy { it.id }
        return ids.map { id -> membersById[id] ?: GroupMember(id = id, name = null, avatar = AvatarStyle.LIME) }
    }
}

internal fun DocumentSnapshot.toForumPost(): ForumPost? {
    val authorId = getString(FIELD_AUTHOR_ID) ?: return null
    val text = getString(FIELD_TEXT) ?: return null
    val createdAt = getLong(FIELD_CREATED_AT) ?: return null
    val avatarName = getString(FIELD_AUTHOR_AVATAR)
    val authorAvatar = AvatarStyle.entries.firstOrNull { it.name == avatarName }
    return ForumPost(
        id = id,
        authorId = authorId,
        authorName = getString(FIELD_AUTHOR_NAME),
        authorAvatar = authorAvatar,
        text = text,
        createdAtEpochMillis = createdAt,
        isHidden = getBoolean(FIELD_HIDDEN) == true,
        reportCount = getLong(FIELD_REPORT_COUNT)?.toInt() ?: 0,
    )
}

private fun DocumentSnapshot.toCommunityGroup(): CommunityGroup? {
    val name = getString(FIELD_NAME) ?: return null
    val memberIds = (get(FIELD_MEMBER_IDS) as? List<*>)?.filterIsInstance<String>().orEmpty()
    return CommunityGroup(
        id = id,
        name = name,
        memberIds = memberIds,
        ownerId = getString(FIELD_OWNER_ID),
        inviteCode = getString(FIELD_INVITE_CODE),
    )
}

private fun DocumentSnapshot.toGroupMessage(): GroupMessage? {
    val senderId = getString(FIELD_SENDER_ID) ?: return null
    val text = getString(FIELD_TEXT) ?: return null
    val sentAt = getLong(FIELD_CREATED_AT) ?: return null
    return GroupMessage(
        id = id,
        senderId = senderId,
        senderName = getString(FIELD_SENDER_NAME),
        senderAvatar = AvatarStyle.entries.firstOrNull { it.name == getString(FIELD_SENDER_AVATAR) },
        text = text,
        sentAtEpochMillis = sentAt,
    )
}

private fun DocumentSnapshot.toGroupMember(): GroupMember {
    val name = listOfNotNull(getString(FIELD_FIRST_NAME), getString(FIELD_LAST_NAME)).joinToString(" ").trim()
    return GroupMember(
        id = id,
        name = name.ifBlank { null },
        avatar = AvatarStyle.entries.firstOrNull { it.name == getString(FIELD_AVATAR) } ?: AvatarStyle.LIME,
    )
}
