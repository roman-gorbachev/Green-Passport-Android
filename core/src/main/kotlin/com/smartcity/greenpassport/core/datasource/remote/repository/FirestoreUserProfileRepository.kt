package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.model.TaskCategory
import com.smartcity.greenpassport.core.model.profile.AvatarStyle
import com.smartcity.greenpassport.core.model.profile.UserProfile
import com.smartcity.greenpassport.core.model.profile.UserProfileRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val FIELD_FIRST_NAME = "firstName"
private const val FIELD_LAST_NAME = "lastName"
private const val FIELD_CITY = "city"
private const val FIELD_INTERESTS = "interests"
private const val FIELD_AVATAR = "avatar"
private const val FIELD_PROFILE_COMPLETED_AT = "profileCompletedAt"

class FirestoreUserProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : UserProfileRepository {

    override fun observeProfile(userId: String): Flow<UserProfile?> = callbackFlow {
        val registration = FirestoreCollections.users(firestore).document(userId)
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> close(error)
                    snapshot == null -> Unit
                    snapshot.metadata.isFromCache && !snapshot.exists() -> Unit
                    else -> trySend(snapshot.toUserProfile())
                }
            }
        awaitClose { registration.remove() }
    }

    override suspend fun saveProfile(profile: UserProfile) {
        FirestoreCollections.users(firestore).document(profile.userId).set(
            mapOf(
                FIELD_FIRST_NAME to profile.firstName,
                FIELD_LAST_NAME to profile.lastName,
                FIELD_CITY to profile.city,
                FIELD_INTERESTS to profile.interests.map { it.name },
                FIELD_AVATAR to profile.avatar.name,
                FIELD_PROFILE_COMPLETED_AT to System.currentTimeMillis(),
            ),
            SetOptions.merge(),
        ).await()
    }
}

private fun DocumentSnapshot.toUserProfile(): UserProfile? {
    if (!exists() || getLong(FIELD_PROFILE_COMPLETED_AT) == null) return null
    val firstName = getString(FIELD_FIRST_NAME) ?: return null
    val interests = (get(FIELD_INTERESTS) as? List<*>).orEmpty()
        .mapNotNull { name -> TaskCategory.entries.firstOrNull { it.name == name } }
        .toSet()
    return UserProfile(
        userId = id,
        firstName = firstName,
        lastName = getString(FIELD_LAST_NAME).orEmpty(),
        city = getString(FIELD_CITY).orEmpty(),
        interests = interests,
        avatar = AvatarStyle.entries.firstOrNull { it.name == getString(FIELD_AVATAR) } ?: AvatarStyle.LIME,
    )
}
