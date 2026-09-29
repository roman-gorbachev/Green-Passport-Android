package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import com.smartcity.greenpassport.core.navigation.TopLevelDestination

private const val BAR_SURFACE_ALPHA = 0.92f

@Composable
fun GpBottomBar(
    selected: TopLevelDestination,
    onSelect: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(Dimens.CornerRadiusPill),
        color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = BAR_SURFACE_ALPHA),
        shadowElevation = Dimens.BottomBarElevation,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.BottomBarHeight),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SpacingSmall),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
            verticalAlignment = Alignment.CenterVertically,
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
}

@Composable
private fun BottomBarItem(
    tab: TopLevelDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.CornerRadiusPill),
        color = if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent,
        modifier = modifier
            .fillMaxHeight()
            .semantics {
                role = Role.Tab
                this.selected = isSelected
            },
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = tab.icon,
                contentDescription = stringResource(tab.labelRes),
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(Dimens.IconSizeMedium + Dimens.SpacingExtraSmall),
            )
        }
    }
}

@Preview
@Composable
private fun GpBottomBarPreview() {
    GreenPassportTheme {
        GpBottomBar(selected = TopLevelDestination.HOME, onSelect = {})
    }
}
