package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import kotlin.math.min

private val MascotBodyColor = Color(0xFF6FA954)
private val MascotHornColor = Color(0xFFE0A26E)
private val MascotFaceColor = Color(0xFFFFFFFF)
private val MascotEyeColor = Color(0xFF1B2A20)

@Composable
fun MascotWidget(
    modifier: Modifier = Modifier,
    size: Dp = Dimens.MascotSizeMedium,
    artwork: Painter? = null,
) {
    if (artwork != null) {
        Image(
            painter = artwork,
            contentDescription = null,
            modifier = modifier.size(size),
        )
        return
    }

    Canvas(modifier = modifier.size(size)) {
        val radius = min(this.size.width, this.size.height) / 2f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        val hornRadius = radius * 0.22f
        drawCircle(
            color = MascotHornColor,
            radius = hornRadius,
            center = center + Offset(-radius * 0.55f, -radius * 0.75f),
        )
        drawCircle(
            color = MascotHornColor,
            radius = hornRadius,
            center = center + Offset(radius * 0.55f, -radius * 0.75f),
        )

        drawCircle(color = MascotBodyColor, radius = radius, center = center)

        val faceRadius = radius * 0.55f
        drawCircle(
            color = MascotFaceColor,
            radius = faceRadius,
            center = center + Offset(0f, radius * 0.08f),
        )

        val eyeRadius = faceRadius * 0.14f
        val eyeOffsetX = faceRadius * 0.4f
        val eyeOffsetY = radius * 0.02f
        drawCircle(
            color = MascotEyeColor,
            radius = eyeRadius,
            center = center + Offset(-eyeOffsetX, eyeOffsetY),
        )
        drawCircle(
            color = MascotEyeColor,
            radius = eyeRadius,
            center = center + Offset(eyeOffsetX, eyeOffsetY),
        )
    }
}

@Preview
@Composable
private fun MascotWidgetPreview() {
    GreenPassportTheme {
        MascotWidget(size = 96.dp)
    }
}
