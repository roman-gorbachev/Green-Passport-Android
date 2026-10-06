package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.model.community.MessageNotificationsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val FIELD_MESSAGE_NOTIFICATIONS_ENABLED = "messageNotificationsEnabled"

class FirestoreMessageNotificationsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : MessageNotificationsRepository {

    override fun observeIsEnabled(userId: String): Flow<Boolean> = callbackFlow {
        val registration = FirestoreCollections.users(firestore).document(userId).addSnapshotListener { snapshot, _ ->
            trySend(snapshot?.getBoolean(FIELD_MESSAGE_NOTIFICATIONS_ENABLED) ?: true)
        }
        awaitClose { registration.remove() }
    }

    override suspend fun setEnabled(isEnabled: Boolean, userId: String) {
        FirestoreCollections.users(firestore).document(userId)
            .set(mapOf(FIELD_MESSAGE_NOTIFICATIONS_ENABLED to isEnabled), SetOptions.merge())
            .await()
    }
}
