package com.smartcity.greenpassport.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val MintBackgroundLight = Color(0xFFEAF6EC)
private val LeafSurfaceLight = Color(0xFFD5EDDA)
private val LeafSurfaceVariantLight = Color(0xFFC6E3CD)
private val InkLight = Color(0xFF1B1F1C)
private val MossLight = Color(0xFF4E6A61)
private val BarkLight = Color(0xFF93674A)
private val OnBarkLight = Color(0xFFFFFFFF)
private val TrackLight = Color(0xFFDCE3DE)
private val OutlineLight = Color(0xFF9DB3A8)
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
private val OutlineDark = Color(0xFF5E7268)
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
    outline = OutlineLight,
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
    outline = OutlineDark,
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
