package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.core.model.community.MessageEditingRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val FIELD_TEXT = "text"
private const val FIELD_EDITED_AT = "editedAtEpochMillis"
private const val FIELD_DELETED = "deleted"
private const val FIELD_REPLY_TO = "replyTo"
private const val FIELD_FORWARDED_FROM = "forwardedFrom"

class FirestoreMessageEditingRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : MessageEditingRepository {

    override suspend fun editMessage(chat: ChatId, messageId: String, text: String) {
        messageReference(chat, messageId)
            .update(mapOf(FIELD_TEXT to text, FIELD_EDITED_AT to System.currentTimeMillis()))
            .await()
    }

    override suspend fun deleteMessage(chat: ChatId, messageId: String) {
        messageReference(chat, messageId)
            .update(
                mapOf(
                    FIELD_TEXT to "",
                    FIELD_DELETED to true,
                    FIELD_REPLY_TO to FieldValue.delete(),
                    FIELD_FORWARDED_FROM to FieldValue.delete(),
                ),
            )
            .await()
    }

    private fun messageReference(chat: ChatId, messageId: String) = when (chat) {
        ChatId.Forum -> FirestoreCollections.posts(firestore).document(messageId)
        is ChatId.Group -> FirestoreCollections.chatMessages(firestore, chat.id).document(messageId)
    }
}
