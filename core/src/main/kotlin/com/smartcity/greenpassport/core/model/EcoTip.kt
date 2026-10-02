package com.smartcity.greenpassport.core.model

import kotlinx.coroutines.flow.Flow

enum class EcoTipCategory {
    ARTICLE,
    VIDEO,
    KIDS,
}

data class EcoTip(
    val id: String,
    val category: EcoTipCategory,
    val title: LocalizedText,
    val body: LocalizedText,
    val mediaUrl: String?,
    val imageUrl: String?,
    val isDailyTip: Boolean,
    val rewardPoints: Int,
    val rewardXp: Int,
)

interface EcoTipsRepository {
    fun observeTips(): Flow<List<EcoTip>>
    fun observeReadTipIds(userId: String): Flow<Set<String>>
    suspend fun getTips(): List<EcoTip>
    suspend fun getReadTipIds(userId: String): Set<String>
}
