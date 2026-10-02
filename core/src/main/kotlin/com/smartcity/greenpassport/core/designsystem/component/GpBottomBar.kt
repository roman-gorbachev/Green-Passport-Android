package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import com.smartcity.greenpassport.core.navigation.TopLevelDestination
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.blur.materials.HazeMaterials

private const val SHADOW_ALPHA = 0.14f
private const val BORDER_ALPHA = 0.6f
private const val INDICATOR_ALPHA = 0.14f

@Composable
fun GpBottomBar(
    selected: TopLevelDestination,
    onSelect: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
) {
    val shape = RoundedCornerShape(Dimens.CornerRadiusPill)
    val container = MaterialTheme.colorScheme.surfaceContainer

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.BottomBarHeight)
            .shadow(
                elevation = Dimens.BottomBarElevation,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = SHADOW_ALPHA),
                spotColor = Color.Black.copy(alpha = SHADOW_ALPHA),
            )
            .clip(shape)
            .then(
                if (hazeState != null) {
                    Modifier.hazeBlur(
                        input = HazeInput.Sources(hazeState),
                        style = HazeMaterials.thin(containerColor = container),
                    )
                } else {
                    Modifier.background(container)
                },
            )
            .border(
                width = Dimens.BorderWidthThin,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = BORDER_ALPHA),
                shape = shape,
            )
            .padding(Dimens.SpacingExtraSmall),
    ) {
        TopLevelDestination.entries.forEach { tab ->
            BottomBarItem(
                tab = tab,
                isSelected = tab == selected,
                onClick = { onSelect(tab) },
                modifier = Modifier.weight(1f),
            )
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
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "bottomBarItemColor",
    )
    val indicatorColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary.copy(alpha = INDICATOR_ALPHA)
        } else {
            Color.Transparent
        },
        label = "bottomBarIndicatorColor",
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingHairline, Alignment.CenterVertically),
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(Dimens.CornerRadiusPill))
            .background(indicatorColor)
            .clickable(interactionSource = null, indication = null, onClick = onClick)
            .semantics(mergeDescendants = true) {
                role = Role.Tab
                this.selected = isSelected
            },
    ) {
        Icon(
            imageVector = if (isSelected) tab.icon else tab.unselectedIcon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(Dimens.IconSizeMedium),
        )
        Text(
            text = stringResource(tab.labelRes),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
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
