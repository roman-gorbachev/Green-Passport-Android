package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.model.Coupon
import com.smartcity.greenpassport.core.model.Reward
import com.smartcity.greenpassport.core.model.ShopRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val FIELD_TITLE = "title"
private const val FIELD_PARTNER_NAME = "partnerName"
private const val FIELD_POINTS_COST = "pointsCost"

private const val FIELD_USER_ID = "userId"
private const val FIELD_REWARD_ID = "rewardId"
private const val FIELD_REDEEMED_AT = "redeemedAtEpochMillis"
private const val FIELD_EXPIRES_AT = "expiresAtEpochMillis"

class FirestoreShopRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ShopRepository {

    override suspend fun getRewards(): List<Reward> {
        val snapshot = FirestoreCollections.shopItems(firestore).get().await()
        return snapshot.documents.mapNotNull { it.toReward() }
    }

    override suspend fun getPurchases(userId: String): List<Coupon> {
        val snapshot = FirestoreCollections.purchases(firestore)
            .whereEqualTo(FIELD_USER_ID, userId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toCoupon() }
    }
}

private fun DocumentSnapshot.toReward(): Reward? {
    val title = getString(FIELD_TITLE) ?: return null
    val partnerName = getString(FIELD_PARTNER_NAME) ?: return null
    val pointsCost = getLong(FIELD_POINTS_COST)?.toInt() ?: return null
    return Reward(id = id, title = title, partnerName = partnerName, pointsCost = pointsCost)
}

private fun DocumentSnapshot.toCoupon(): Coupon? {
    val rewardId = getString(FIELD_REWARD_ID) ?: return null
    val redeemedAt = getLong(FIELD_REDEEMED_AT) ?: return null
    return Coupon(
        id = id,
        rewardId = rewardId,
        redeemedAtEpochMillis = redeemedAt,
        expiresAtEpochMillis = getLong(FIELD_EXPIRES_AT),
    )
}
