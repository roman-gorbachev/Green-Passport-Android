package com.smartcity.greenpassport.core.model

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

data class Streak(
    val count: Int,
    val lastDay: String,
) {
    fun currentCount(nowEpochMillis: Long): Int {
        val today = today(nowEpochMillis)
        val days = setOf(today.format(DAY_FORMAT), today.minusDays(1).format(DAY_FORMAT))
        return if (lastDay in days) count else 0
    }

    fun isCounted(nowEpochMillis: Long): Boolean = lastDay == today(nowEpochMillis).format(DAY_FORMAT)

    companion object {
        private val SERVER_ZONE: ZoneId = ZoneId.of("Europe/Minsk")
        private val DAY_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        private const val BONUS_PERIOD = 7
        private const val DAYS_IN_WEEK = 7L

        fun summary(streak: Streak?, nowEpochMillis: Long, firstDayOfWeek: DayOfWeek): StreakSummary {
            val today = today(nowEpochMillis)
            val days = streak?.currentCount(nowEpochMillis) ?: 0
            val isTodayCounted = streak?.isCounted(nowEpochMillis) ?: false
            val activeDays = activeDays(streak, days)
            val weekStart = today.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
            val week = (0 until DAYS_IN_WEEK).map { offset ->
                val date = weekStart.plusDays(offset)
                StreakWeekDay(date = date, isActive = date in activeDays, isToday = date == today)
            }
            return StreakSummary(
                days = days,
                isTodayCounted = isTodayCounted,
                daysUntilBonus = daysUntilBonus(days, isTodayCounted),
                week = week,
            )
        }

        private fun today(nowEpochMillis: Long): LocalDate =
            Instant.ofEpochMilli(nowEpochMillis).atZone(SERVER_ZONE).toLocalDate()

        private fun activeDays(streak: Streak?, days: Int): Set<LocalDate> {
            val lastDay = streak?.lastDay
                ?.let { runCatching { LocalDate.parse(it, DAY_FORMAT) }.getOrNull() }
                ?: return emptySet()
            return (0 until days).map { lastDay.minusDays(it.toLong()) }.toSet()
        }

        private fun daysUntilBonus(days: Int, isTodayCounted: Boolean): Int {
            val nextBonusDay = (days / BONUS_PERIOD + 1) * BONUS_PERIOD
            val todayNumber = if (isTodayCounted) days else days + 1
            return nextBonusDay - todayNumber
        }
    }
}
