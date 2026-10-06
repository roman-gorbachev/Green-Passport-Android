package com.smartcity.greenpassport.feature.ecotips.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.smartcity.greenpassport.core.designsystem.component.NetworkImage
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import com.smartcity.greenpassport.core.model.EcoTip

private const val PLACEHOLDER_ICON_FRACTION = 0.32f
private const val GRADIENT_END_ALPHA = 0.75f

@Composable
fun ArticleCover(tip: EcoTip, modifier: Modifier = Modifier) {
    val gradient = Brush.linearGradient(
        listOf(
            GreenPassportTheme.brandColors.forestDeep,
            GreenPassportTheme.brandColors.forestDeep.copy(alpha = GRADIENT_END_ALPHA)
        ),
    )
    BoxWithConstraints(contentAlignment = Alignment.Center, modifier = modifier.background(gradient)) {
        Icon(
            imageVector = ecoTipCategoryIcon(tip.category),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(minOf(maxWidth, maxHeight) * PLACEHOLDER_ICON_FRACTION),
        )
        if (tip.imageUrl != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                NetworkImage(url = tip.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
