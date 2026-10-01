package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.smartcity.greenpassport.core.designsystem.theme.Dimens

fun <T> LazyListScope.listSectionItems(
    items: List<T>,
    key: ((T) -> Any)? = null,
    hasLeading: Boolean = true,
    itemContent: @Composable (T) -> Unit,
) {
    itemsIndexed(items, key = key?.let { keyOf -> { _: Int, item: T -> keyOf(item) } }) { index, item ->
        ListSectionItem(isFirst = index == 0, isLast = index == items.lastIndex, hasLeading = hasLeading) {
            itemContent(item)
        }
    }
}

@Composable
fun ListSectionItem(
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier,
    hasLeading: Boolean = true,
    content: @Composable () -> Unit,
) {
    val radius = Dimens.CornerRadiusLarge
    val shape = RoundedCornerShape(
        topStart = if (isFirst) radius else Dimens.SpacingNone,
        topEnd = if (isFirst) radius else Dimens.SpacingNone,
        bottomStart = if (isLast) radius else Dimens.SpacingNone,
        bottomEnd = if (isLast) radius else Dimens.SpacingNone,
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        content()
        if (!isLast) {
            ListSectionDivider(hasLeading = hasLeading)
        }
    }
}
