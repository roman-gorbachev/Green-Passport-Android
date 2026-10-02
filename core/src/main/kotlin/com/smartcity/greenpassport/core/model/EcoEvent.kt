package com.smartcity.greenpassport.core.model

import kotlinx.coroutines.flow.Flow

data class EcoEvent(
    val id: String,
    val title: LocalizedText,
    val description: LocalizedText,
    val location: LocalizedText,
    val city: String,
    val startAtEpochMillis: Long,
    val imageUrl: String?,
    val rewardPoints: Int,
    val isActive: Boolean = true,
)

interface EventsRepository {
    fun observeEvents(): Flow<List<EcoEvent>>
    fun observeRegisteredEventIds(userId: String): Flow<Set<String>>
    fun observeAttendedEventIds(userId: String): Flow<Set<String>>
    suspend fun getEvents(): List<EcoEvent>
    suspend fun getRegisteredEventIds(userId: String): Set<String>
    suspend fun registerForEvent(userId: String, eventId: String)
}
