package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.model.EcoTip
import com.smartcity.greenpassport.core.model.EcoTipCategory
import com.smartcity.greenpassport.core.model.EcoTipsRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val FIELD_CATEGORY = "category"
private const val FIELD_TITLE = "title"
private const val FIELD_BODY = "body"
private const val FIELD_MEDIA_URL = "mediaUrl"
private const val FIELD_IS_DAILY_TIP = "isDailyTip"
private const val FIELD_REWARD_POINTS = "rewardPoints"
private const val FIELD_REWARD_XP = "rewardXp"

private const val FIELD_USER_ID = "userId"
private const val FIELD_TIP_ID = "tipId"
private const val FIELD_READ_AT = "readAtEpochMillis"

class FirestoreEcoTipsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : EcoTipsRepository {

    override suspend fun getTips(): List<EcoTip> {
        val snapshot = FirestoreCollections.ecoTips(firestore).get().await()
        return snapshot.documents.mapNotNull { it.toEcoTip() }
    }

    override suspend fun getReadTipIds(userId: String): Set<String> {
        val snapshot = FirestoreCollections.ecoTipReads(firestore)
            .whereEqualTo(FIELD_USER_ID, userId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.getString(FIELD_TIP_ID) }.toSet()
    }

    override suspend fun markTipRead(userId: String, tipId: String) {
        val readId = "${userId}_$tipId"
        FirestoreCollections.ecoTipReads(firestore).document(readId)
            .set(
                mapOf(
                    FIELD_USER_ID to userId,
                    FIELD_TIP_ID to tipId,
                    FIELD_READ_AT to System.currentTimeMillis(),
                ),
            )
            .await()
    }
}

private fun DocumentSnapshot.toEcoTip(): EcoTip? {
    val category = getString(FIELD_CATEGORY)?.let { name ->
        runCatching { EcoTipCategory.valueOf(name) }.getOrNull()
    } ?: return null
    val title = getString(FIELD_TITLE) ?: return null
    val body = getString(FIELD_BODY) ?: return null

    return EcoTip(
        id = id,
        category = category,
        title = title,
        body = body,
        mediaUrl = getString(FIELD_MEDIA_URL),
        isDailyTip = getBoolean(FIELD_IS_DAILY_TIP) ?: false,
        rewardPoints = getLong(FIELD_REWARD_POINTS)?.toInt() ?: 0,
        rewardXp = getLong(FIELD_REWARD_XP)?.toInt() ?: 0,
    )
}
