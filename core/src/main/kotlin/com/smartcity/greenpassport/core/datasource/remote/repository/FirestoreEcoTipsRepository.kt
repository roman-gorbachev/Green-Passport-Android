package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.datasource.remote.cacheFirstSnapshots
import com.smartcity.greenpassport.core.datasource.remote.localizedText
import com.smartcity.greenpassport.core.model.EcoTip
import com.smartcity.greenpassport.core.model.EcoTipCategory
import com.smartcity.greenpassport.core.model.EcoTipsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val FIELD_CATEGORY = "category"
private const val FIELD_TITLE = "title"
private const val FIELD_BODY = "body"
private const val FIELD_MEDIA_URL = "mediaUrl"
private const val FIELD_IMAGE_URL = "imageUrl"
private const val FIELD_TITLES = "titles"
private const val FIELD_BODIES = "bodies"
private const val FIELD_IS_DAILY_TIP = "isDailyTip"
private const val FIELD_REWARD_POINTS = "rewardPoints"
private const val FIELD_REWARD_XP = "rewardXp"
private const val FIELD_IS_ACTIVE = "isActive"

private const val FIELD_USER_ID = "userId"
private const val FIELD_TIP_ID = "tipId"

class FirestoreEcoTipsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : EcoTipsRepository {

    override fun observeTips(): Flow<List<EcoTip>> =
        FirestoreCollections.ecoTips(firestore).cacheFirstSnapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.toEcoTip() } }
            .distinctUntilChanged()

    override fun observeReadTipIds(userId: String): Flow<Set<String>> =
        FirestoreCollections.ecoTipReads(firestore)
            .whereEqualTo(FIELD_USER_ID, userId)
            .cacheFirstSnapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.getString(FIELD_TIP_ID) }.toSet() }
            .distinctUntilChanged()

    override suspend fun getTips(): List<EcoTip> = observeTips().first()

    override suspend fun getReadTipIds(userId: String): Set<String> = observeReadTipIds(userId).first()
}

private fun DocumentSnapshot.toEcoTip(): EcoTip? {
    val category = getString(FIELD_CATEGORY)?.let { name ->
        runCatching { EcoTipCategory.valueOf(name) }.getOrNull()
    } ?: return null
    val title = localizedText(FIELD_TITLE, FIELD_TITLES) ?: return null
    val body = localizedText(FIELD_BODY, FIELD_BODIES) ?: return null

    return EcoTip(
        id = id,
        category = category,
        title = title,
        body = body,
        mediaUrl = getString(FIELD_MEDIA_URL),
        imageUrl = getString(FIELD_IMAGE_URL),
        isDailyTip = getBoolean(FIELD_IS_DAILY_TIP) ?: false,
        rewardPoints = getLong(FIELD_REWARD_POINTS)?.toInt() ?: 0,
        rewardXp = getLong(FIELD_REWARD_XP)?.toInt() ?: 0,
        isActive = getBoolean(FIELD_IS_ACTIVE) ?: true,
    )
}
