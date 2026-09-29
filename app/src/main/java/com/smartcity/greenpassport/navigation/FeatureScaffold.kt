package com.smartcity.greenpassport.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.smartcity.greenpassport.core.designsystem.component.GpTopBar
import com.smartcity.greenpassport.core.designsystem.theme.Dimens

@Composable
fun FeatureScaffold(
    title: String,
    onNavigateBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    content: @Composable (innerPadding: PaddingValues) -> Unit,
) {
    val contentInsets = if (onNavigateBack == null) {
        WindowInsets.navigationBars.add(WindowInsets(bottom = Dimens.BottomBarReservedHeight))
    } else {
        WindowInsets.navigationBars
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = contentInsets,
        topBar = { GpTopBar(title = title, onNavigateBack = onNavigateBack) },
        content = { innerPadding -> content(innerPadding) },
    )
}
