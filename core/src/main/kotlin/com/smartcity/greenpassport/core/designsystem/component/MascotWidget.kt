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

private const val HORN_RADIUS_RATIO = 0.22f
private const val HORN_OFFSET_X_RATIO = 0.55f
private const val HORN_OFFSET_Y_RATIO = 0.75f
private const val FACE_RADIUS_RATIO = 0.55f
private const val FACE_OFFSET_Y_RATIO = 0.08f
private const val EYE_RADIUS_RATIO = 0.14f
private const val EYE_OFFSET_X_RATIO = 0.4f
private const val EYE_OFFSET_Y_RATIO = 0.02f

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

        val hornRadius = radius * HORN_RADIUS_RATIO
        drawCircle(
            color = MascotHornColor,
            radius = hornRadius,
            center = center + Offset(-radius * HORN_OFFSET_X_RATIO, -radius * HORN_OFFSET_Y_RATIO),
        )
        drawCircle(
            color = MascotHornColor,
            radius = hornRadius,
            center = center + Offset(radius * HORN_OFFSET_X_RATIO, -radius * HORN_OFFSET_Y_RATIO),
        )

        drawCircle(color = MascotBodyColor, radius = radius, center = center)

        val faceRadius = radius * FACE_RADIUS_RATIO
        drawCircle(
            color = MascotFaceColor,
            radius = faceRadius,
            center = center + Offset(0f, radius * FACE_OFFSET_Y_RATIO),
        )

        val eyeRadius = faceRadius * EYE_RADIUS_RATIO
        val eyeOffsetX = faceRadius * EYE_OFFSET_X_RATIO
        val eyeOffsetY = radius * EYE_OFFSET_Y_RATIO
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
