package com.smartcity.greenpassport.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.StreakSummary
import com.smartcity.greenpassport.core.model.StreakWeekDay
import com.smartcity.greenpassport.feature.home.R
import java.time.format.TextStyle
import com.smartcity.greenpassport.core.R as CoreR

private const val FLAME_FRACTION = 0.45f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakSheet(
    summary: StreakSummary,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.ScreenHorizontalPadding)
                .padding(bottom = Dimens.SpacingLarge),
        ) {
            StreakHeadline(days = summary.days)
            StreakWeekStrip(week = summary.week)
            StreakStatus(summary = summary)
        }
    }
}

@Composable
private fun StreakHeadline(days: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
    ) {
        Icon(
            imageVector = Icons.Filled.LocalFireDepartment,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(Dimens.IconSizeMedium),
        )
        Text(
            text = pluralStringResource(CoreR.plurals.streak_days_in_row, days, days),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun StreakWeekStrip(week: List<StreakWeekDay>) {
    val locale = LocalLocale.current.platformLocale
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        modifier = Modifier.fillMaxWidth(),
    ) {
        week.forEach { day ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
                modifier = Modifier
                    .weight(1f)
                    .semantics(mergeDescendants = true) {},
            ) {
                Text(
                    text = day.date.dayOfWeek.getDisplayName(TextStyle.NARROW_STANDALONE, locale),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (day.isToday) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
                StreakDayCircle(day = day)
            }
        }
    }
}

@Composable
private fun StreakDayCircle(day: StreakWeekDay) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(Dimens.CalendarDaySize)
            .clip(CircleShape)
            .background(
                if (day.isActive) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                },
            )
            .then(
                if (day.isToday) {
                    Modifier.border(Dimens.BorderWidthMedium, MaterialTheme.colorScheme.primary, CircleShape)
                } else {
                    Modifier
                },
            ),
    ) {
        if (day.isActive) {
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.size(Dimens.CalendarDaySize * FLAME_FRACTION),
            )
        } else {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun StreakStatus(summary: StreakSummary) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
    ) {
        Text(
            text = stringResource(
                if (summary.isTodayCounted) R.string.streak_today_counted else R.string.streak_today_pending,
            ),
            style = MaterialTheme.typography.titleMedium,
            color = if (summary.isTodayCounted) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.Center,
        )
        Text(
            text = if (summary.daysUntilBonus == 0) {
                stringResource(R.string.streak_bonus_today)
            } else {
                pluralStringResource(CoreR.plurals.streak_bonus_in_days, summary.daysUntilBonus, summary.daysUntilBonus)
            },
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.streak_rule),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Dimens.SpacingSmall),
        )
    }
}
