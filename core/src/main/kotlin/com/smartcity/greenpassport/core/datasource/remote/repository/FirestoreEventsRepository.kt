package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.core.model.EventsRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val FIELD_TITLE = "title"
private const val FIELD_DESCRIPTION = "description"
private const val FIELD_LOCATION = "location"
private const val FIELD_CITY = "city"
private const val FIELD_START_AT = "startAtEpochMillis"

private const val FIELD_USER_ID = "userId"
private const val FIELD_EVENT_ID = "eventId"
private const val FIELD_REGISTERED_AT = "registeredAtEpochMillis"

class FirestoreEventsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : EventsRepository {

    override suspend fun getEvents(): List<EcoEvent> {
        val snapshot = FirestoreCollections.events(firestore).get().await()
        return snapshot.documents.mapNotNull { it.toEcoEvent() }
    }

    override suspend fun getRegisteredEventIds(userId: String): Set<String> {
        val snapshot = FirestoreCollections.eventRegistrations(firestore)
            .whereEqualTo(FIELD_USER_ID, userId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.getString(FIELD_EVENT_ID) }.toSet()
    }

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
    )
}
