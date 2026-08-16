package com.smartcity.greenpassport.core.model

enum class EcoTipCategory {
    ARTICLE,
    VIDEO,
    KIDS,
}

data class EcoTip(
    val id: String,
    val category: EcoTipCategory,
    val title: String,
    val body: String,
    val mediaUrl: String?,
    val isDailyTip: Boolean,
    val rewardPoints: Int,
    val rewardXp: Int,
)

interface EcoTipsRepository {
    suspend fun getTips(): List<EcoTip>
    suspend fun getReadTipIds(userId: String): Set<String>
    suspend fun markTipRead(userId: String, tipId: String)
}
