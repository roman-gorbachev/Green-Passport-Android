package com.smartcity.greenpassport.core.database

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.model.Coupon
import com.smartcity.greenpassport.core.model.Reward
import com.smartcity.greenpassport.core.model.ShopRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

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

    override suspend fun recordPurchase(userId: String, reward: Reward): Coupon {
        val redeemedAt = System.currentTimeMillis()
        val data = mapOf(
            FIELD_USER_ID to userId,
            FIELD_REWARD_ID to reward.id,
            FIELD_REDEEMED_AT to redeemedAt,
        )
        val documentRef = FirestoreCollections.purchases(firestore).add(data).await()
        return Coupon(
            id = documentRef.id,
            rewardId = reward.id,
            redeemedAtEpochMillis = redeemedAt,
            expiresAtEpochMillis = null,
        )
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
