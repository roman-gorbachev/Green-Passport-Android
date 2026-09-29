package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur

private const val GLASS_TINT_ALPHA = 0.55f
private const val GLASS_FALLBACK_ALPHA = 0.92f

fun Modifier.glassSurface(
    hazeState: HazeState?,
    shape: Shape,
    color: Color,
): Modifier {
    val clipped = clip(shape)
    if (hazeState == null) return clipped.background(color.copy(alpha = GLASS_FALLBACK_ALPHA))
    val style = HazeBlurStyle {
        blurRadius(Dimens.GlassBlurRadius)
        colorEffects(listOf(HazeColorEffect.tint(color.copy(alpha = GLASS_TINT_ALPHA))))
        fallbackColorEffect(HazeColorEffect.tint(color.copy(alpha = GLASS_FALLBACK_ALPHA)))
    }
    return clipped.hazeBlur(input = HazeInput.Sources(hazeState), style = style)
}
