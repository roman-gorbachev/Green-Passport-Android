package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.datasource.remote.cacheFirstSnapshots
import com.smartcity.greenpassport.core.datasource.remote.localizedText
import com.smartcity.greenpassport.core.model.Coupon
import com.smartcity.greenpassport.core.model.Reward
import com.smartcity.greenpassport.core.model.ShopRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val FIELD_TITLE = "title"
private const val FIELD_PARTNER_NAME = "partnerName"
private const val FIELD_TITLES = "titles"
private const val FIELD_PARTNER_NAMES = "partnerNames"
private const val FIELD_POINTS_COST = "pointsCost"
private const val FIELD_IMAGE_URL = "imageUrl"
private const val FIELD_IS_ACTIVE = "isActive"

private const val FIELD_USER_ID = "userId"
private const val FIELD_REWARD_ID = "rewardId"
private const val FIELD_REDEEMED_AT = "redeemedAtEpochMillis"
private const val FIELD_EXPIRES_AT = "expiresAtEpochMillis"
private const val FIELD_CODE = "code"
private const val FIELD_USED_AT = "usedAtEpochMillis"

class FirestoreShopRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ShopRepository {

    override fun observeRewards(): Flow<List<Reward>> =
        FirestoreCollections.shopItems(firestore).cacheFirstSnapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.toReward() } }
            .distinctUntilChanged()

    override fun observePurchases(userId: String): Flow<List<Coupon>> =
        FirestoreCollections.purchases(firestore)
            .whereEqualTo(FIELD_USER_ID, userId)
            .cacheFirstSnapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.toCoupon() } }
            .distinctUntilChanged()

    override fun observePurchase(couponId: String): Flow<Coupon?> =
        FirestoreCollections.purchases(firestore).document(couponId).cacheFirstSnapshots()
            .map { snapshot -> snapshot.toCoupon() }
            .distinctUntilChanged()

    override suspend fun getRewards(): List<Reward> = observeRewards().first()

    override suspend fun getPurchases(userId: String): List<Coupon> = observePurchases(userId).first()
}

private fun DocumentSnapshot.toReward(): Reward? {
    val title = localizedText(FIELD_TITLE, FIELD_TITLES) ?: return null
    val partnerName = localizedText(FIELD_PARTNER_NAME, FIELD_PARTNER_NAMES) ?: return null
    val pointsCost = getLong(FIELD_POINTS_COST)?.toInt() ?: return null
    return Reward(
        id = id,
        title = title,
        partnerName = partnerName,
        pointsCost = pointsCost,
        imageUrl = getString(FIELD_IMAGE_URL),
        isActive = getBoolean(FIELD_IS_ACTIVE) ?: true,
    )
}

private fun DocumentSnapshot.toCoupon(): Coupon? {
    val rewardId = getString(FIELD_REWARD_ID) ?: return null
    val redeemedAt = getLong(FIELD_REDEEMED_AT) ?: return null
    return Coupon(
        id = id,
        rewardId = rewardId,
        redeemedAtEpochMillis = redeemedAt,
        expiresAtEpochMillis = getLong(FIELD_EXPIRES_AT),
        code = getString(FIELD_CODE),
        usedAtEpochMillis = getLong(FIELD_USED_AT),
    )
}
