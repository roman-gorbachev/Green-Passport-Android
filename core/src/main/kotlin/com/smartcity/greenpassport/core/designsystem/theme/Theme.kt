package com.smartcity.greenpassport.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

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
