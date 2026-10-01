package com.smartcity.greenpassport.feature.auth.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartcity.greenpassport.core.designsystem.theme.Dimens

private val DotSize = 8.dp
private val ActiveDotWidth = 24.dp

@Composable
fun StepIndicator(
    stepCount: Int,
    currentStep: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        modifier = modifier,
    ) {
        repeat(stepCount) { index ->
            val width by animateDpAsState(
                targetValue = if (index == currentStep) ActiveDotWidth else DotSize,
                label = "stepDotWidth",
            )
            val color by animateColorAsState(
                targetValue = if (index <= currentStep) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                label = "stepDotColor",
            )
            Box(
                modifier = Modifier
                    .height(DotSize)
                    .width(width)
                    .background(color = color, shape = RoundedCornerShape(Dimens.CornerRadiusPill)),
            )
        }
    }
}
