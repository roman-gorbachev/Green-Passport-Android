package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.datasource.remote.cacheFirstSnapshots
import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.core.model.EventsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val FIELD_TITLE = "title"
private const val FIELD_DESCRIPTION = "description"
private const val FIELD_LOCATION = "location"
private const val FIELD_CITY = "city"
private const val FIELD_START_AT = "startAtEpochMillis"
private const val FIELD_IMAGE_URL = "imageUrl"
private const val FIELD_REWARD_POINTS = "rewardPoints"

private const val FIELD_USER_ID = "userId"
private const val FIELD_EVENT_ID = "eventId"
private const val FIELD_REGISTERED_AT = "registeredAtEpochMillis"

class FirestoreEventsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : EventsRepository {

    override fun observeEvents(): Flow<List<EcoEvent>> =
        FirestoreCollections.events(firestore).cacheFirstSnapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.toEcoEvent() } }
            .distinctUntilChanged()

    override fun observeRegisteredEventIds(userId: String): Flow<Set<String>> =
        FirestoreCollections.eventRegistrations(firestore)
            .whereEqualTo(FIELD_USER_ID, userId)
            .cacheFirstSnapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.getString(FIELD_EVENT_ID) }.toSet() }
            .distinctUntilChanged()

    override fun observeAttendedEventIds(userId: String): Flow<Set<String>> =
        FirestoreCollections.eventAttendance(firestore)
            .whereEqualTo(FIELD_USER_ID, userId)
            .cacheFirstSnapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.getString(FIELD_EVENT_ID) }.toSet() }
            .distinctUntilChanged()

    override suspend fun getEvents(): List<EcoEvent> = observeEvents().first()

    override suspend fun getRegisteredEventIds(userId: String): Set<String> = observeRegisteredEventIds(userId).first()

    override suspend fun registerForEvent(userId: String, eventId: String) {
        val registrationId = "${userId}_$eventId"
        FirestoreCollections.eventRegistrations(firestore).document(registrationId)
            .set(
                mapOf(
                    FIELD_USER_ID to userId,
                    FIELD_EVENT_ID to eventId,
                    FIELD_REGISTERED_AT to System.currentTimeMillis(),
                ),
            )
            .await()
    }
}

private fun DocumentSnapshot.toEcoEvent(): EcoEvent? {
    val title = getString(FIELD_TITLE) ?: return null
    val description = getString(FIELD_DESCRIPTION) ?: return null
    val location = getString(FIELD_LOCATION) ?: return null
    val city = getString(FIELD_CITY) ?: return null
    val startAt = getLong(FIELD_START_AT) ?: return null

    return EcoEvent(
        id = id,
        title = title,
        description = description,
        location = location,
        city = city,
        startAtEpochMillis = startAt,
        imageUrl = getString(FIELD_IMAGE_URL),
        rewardPoints = getLong(FIELD_REWARD_POINTS)?.toInt() ?: 0,
    )
}
