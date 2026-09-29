package com.smartcity.greenpassport.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val MintBackgroundLight = Color(0xFFF1FEF5)
private val LeafSurfaceLight = Color(0xFFD6F1E0)
private val LeafSurfaceVariantLight = Color(0xFFC9E6D3)
private val InkLight = Color(0xFF0B0F10)
private val MossLight = Color(0xFF385247)
private val BarkLight = Color(0xFF947259)
private val OnBarkLight = Color(0xFFFFFFFF)
private val TrackLight = Color(0xFFC5DCD2)
private val MossSecondaryLight = Color(0xFF526959)
private val PebbleLight = Color(0xFFD1DCD6)
private val ErrorLight = Color(0xFFB3261E)
private val OnErrorLight = Color(0xFFFFFFFF)

private val MintBackgroundDark = Color(0xFF121815)
private val LeafSurfaceDark = Color(0xFF1E2A24)
private val LeafSurfaceVariantDark = Color(0xFF28372F)
private val InkDark = Color(0xFFE4EEE7)
private val MossDark = Color(0xFFA9C2B7)
private val BarkDark = Color(0xFFC39A7C)
private val OnBarkDark = Color(0xFF2E1A0D)
private val TrackDark = Color(0xFF34443B)
private val MossSecondaryDark = Color(0xFF8AA398)
private val PebbleDark = Color(0xFF3A4540)
private val ErrorDark = Color(0xFFF2B8B5)
private val OnErrorDark = Color(0xFF601410)

val GreenPassportLightColorScheme = lightColorScheme(
    primary = BarkLight,
    onPrimary = OnBarkLight,
    primaryContainer = LeafSurfaceVariantLight,
    onPrimaryContainer = InkLight,
    secondary = MossLight,
    onSecondary = OnBarkLight,
    secondaryContainer = LeafSurfaceLight,
    onSecondaryContainer = MossLight,
    tertiary = MossLight,
    onTertiary = OnBarkLight,
    background = MintBackgroundLight,
    onBackground = InkLight,
    surface = MintBackgroundLight,
    onSurface = InkLight,
    surfaceVariant = LeafSurfaceLight,
    onSurfaceVariant = MossLight,
    surfaceContainerLowest = MintBackgroundLight,
    surfaceContainerLow = LeafSurfaceLight,
    surfaceContainer = LeafSurfaceLight,
    surfaceContainerHigh = LeafSurfaceVariantLight,
    surfaceContainerHighest = LeafSurfaceVariantLight,
    outline = MossSecondaryLight,
    surfaceDim = PebbleLight,
    outlineVariant = TrackLight,
    error = ErrorLight,
    onError = OnErrorLight,
)

val GreenPassportDarkColorScheme = darkColorScheme(
    primary = BarkDark,
    onPrimary = OnBarkDark,
    primaryContainer = LeafSurfaceVariantDark,
    onPrimaryContainer = InkDark,
    secondary = MossDark,
    onSecondary = OnBarkDark,
    secondaryContainer = LeafSurfaceDark,
    onSecondaryContainer = MossDark,
    tertiary = MossDark,
    onTertiary = OnBarkDark,
    background = MintBackgroundDark,
    onBackground = InkDark,
    surface = MintBackgroundDark,
    onSurface = InkDark,
    surfaceVariant = LeafSurfaceDark,
    onSurfaceVariant = MossDark,
    surfaceContainerLowest = MintBackgroundDark,
    surfaceContainerLow = LeafSurfaceDark,
    surfaceContainer = LeafSurfaceDark,
    surfaceContainerHigh = LeafSurfaceVariantDark,
    surfaceContainerHighest = LeafSurfaceVariantDark,
    outline = MossSecondaryDark,
    surfaceDim = PebbleDark,
    outlineVariant = TrackDark,
    error = ErrorDark,
    onError = OnErrorDark,
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
