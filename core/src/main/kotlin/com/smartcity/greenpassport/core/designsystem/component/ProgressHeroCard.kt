package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
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
) {
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    GpSurfaceCard(
        shape = RoundedCornerShape(Dimens.CornerRadiusExtraLarge),
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (level != null) {
                        stringResource(R.string.level, level.number)
                    } else {
                        stringResource(R.string.your_balance)
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = onPrimary.copy(alpha = CAPTION_ALPHA),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = Dimens.SpacingExtraSmall),
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
                        modifier = Modifier.padding(start = Dimens.SpacingExtraSmall),
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
                            .padding(top = Dimens.SpacingMedium)
                            .height(Dimens.ProgressBarHeight),
                    )
                    Text(
                        text = stringResource(R.string.xp_progress, level.currentXp, level.xpForNextLevel),
                        style = MaterialTheme.typography.labelSmall,
                        color = onPrimary.copy(alpha = CAPTION_ALPHA),
                        modifier = Modifier.padding(top = Dimens.SpacingExtraSmall),
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
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

private fun Level.progressFraction(): Float =
    if (xpForNextLevel > 0) currentXp.toFloat() / xpForNextLevel else 0f

@Composable
private fun SpeechBubble(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        modifier = modifier
            .widthIn(max = Dimens.SpeechBubbleMaxWidth)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = RoundedCornerShape(Dimens.CornerRadiusSmall),
            )
            .padding(horizontal = Dimens.SpacingSmall, vertical = Dimens.SpacingExtraSmall),
    )
}

@Preview
@Composable
private fun ProgressHeroCardPreview() {
    GreenPassportTheme {
        ProgressHeroCard(points = 500, level = Level(number = 3, currentXp = 800, xpForNextLevel = 1000))
    }
}
