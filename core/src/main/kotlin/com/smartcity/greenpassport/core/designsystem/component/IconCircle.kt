package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme

@Composable
fun IconCircle(
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = Dimens.IconCircleSize,
    contentColor: Color = Color.White,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .shadow(elevation = Dimens.CardElevation, shape = CircleShape)
            .background(color = color, shape = CircleShape),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(size / ICON_TO_CIRCLE_RATIO),
        )
    }
}

private const val ICON_TO_CIRCLE_RATIO = 2

@Preview
@Composable
private fun IconCirclePreview() {
    GreenPassportTheme {
        IconCircle(icon = Icons.Filled.SportsEsports, color = GreenPassportTheme.sectionColors.games)
    }
}
