package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.datasource.remote.cacheFirstSnapshots
import com.smartcity.greenpassport.core.model.Experience
import com.smartcity.greenpassport.core.model.PointsBalance
import com.smartcity.greenpassport.core.model.PointsRepository
import com.smartcity.greenpassport.core.model.Streak
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val FIELD_AVAILABLE_POINTS = "availablePoints"
private const val FIELD_LIFETIME_XP = "lifetimeXp"
private const val FIELD_STREAK = "streak"
private const val FIELD_STREAK_COUNT = "count"
private const val FIELD_STREAK_LAST_DAY = "lastDay"

class FirestorePointsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : PointsRepository {

    private val users get() = FirestoreCollections.users(firestore)

    override fun observeBalance(userId: String): Flow<PointsBalance> =
        users.document(userId).cacheFirstSnapshots()
            .map { snapshot -> PointsBalance(userId = userId, availablePoints = snapshot.availablePointsOrZero()) }
            .distinctUntilChanged()

    override fun observeExperience(userId: String): Flow<Experience> =
        users.document(userId).cacheFirstSnapshots()
            .map { snapshot -> Experience(userId = userId, lifetimeXp = snapshot.lifetimeXpOrZero()) }
            .distinctUntilChanged()

    override fun observeStreak(userId: String): Flow<Streak?> =
        users.document(userId).cacheFirstSnapshots()
            .map { snapshot -> snapshot.streakOrNull() }
            .distinctUntilChanged()

    override suspend fun getBalance(userId: String): PointsBalance = observeBalance(userId).first()

    override suspend fun getExperience(userId: String): Experience = observeExperience(userId).first()
}

private fun DocumentSnapshot.availablePointsOrZero(): Int =
    getLong(FIELD_AVAILABLE_POINTS)?.toInt() ?: 0

private fun DocumentSnapshot.lifetimeXpOrZero(): Int =
    getLong(FIELD_LIFETIME_XP)?.toInt() ?: 0

private fun DocumentSnapshot.streakOrNull(): Streak? {
    val streak = get(FIELD_STREAK) as? Map<*, *> ?: return null
    val count = (streak[FIELD_STREAK_COUNT] as? Number)?.toInt() ?: return null
    val lastDay = streak[FIELD_STREAK_LAST_DAY] as? String ?: return null
    return Streak(count = count, lastDay = lastDay)
}
