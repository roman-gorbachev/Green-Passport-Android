package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.blur.materials.HazeMaterials

private const val SHADOW_ALPHA = 0.14f
private const val BORDER_ALPHA = 0.6f

@Composable
fun GlassSearchBar(
    content: SearchBarContent,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
) {
    val shape = RoundedCornerShape(Dimens.CornerRadiusPill)
    val container = MaterialTheme.colorScheme.surfaceContainer
    val secondaryColor = MaterialTheme.colorScheme.onSurfaceVariant
    val textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.SearchBarHeight)
            .shadow(
                elevation = Dimens.BottomBarElevation,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = SHADOW_ALPHA),
                spotColor = Color.Black.copy(alpha = SHADOW_ALPHA),
            )
            .clip(shape)
            .then(
                if (hazeState != null) {
                    Modifier.hazeBlur(
                        input = HazeInput.Sources(hazeState),
                        style = HazeMaterials.thin(containerColor = container),
                    )
                } else {
                    Modifier.background(container)
                },
            )
            .border(
                width = Dimens.BorderWidthThin,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = BORDER_ALPHA),
                shape = shape,
            )
            .padding(horizontal = Dimens.SpacingMedium),
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = secondaryColor,
            modifier = Modifier.size(Dimens.IconSizeSmall),
        )
        BasicTextField(
            value = content.query,
            onValueChange = content.onQueryChange,
            singleLine = true,
            textStyle = textStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (content.query.isEmpty()) {
                        Text(text = content.placeholder, style = textStyle, color = secondaryColor, maxLines = 1)
                    }
                    innerTextField()
                }
            },
            modifier = Modifier.weight(1f),
        )
        if (content.query.isNotEmpty()) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = secondaryColor,
                modifier = Modifier
                    .size(Dimens.IconSizeSmall)
                    .clip(shape)
                    .clickable { content.onQueryChange("") },
            )
        }
    }
}

@Preview
@Composable
private fun GlassSearchBarPreview() {
    GreenPassportTheme {
        GlassSearchBar(content = SearchBarContent(query = "", onQueryChange = {}, placeholder = "Поиск заданий"))
    }
}
