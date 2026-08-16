package com.smartcity.greenpassport.core.database

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.smartcity.greenpassport.core.model.ChatMessage
import com.smartcity.greenpassport.core.model.CommunityGroup
import com.smartcity.greenpassport.core.model.CommunityRepository
import com.smartcity.greenpassport.core.model.ForumPost
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val FIELD_AUTHOR_ID = "authorId"
private const val FIELD_TEXT = "text"
private const val FIELD_CREATED_AT = "createdAtEpochMillis"

private const val FIELD_NAME = "name"
private const val FIELD_MEMBER_IDS = "memberIds"

private const val FIELD_SENDER_ID = "senderId"
private const val FIELD_SENT_AT = "sentAtEpochMillis"

class FirestoreCommunityRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : CommunityRepository {

    override fun observeForumPosts(): Flow<List<ForumPost>> = callbackFlow {
        val query = FirestoreCollections.posts(firestore).orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING)
        val registration = query.addSnapshotListener { snapshot, _ ->
            trySend(snapshot?.documents.orEmpty().mapNotNull { it.toForumPost() })
        }
        awaitClose { registration.remove() }
    }

    override suspend fun postToForum(authorId: String, text: String) {
        val data = mapOf(
            FIELD_AUTHOR_ID to authorId,
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
        val data = mapOf(FIELD_NAME to name, FIELD_MEMBER_IDS to listOf(creatorId))
        FirestoreCollections.groups(firestore).add(data).await()
    }

    override suspend fun joinGroup(groupId: String, userId: String) {
        FirestoreCollections.groups(firestore).document(groupId)
            .update(FIELD_MEMBER_IDS, FieldValue.arrayUnion(userId))
            .await()
    }

    override fun observeChatMessages(chatId: String): Flow<List<ChatMessage>> = callbackFlow {
        val query = FirestoreCollections.chatMessages(firestore, chatId)
            .orderBy(FIELD_SENT_AT, Query.Direction.ASCENDING)
        val registration = query.addSnapshotListener { snapshot, _ ->
            trySend(snapshot?.documents.orEmpty().mapNotNull { it.toChatMessage() })
        }
        awaitClose { registration.remove() }
    }

    override suspend fun sendChatMessage(chatId: String, senderId: String, text: String) {
        val data = mapOf(
            FIELD_SENDER_ID to senderId,
            FIELD_TEXT to text,
            FIELD_SENT_AT to System.currentTimeMillis(),
        )
        FirestoreCollections.chatMessages(firestore, chatId).add(data).await()
    }
}

private fun DocumentSnapshot.toForumPost(): ForumPost? {
    val authorId = getString(FIELD_AUTHOR_ID) ?: return null
    val text = getString(FIELD_TEXT) ?: return null
    val createdAt = getLong(FIELD_CREATED_AT) ?: return null
    return ForumPost(id = id, authorId = authorId, text = text, createdAtEpochMillis = createdAt)
}

private fun DocumentSnapshot.toCommunityGroup(): CommunityGroup? {
    val name = getString(FIELD_NAME) ?: return null
    @Suppress("UNCHECKED_CAST")
    val memberIds = get(FIELD_MEMBER_IDS) as? List<String> ?: emptyList()
    return CommunityGroup(id = id, name = name, memberIds = memberIds)
}

private fun DocumentSnapshot.toChatMessage(): ChatMessage? {
    val senderId = getString(FIELD_SENDER_ID) ?: return null
    val text = getString(FIELD_TEXT) ?: return null
    val sentAt = getLong(FIELD_SENT_AT) ?: return null
    return ChatMessage(id = id, senderId = senderId, text = text, sentAtEpochMillis = sentAt)
}
