package com.smartcity.greenpassport.core.designsystem.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LocalRewardTierColors = staticCompositionLocalOf { LightRewardTierColors }
private val LocalSectionColors = staticCompositionLocalOf { LightSectionColors }

object GreenPassportTheme {
    val rewardTierColors: RewardTierColors
        @Composable
        get() = LocalRewardTierColors.current

    val sectionColors: SectionColors
        @Composable
        get() = LocalSectionColors.current
}

@Composable
fun GreenPassportTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) GreenPassportDarkColorScheme else GreenPassportLightColorScheme
    val rewardTierColors = if (darkTheme) DarkRewardTierColors else LightRewardTierColors
    val sectionColors = if (darkTheme) DarkSectionColors else LightSectionColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    CompositionLocalProvider(
        LocalRewardTierColors provides rewardTierColors,
        LocalSectionColors provides sectionColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = GreenPassportTypography,
            shapes = GreenPassportShapes,
            content = content,
        )
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
