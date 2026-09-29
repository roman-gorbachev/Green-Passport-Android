package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import com.smartcity.greenpassport.core.navigation.TopLevelDestination
import dev.chrisbanes.haze.HazeState

private const val RIM_ALPHA = 0.8f
private const val SHADOW_ALPHA = 0.08f

@Composable
fun GpBottomBar(
    selected: TopLevelDestination,
    onSelect: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
) {
    val shape = RoundedCornerShape(Dimens.CornerRadiusPill)
    val haptics = LocalHapticFeedback.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.BottomBarHeight)
            .shadow(
                elevation = Dimens.BottomBarElevation,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = SHADOW_ALPHA),
                spotColor = Color.Black.copy(alpha = SHADOW_ALPHA),
            )
            .glassSurface(hazeState = hazeState, shape = shape, color = MaterialTheme.colorScheme.background)
            .border(Dimens.BorderWidthThin, Color.White.copy(alpha = RIM_ALPHA), shape)
            .padding(Dimens.SpacingExtraSmall + Dimens.BorderWidthThin),
    ) {
        val tabWidth = maxWidth / TopLevelDestination.entries.size
        val indicatorOffset by animateDpAsState(
            targetValue = tabWidth * selected.ordinal,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow,
            ),
            label = "tabIndicatorOffset",
        )

        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .fillMaxHeight()
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        )

        Row(modifier = Modifier.fillMaxSize()) {
            TopLevelDestination.entries.forEach { tab ->
                BottomBarItem(
                    tab = tab,
                    isSelected = tab == selected,
                    onClick = {
                        if (tab != selected) {
                            haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        }
                        onSelect(tab)
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    tab: TopLevelDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
        label = "tabTint",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
            .clickable(interactionSource = null, indication = null, onClick = onClick)
            .semantics {
                role = Role.Tab
                this.selected = isSelected
            },
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = stringResource(tab.labelRes),
            tint = tint,
            modifier = Modifier.size(Dimens.IconSizeMedium),
        )
    }
}

@Preview
@Composable
private fun GpBottomBarPreview() {
    GreenPassportTheme {
        GpBottomBar(selected = TopLevelDestination.HOME, onSelect = {})
    }
}
