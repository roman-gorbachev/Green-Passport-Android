package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpSurfaceCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (onClick == null) {
        Surface(modifier = modifier, shape = shape, color = color) {
            Column(content = content)
        }
        return
    }

    val interactionSource = remember { MutableInteractionSource() }
    val scale by rememberPressScale(interactionSource)

    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        Surface(
            onClick = onClick,
            interactionSource = interactionSource,
            shape = shape,
            color = color,
            modifier = modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        ) {
            Column(content = content)
        }
    }
}
