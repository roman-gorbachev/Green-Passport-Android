package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import com.smartcity.greenpassport.core.model.profile.AvatarStyle

private const val MASCOT_TO_AVATAR_RATIO = 0.78f

@Composable
fun ProfileAvatar(
    style: AvatarStyle,
    modifier: Modifier = Modifier,
    size: Dp = Dimens.AvatarSize,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .background(color = avatarColor(style), shape = CircleShape),
    ) {
        MascotWidget(size = size * MASCOT_TO_AVATAR_RATIO)
    }
}

@Composable
fun avatarColor(style: AvatarStyle): Color {
    val sectionColors = GreenPassportTheme.sectionColors
    return when (style) {
        AvatarStyle.LIME -> MaterialTheme.colorScheme.secondary
        AvatarStyle.FOREST -> MaterialTheme.colorScheme.primary
        AvatarStyle.SKY -> sectionColors.calendar
        AvatarStyle.SUNSET -> sectionColors.tips
        AvatarStyle.BERRY -> sectionColors.feedback
        AvatarStyle.VIOLET -> sectionColors.games
    }
}

@Preview
@Composable
private fun ProfileAvatarPreview() {
    GreenPassportTheme {
        ProfileAvatar(style = AvatarStyle.SKY, size = Dimens.EmptyStateMascotSize)
    }
}
