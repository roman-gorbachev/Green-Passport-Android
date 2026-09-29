package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
fun NetworkImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    fallback: Painter? = null,
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        placeholder = fallback,
        error = fallback,
        fallback = fallback,
        modifier = modifier,
        contentScale = contentScale,
    )
}
