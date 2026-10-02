package com.smartcity.greenpassport.core.model

enum class HistoryEntryType {
    TASK_COMPLETED,
    EVENT_ATTENDED,
    REWARD_REDEEMED,
}

data class HistoryEntry(
    val id: String,
    val type: HistoryEntryType,
    val title: LocalizedText,
    val timestampEpochMillis: Long,
)

interface HistoryRepository {
    suspend fun getHistory(userId: String): List<HistoryEntry>
}
