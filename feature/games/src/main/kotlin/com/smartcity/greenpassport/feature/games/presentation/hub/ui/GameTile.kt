package com.smartcity.greenpassport.feature.games.presentation.hub.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.smartcity.greenpassport.core.designsystem.component.NetworkImage
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import com.smartcity.greenpassport.core.model.games.Game
import com.smartcity.greenpassport.feature.games.presentation.hub.state.gameGradient
import com.smartcity.greenpassport.feature.games.presentation.hub.state.gameIcon
import com.smartcity.greenpassport.feature.games.presentation.hub.state.gameIconUrl

private const val TITLE_LINES = 2
private const val PRESSED_SCALE = 0.92f
private const val PRESSED_ROTATION = -3f
private const val SPRING_DAMPING = 0.5f
private const val EMOJI_FRACTION = 0.46f
private const val BREATH_SCALE = 1.06f
private const val BREATH_DURATION_MILLIS = 1800

@Composable
fun GameTile(
    game: Game,
    title: String,
    bestScore: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptics = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (isPressed) PRESSED_SCALE else 1f,
        animationSpec = spring(dampingRatio = SPRING_DAMPING, stiffness = Spring.StiffnessMedium),
        label = "gameTileScale",
    )
    val rotation by animateFloatAsState(
        targetValue = if (isPressed) PRESSED_ROTATION else 0f,
        animationSpec = spring(dampingRatio = SPRING_DAMPING, stiffness = Spring.StiffnessMedium),
        label = "gameTileRotation",
    )
    LaunchedEffect(isPressed) {
        if (isPressed) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
            }
            .clickable(interactionSource = interactionSource, indication = null, role = Role.Button, onClick = onClick),
    ) {
        GameArtwork(game = game)
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            minLines = TITLE_LINES,
            maxLines = TITLE_LINES,
            overflow = TextOverflow.Ellipsis,
        )
        if (bestScore != null) {
            Text(
                text = bestScore,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun GameArtwork(game: Game) {
    val fallback = GreenPassportTheme.brandColors.forestDeep
    val colors = gameGradient(game) ?: listOf(fallback, fallback)
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(Dimens.CornerRadiusLarge))
            .background(Brush.linearGradient(colors)),
    ) {
        val emoji = game.iconEmoji
        if (emoji != null) {
            BreathingEmoji(emoji = emoji, sizePx = constraints.maxWidth * EMOJI_FRACTION)
        } else {
            Icon(
                imageVector = gameIcon(game.materialIcon),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(maxWidth * EMOJI_FRACTION),
            )
        }
        val iconUrl = gameIconUrl(game)
        if (iconUrl != null) {
            NetworkImage(url = iconUrl, contentDescription = null, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun BreathingEmoji(emoji: String, sizePx: Float) {
    val transition = rememberInfiniteTransition(label = "gameEmojiBreath")
    val breath by transition.animateFloat(
        initialValue = 1f,
        targetValue = BREATH_SCALE,
        animationSpec = infiniteRepeatable(
            animation = tween(BREATH_DURATION_MILLIS, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "gameEmojiScale",
    )
    val fontSize = with(LocalDensity.current) { sizePx.toSp() }
    Text(
        text = emoji,
        fontSize = fontSize,
        modifier = Modifier.graphicsLayer {
            scaleX = breath
            scaleY = breath
        },
    )
}
