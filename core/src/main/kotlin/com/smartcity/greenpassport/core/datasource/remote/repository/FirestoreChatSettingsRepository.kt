package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.core.model.community.ChatSettings
import com.smartcity.greenpassport.core.model.community.ChatSettingsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val FIELD_USER_ID = "userId"
private const val FIELD_CHAT_ID = "chatId"
private const val FIELD_PINNED = "pinned"
private const val FIELD_ARCHIVED = "archived"
private const val FIELD_MUTED = "muted"
private const val FIELD_UPDATED_AT = "updatedAtEpochMillis"

class FirestoreChatSettingsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ChatSettingsRepository {

    override fun observeSettings(userId: String): Flow<Map<ChatId, ChatSettings>> = callbackFlow {
        val query = FirestoreCollections.chatSettings(firestore).whereEqualTo(FIELD_USER_ID, userId)
        val registration = query.addSnapshotListener { snapshot, _ ->
            trySend(snapshot?.documents.orEmpty().mapNotNull { it.toChatSettings() }.associateBy { it.chatId })
        }
        awaitClose { registration.remove() }
    }

    override suspend fun save(settings: ChatSettings, userId: String) {
        val data = mapOf(
            FIELD_USER_ID to userId,
            FIELD_CHAT_ID to settings.chatId.rawValue,
            FIELD_PINNED to settings.isPinned,
            FIELD_ARCHIVED to settings.isArchived,
            FIELD_MUTED to settings.isMuted,
            FIELD_UPDATED_AT to System.currentTimeMillis(),
        )
        FirestoreCollections.chatSettings(firestore)
            .document("${userId}_${settings.chatId.rawValue}")
            .set(data)
            .await()
    }
}

private fun DocumentSnapshot.toChatSettings(): ChatSettings? {
    val chatId = getString(FIELD_CHAT_ID)?.let(ChatId::fromRawValue) ?: return null
    val defaults = ChatSettings.defaults(chatId)
    return ChatSettings(
        chatId = chatId,
        isPinned = getBoolean(FIELD_PINNED) ?: defaults.isPinned,
        isArchived = getBoolean(FIELD_ARCHIVED) ?: defaults.isArchived,
        isMuted = getBoolean(FIELD_MUTED) ?: defaults.isMuted,
    )
}
