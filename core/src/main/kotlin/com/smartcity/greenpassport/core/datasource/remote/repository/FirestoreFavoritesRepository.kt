package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.model.FavoritesRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val FIELD_USER_ID = "userId"
private const val FIELD_TASK_ID = "taskId"
private const val FIELD_TIP_ID = "tipId"

class FirestoreFavoritesRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : FavoritesRepository {

    override fun observeFavoriteTaskIds(userId: String): Flow<Set<String>> = callbackFlow {
        val registration = FirestoreCollections.favoriteTasks(firestore)
            .whereEqualTo(FIELD_USER_ID, userId)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.documents.orEmpty().mapNotNull { it.getString(FIELD_TASK_ID) }.toSet())
            }
        awaitClose { registration.remove() }
    }

    override suspend fun setTaskFavorite(userId: String, taskId: String, isFavorite: Boolean) {
        val document = FirestoreCollections.favoriteTasks(firestore).document("${userId}_$taskId")
        if (isFavorite) {
            document.set(mapOf(FIELD_USER_ID to userId, FIELD_TASK_ID to taskId)).await()
        } else {
            document.delete().await()
        }
    }

    override fun observeBookmarkedTipIds(userId: String): Flow<Set<String>> = callbackFlow {
        val registration = FirestoreCollections.bookmarkedTips(firestore)
            .whereEqualTo(FIELD_USER_ID, userId)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.documents.orEmpty().mapNotNull { it.getString(FIELD_TIP_ID) }.toSet())
            }
        awaitClose { registration.remove() }
    }

    override suspend fun setTipBookmarked(userId: String, tipId: String, isBookmarked: Boolean) {
        val document = FirestoreCollections.bookmarkedTips(firestore).document("${userId}_$tipId")
        if (isBookmarked) {
            document.set(mapOf(FIELD_USER_ID to userId, FIELD_TIP_ID to tipId)).await()
        } else {
            document.delete().await()
        }
    }
}
