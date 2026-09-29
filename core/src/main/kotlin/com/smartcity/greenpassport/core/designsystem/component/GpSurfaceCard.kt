package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.smartcity.greenpassport.core.designsystem.theme.Dimens

@Composable
fun GpSurfaceCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    shadowElevation: Dp = Dimens.CardElevation,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (onClick == null) {
        Surface(modifier = modifier, shape = shape, color = color, shadowElevation = shadowElevation) {
            Column(content = content)
        }
    } else {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            color = color,
            shadowElevation = shadowElevation,
        ) {
            Column(content = content)
        }
    }
}
