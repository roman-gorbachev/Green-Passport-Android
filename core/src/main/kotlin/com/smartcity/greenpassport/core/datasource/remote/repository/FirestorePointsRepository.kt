package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.model.Experience
import com.smartcity.greenpassport.core.model.PointsBalance
import com.smartcity.greenpassport.core.model.PointsRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

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
}

private fun DocumentSnapshot.availablePointsOrZero(): Int =
    getLong(FIELD_AVAILABLE_POINTS)?.toInt() ?: 0

private fun DocumentSnapshot.lifetimeXpOrZero(): Int =
    getLong(FIELD_LIFETIME_XP)?.toInt() ?: 0
