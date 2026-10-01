package com.smartcity.greenpassport.core.model

import kotlinx.coroutines.flow.Flow

data class EcoEvent(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val city: String,
    val startAtEpochMillis: Long,
    val imageUrl: String?,
    val rewardPoints: Int,
)

interface EventsRepository {
    fun observeEvents(): Flow<List<EcoEvent>>
    fun observeRegisteredEventIds(userId: String): Flow<Set<String>>
    fun observeAttendedEventIds(userId: String): Flow<Set<String>>
    suspend fun getEvents(): List<EcoEvent>
    suspend fun getRegisteredEventIds(userId: String): Set<String>
    suspend fun registerForEvent(userId: String, eventId: String)
}
