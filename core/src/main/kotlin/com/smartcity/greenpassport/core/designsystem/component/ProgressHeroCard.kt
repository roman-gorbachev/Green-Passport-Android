package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.smartcity.greenpassport.core.R
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import com.smartcity.greenpassport.core.model.Level

private const val TRACK_ALPHA = 0.22f
private const val CAPTION_ALPHA = 0.8f

@Composable
fun ProgressHeroCard(
    points: Int,
    modifier: Modifier = Modifier,
    level: Level? = null,
    streakDays: Int = 0,
    onClick: (() -> Unit)? = null,
) {
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    GpSurfaceCard(
        onClick = onClick,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.ProgressHeroHeight),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                start = Dimens.SpacingLarge,
                end = Dimens.CardPadding,
                top = Dimens.CardPadding,
                bottom = Dimens.CardPadding,
            ),
        ) {
            ProgressSummary(
                points = points,
                level = level,
                streakDays = streakDays,
                modifier = Modifier.weight(1f),
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
                modifier = Modifier.padding(start = Dimens.SpacingSmall),
            ) {
                if (level != null) {
                    SpeechBubble(
                        text = stringResource(
                            R.string.xp_left_to_level,
                            level.xpForNextLevel - level.currentXp,
                            level.number + 1,
                        ),
                    )
                }
                MascotWidget(size = Dimens.ProgressHeroMascotSize)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProgressSummary(
    points: Int,
    level: Level?,
    streakDays: Int,
    modifier: Modifier = Modifier,
) {
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
        modifier = modifier,
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (level != null) {
                    stringResource(R.string.level, level.number)
                } else {
                    stringResource(R.string.your_balance)
                },
                style = MaterialTheme.typography.titleSmall,
                color = onPrimary.copy(alpha = CAPTION_ALPHA),
            )
            if (streakDays > 0) {
                StreakCapsule(days = streakDays)
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(Dimens.IconSizeMedium),
            )
            Text(
                text = stringResource(R.string.points_count, points),
                style = MaterialTheme.typography.headlineLarge,
                color = onPrimary,
            )
        }
        if (level != null) {
            LinearProgressIndicator(
                progress = { level.progressFraction() },
                color = MaterialTheme.colorScheme.secondary,
                trackColor = onPrimary.copy(alpha = TRACK_ALPHA),
                strokeCap = StrokeCap.Round,
                gapSize = Dimens.SpacingNone,
                drawStopIndicator = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.SpacingSmall)
                    .height(Dimens.ProgressBarHeight),
            )
        }
    }
}

private fun Level.progressFraction(): Float =
    if (xpForNextLevel > 0) currentXp.toFloat() / xpForNextLevel else 0f

@Composable
private fun StreakCapsule(
    days: Int,
    modifier: Modifier = Modifier,
) {
    val description = pluralStringResource(R.plurals.streak_days_in_row, days, days)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
        modifier = modifier
            .semantics(mergeDescendants = true) { contentDescription = description }
            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(Dimens.CornerRadiusPill))
            .padding(horizontal = Dimens.SpacingSmall, vertical = Dimens.SpacingHairline),
    ) {
        Icon(
            imageVector = Icons.Filled.LocalFireDepartment,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(Dimens.IconSizeExtraSmall),
        )
        Text(
            text = days.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondary,
        )
    }
}

@Composable
private fun SpeechBubble(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = modifier
            .widthIn(max = Dimens.SpeechBubbleMaxWidth)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(Dimens.CornerRadiusSmall),
            )
            .padding(horizontal = Dimens.SpacingSmall, vertical = Dimens.SpacingExtraSmall),
    )
}

@Preview
@Composable
private fun ProgressHeroCardPreview() {
    GreenPassportTheme {
        ProgressHeroCard(
            points = 500,
            level = Level(number = 3, currentXp = 800, xpForNextLevel = 1000),
            streakDays = 5,
        )
    }
}
