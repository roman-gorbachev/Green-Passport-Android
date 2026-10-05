package com.smartcity.greenpassport.feature.calendar.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import com.smartcity.greenpassport.feature.calendar.R
import com.smartcity.greenpassport.feature.calendar.presentation.state.DayEventCounts
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

private const val DAYS_IN_WEEK = 7
private const val MONTH_TITLE_PATTERN = "LLLL yyyy"

@Composable
fun MonthCalendar(
    month: YearMonth,
    selectedDay: LocalDate,
    dayCounts: Map<LocalDate, DayEventCounts>,
    onMonthChange: (YearMonth) -> Unit,
    onDaySelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    val firstDayOfWeek = remember(locale) { WeekFields.of(locale).firstDayOfWeek }
    val weeks = remember(month, firstDayOfWeek) { monthWeeks(month, firstDayOfWeek) }
    val today = LocalDate.now()

    Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall), modifier = modifier.fillMaxWidth()) {
        MonthHeader(month = month, locale = locale, onMonthChange = onMonthChange)
        WeekdayRow(firstDayOfWeek = firstDayOfWeek, locale = locale)
        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.weight(1f)) {
                        if (day != null) {
                            DayCell(
                                day = day,
                                isSelected = day == selectedDay,
                                isToday = day == today,
                                counts = dayCounts[day],
                                onClick = { onDaySelected(day) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(month: YearMonth, locale: Locale, onMonthChange: (YearMonth) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = month.format(DateTimeFormatter.ofPattern(MONTH_TITLE_PATTERN, locale))
                .replaceFirstChar { it.titlecase(locale) },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(start = Dimens.SpacingSmall),
        )
        IconButton(onClick = { onMonthChange(month.minusMonths(1)) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.calendar_previous_month),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        IconButton(onClick = { onMonthChange(month.plusMonths(1)) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.calendar_next_month),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun WeekdayRow(firstDayOfWeek: DayOfWeek, locale: Locale) {
    Row(modifier = Modifier.fillMaxWidth()) {
        repeat(DAYS_IN_WEEK) { offset ->
            Text(
                text = firstDayOfWeek.plus(offset.toLong()).getDisplayName(TextStyle.SHORT_STANDALONE, locale),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun DayCell(
    day: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    counts: DayEventCounts?,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingHairline),
        modifier = Modifier
            .clip(RoundedCornerShape(Dimens.CornerRadiusSmall))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = Dimens.SpacingExtraSmall),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(Dimens.CalendarDaySize)
                .clip(CircleShape)
                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                .then(
                    if (isToday && !isSelected) {
                        Modifier.border(Dimens.BorderWidthThin, MaterialTheme.colorScheme.primary, CircleShape)
                    } else {
                        Modifier
                    },
                ),
        ) {
            Text(
                text = day.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            )
        }
        EventCountBadges(counts)
    }
}

@Composable
private fun EventCountBadges(counts: DayEventCounts?) {
    if (counts == null) {
        Spacer(modifier = Modifier.height(Dimens.CalendarBadgeMinSize))
        return
    }
    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingHairline)) {
        if (counts.open > 0) {
            EventCountBadge(
                count = counts.open,
                color = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            )
        }
        if (counts.registered > 0) {
            EventCountBadge(
                count = counts.registered,
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun EventCountBadge(count: Int, color: Color, contentColor: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .defaultMinSize(minWidth = Dimens.CalendarBadgeMinSize, minHeight = Dimens.CalendarBadgeMinSize)
            .clip(CircleShape)
            .background(color)
            .padding(horizontal = Dimens.SpacingExtraSmall),
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
        )
    }
}

private fun monthWeeks(month: YearMonth, firstDayOfWeek: DayOfWeek): List<List<LocalDate?>> {
    val leadingBlanks = (month.atDay(1).dayOfWeek.value - firstDayOfWeek.value + DAYS_IN_WEEK) % DAYS_IN_WEEK
    val cells = List<LocalDate?>(leadingBlanks) { null } + (1..month.lengthOfMonth()).map(month::atDay)
    val trailingBlanks = (DAYS_IN_WEEK - cells.size % DAYS_IN_WEEK) % DAYS_IN_WEEK
    return (cells + List<LocalDate?>(trailingBlanks) { null }).chunked(DAYS_IN_WEEK)
}

@Preview
@Composable
private fun MonthCalendarPreview() {
    GreenPassportTheme {
        val today = LocalDate.now()
        MonthCalendar(
            month = YearMonth.from(today),
            selectedDay = today,
            dayCounts = mapOf(
                today to DayEventCounts(open = 1, registered = 0),
                today.plusDays(2) to DayEventCounts(open = 2, registered = 1)
            ),
            onMonthChange = {},
            onDaySelected = {},
        )
    }
}
