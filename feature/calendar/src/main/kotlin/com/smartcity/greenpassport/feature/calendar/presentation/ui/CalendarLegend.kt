package com.smartcity.greenpassport.feature.calendar.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.feature.calendar.R

@Composable
fun CalendarLegend(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium, Alignment.CenterHorizontally),
        modifier = modifier.fillMaxWidth(),
    ) {
        CalendarLegendItem(
            title = stringResource(R.string.not_registered),
            color = MaterialTheme.colorScheme.error,
        )
        CalendarLegendItem(
            title = stringResource(R.string.registered),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun CalendarLegendItem(title: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.CalendarLegendDotSize)
                .background(color, CircleShape),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
