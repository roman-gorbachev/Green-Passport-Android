package com.smartcity.greenpassport.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val GreenPrimaryLight = Color(0xFF2E7D32)
private val OnGreenPrimaryLight = Color(0xFFFFFFFF)
private val GreenPrimaryContainerLight = Color(0xFFC8E6C9)
private val OnGreenPrimaryContainerLight = Color(0xFF1B5E20)
private val GoldSecondaryLight = Color(0xFFB8892F)
private val OnGoldSecondaryLight = Color(0xFFFFFFFF)
private val GoldSecondaryContainerLight = Color(0xFFF3E7CC)
private val OnGoldSecondaryContainerLight = Color(0xFF6B4E1D)
private val TealTertiaryLight = Color(0xFF2A6F77)
private val OnTealTertiaryLight = Color(0xFFFFFFFF)
private val BackgroundLight = Color(0xFFF6FBF3)
private val OnBackgroundLight = Color(0xFF1B2A20)
private val SurfaceLight = Color(0xFFFFFFFF)
private val OnSurfaceLight = Color(0xFF1B2A20)
private val ErrorLight = Color(0xFFB3261E)
private val OnErrorLight = Color(0xFFFFFFFF)

private val GreenPrimaryDark = Color(0xFF81C995)
private val OnGreenPrimaryDark = Color(0xFF0B3B14)
private val GreenPrimaryContainerDark = Color(0xFF1E4620)
private val OnGreenPrimaryContainerDark = Color(0xFFC8E6C9)
private val GoldSecondaryDark = Color(0xFFDFB15C)
private val OnGoldSecondaryDark = Color(0xFF3B2A08)
private val GoldSecondaryContainerDark = Color(0xFF4E3A12)
private val OnGoldSecondaryContainerDark = Color(0xFFF3E7CC)
private val TealTertiaryDark = Color(0xFF63B7BE)
private val OnTealTertiaryDark = Color(0xFF07333A)
private val BackgroundDark = Color(0xFF10160F)
private val OnBackgroundDark = Color(0xFFE7EFE3)
private val SurfaceDark = Color(0xFF182018)
private val OnSurfaceDark = Color(0xFFE7EFE3)
private val ErrorDark = Color(0xFFF2B8B5)
private val OnErrorDark = Color(0xFF601410)

val GreenPassportLightColorScheme = lightColorScheme(
    primary = GreenPrimaryLight,
    onPrimary = OnGreenPrimaryLight,
    primaryContainer = GreenPrimaryContainerLight,
    onPrimaryContainer = OnGreenPrimaryContainerLight,
    secondary = GoldSecondaryLight,
    onSecondary = OnGoldSecondaryLight,
    secondaryContainer = GoldSecondaryContainerLight,
    onSecondaryContainer = OnGoldSecondaryContainerLight,
    tertiary = TealTertiaryLight,
    onTertiary = OnTealTertiaryLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    error = ErrorLight,
    onError = OnErrorLight,
)

val GreenPassportDarkColorScheme = darkColorScheme(
    primary = GreenPrimaryDark,
    onPrimary = OnGreenPrimaryDark,
    primaryContainer = GreenPrimaryContainerDark,
    onPrimaryContainer = OnGreenPrimaryContainerDark,
    secondary = GoldSecondaryDark,
    onSecondary = OnGoldSecondaryDark,
    secondaryContainer = GoldSecondaryContainerDark,
    onSecondaryContainer = OnGoldSecondaryContainerDark,
    tertiary = TealTertiaryDark,
    onTertiary = OnTealTertiaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    error = ErrorDark,
    onError = OnErrorDark,
)

data class RewardTierColors(
    val teal: Color,
    val coral: Color,
    val green: Color,
)

val LightRewardTierColors = RewardTierColors(
    teal = Color(0xFF2A9DA6),
    coral = Color(0xFFE07A5F),
    green = Color(0xFF6FA954),
)

val DarkRewardTierColors = RewardTierColors(
    teal = Color(0xFF63B7BE),
    coral = Color(0xFFE8A088),
    green = Color(0xFF9CCB84),
)
