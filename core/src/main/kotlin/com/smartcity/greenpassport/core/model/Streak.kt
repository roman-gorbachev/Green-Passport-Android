package com.smartcity.greenpassport.core.model

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class Streak(
    val count: Int,
    val lastDay: String,
) {
    fun currentCount(nowEpochMillis: Long): Int {
        val today = Instant.ofEpochMilli(nowEpochMillis).atZone(SERVER_ZONE).toLocalDate()
        val days = setOf(today.format(DAY_FORMAT), today.minusDays(1).format(DAY_FORMAT))
        return if (lastDay in days) count else 0
    }

    companion object {
        private val SERVER_ZONE: ZoneId = ZoneId.of("Europe/Minsk")
        private val DAY_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    }
}
