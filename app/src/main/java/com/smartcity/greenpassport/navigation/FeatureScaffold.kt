package com.smartcity.greenpassport.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.smartcity.greenpassport.core.designsystem.component.GlassHeaderScaffold
import com.smartcity.greenpassport.core.designsystem.component.SearchBarContent
import com.smartcity.greenpassport.core.designsystem.theme.Dimens

@Composable
fun FeatureScaffold(
    title: String,
    onNavigateBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    search: SearchBarContent? = null,
    actions: @Composable () -> Unit = {},
    content: @Composable (contentPadding: PaddingValues) -> Unit,
) {
    val bottomInset = if (onNavigateBack == null) Dimens.BottomBarReservedHeight else Dimens.SpacingNone
    GlassHeaderScaffold(
        title = title,
        onNavigateBack = onNavigateBack,
        bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + bottomInset,
        modifier = modifier,
        search = search,
        actions = actions,
        content = content,
    )
}
