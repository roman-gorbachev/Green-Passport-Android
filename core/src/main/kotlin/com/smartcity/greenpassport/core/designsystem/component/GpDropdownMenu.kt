package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.smartcity.greenpassport.core.designsystem.theme.Dimens

@Composable
fun GpDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = Dimens.SpacingNone,
        shadowElevation = Dimens.MenuShadowElevation,
        modifier = modifier.widthIn(min = Dimens.MenuMinWidth),
        content = content,
    )
}
