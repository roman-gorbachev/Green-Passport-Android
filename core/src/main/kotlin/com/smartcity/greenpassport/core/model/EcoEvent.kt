package com.smartcity.greenpassport.core.model

data class EcoEvent(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val city: String,
    val startAtEpochMillis: Long,
)

interface EventsRepository {
    suspend fun getEvents(): List<EcoEvent>
    suspend fun getRegisteredEventIds(userId: String): Set<String>
    suspend fun registerForEvent(userId: String, eventId: String)
}
