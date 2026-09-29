package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.smartcity.greenpassport.core.R
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme

@Composable
fun LevelProgressCard(
    level: Int,
    currentXp: Int,
    xpForNextLevel: Int,
    modifier: Modifier = Modifier,
) {
    GpSurfaceCard(
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.LevelCardHeight),
    ) {
        Row(
            modifier = Modifier.padding(
                start = Dimens.CardPadding,
                end = Dimens.CardPadding,
                top = Dimens.SpacingSmall,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Eco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Dimens.IconSizeSmall),
            )
            Text(
                text = stringResource(R.string.level_number, level),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = Dimens.SpacingSmall),
            )
            Text(
                text = stringResource(R.string.xp_progress, currentXp, xpForNextLevel),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        LinearProgressIndicator(
            progress = { if (xpForNextLevel > 0) currentXp.toFloat() / xpForNextLevel else 0f },
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outlineVariant,
            strokeCap = StrokeCap.Round,
            gapSize = Dimens.SpacingExtraSmall,
            drawStopIndicator = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = Dimens.CardPadding,
                    end = Dimens.CardPadding,
                    top = Dimens.SpacingExtraSmall,
                    bottom = Dimens.SpacingSmall + Dimens.SpacingExtraSmall,
                )
                .height(Dimens.ProgressBarHeight),
        )
    }
}

@Preview
@Composable
private fun LevelProgressCardPreview() {
    GreenPassportTheme {
        LevelProgressCard(level = 3, currentXp = 500, xpForNextLevel = 700)
    }
}
