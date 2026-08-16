package com.smartcity.greenpassport.core.database

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.smartcity.greenpassport.core.model.Experience
import com.smartcity.greenpassport.core.model.PointsAward
import com.smartcity.greenpassport.core.model.PointsBalance
import com.smartcity.greenpassport.core.model.PointsRepository
import com.smartcity.greenpassport.core.model.PointsSpendReason
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

private const val FIELD_AVAILABLE_POINTS = "availablePoints"
private const val FIELD_LIFETIME_XP = "lifetimeXp"

class FirestorePointsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : PointsRepository {

    private val users get() = FirestoreCollections.users(firestore)

    override suspend fun getBalance(userId: String): PointsBalance {
        val snapshot = users.document(userId).get().await()
        return PointsBalance(userId = userId, availablePoints = snapshot.availablePointsOrZero())
    }

    override suspend fun getExperience(userId: String): Experience {
        val snapshot = users.document(userId).get().await()
        return Experience(userId = userId, lifetimeXp = snapshot.lifetimeXpOrZero())
    }

    override suspend fun award(userId: String, award: PointsAward): PointsBalance {
        val userDoc = users.document(userId)
        val updatedPoints = firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userDoc)
            val newPoints = snapshot.availablePointsOrZero() + award.points
            val newXp = snapshot.lifetimeXpOrZero() + award.xp
            transaction.set(
                userDoc,
                mapOf(
                    FIELD_AVAILABLE_POINTS to newPoints,
                    FIELD_LIFETIME_XP to newXp,
                ),
                SetOptions.merge(),
            )
            newPoints
        }.await()
        return PointsBalance(userId = userId, availablePoints = updatedPoints)
    }

    override suspend fun spend(userId: String, points: Int, reason: PointsSpendReason): PointsBalance {
        val userDoc = users.document(userId)
        val updatedPoints = firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userDoc)
            val currentPoints = snapshot.availablePointsOrZero()
            check(currentPoints >= points) { "Not enough points: has $currentPoints, needs $points" }
            val newPoints = currentPoints - points
            transaction.update(userDoc, FIELD_AVAILABLE_POINTS, newPoints)
            newPoints
        }.await()
        return PointsBalance(userId = userId, availablePoints = updatedPoints)
    }
}

private fun DocumentSnapshot.availablePointsOrZero(): Int =
    getLong(FIELD_AVAILABLE_POINTS)?.toInt() ?: 0

private fun DocumentSnapshot.lifetimeXpOrZero(): Int =
    getLong(FIELD_LIFETIME_XP)?.toInt() ?: 0
