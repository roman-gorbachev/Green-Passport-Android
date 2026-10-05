package com.smartcity.greenpassport.feature.calendar.presentation.state

import com.smartcity.greenpassport.core.common.eventDay
import com.smartcity.greenpassport.core.model.EcoEvent
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val events: List<EcoEvent> = emptyList(),
    val registeredEventIds: Set<String> = emptySet(),
    val selectedDay: LocalDate = LocalDate.now(),
    val visibleMonth: YearMonth = YearMonth.now(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
) {
    val dayCounts: Map<LocalDate, DayEventCounts>
        get() = events.groupBy { eventDay(it.startAtEpochMillis) }.mapValues { (_, dayEvents) ->
            val registered = dayEvents.count { it.id in registeredEventIds }
            DayEventCounts(open = dayEvents.size - registered, registered = registered)
        }

    val dayEvents: List<EcoEvent>
        get() = events.filter { eventDay(it.startAtEpochMillis) == selectedDay }
}
