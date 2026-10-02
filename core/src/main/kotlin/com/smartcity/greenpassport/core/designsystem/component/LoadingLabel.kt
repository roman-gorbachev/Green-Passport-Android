package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import com.smartcity.greenpassport.core.designsystem.theme.Dimens

@Composable
fun LoadingLabel(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    content: @Composable () -> Unit,
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Box(modifier = Modifier.alpha(if (isLoading) 0f else 1f)) {
            content()
        }
        if (isLoading) {
            CircularProgressIndicator(
                color = color,
                strokeWidth = Dimens.ProgressStrokeWidth,
                modifier = Modifier.size(Dimens.LoadingIndicatorSize),
            )
        }
    }
}
