package com.smartcity.greenpassport.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import com.smartcity.greenpassport.core.designsystem.component.GpBackButton
import com.smartcity.greenpassport.core.designsystem.theme.Dimens

private const val SCROLLED_BAR_ALPHA = 0.94f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureScaffold(
    title: String,
    onNavigateBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    content: @Composable (innerPadding: PaddingValues) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val background = MaterialTheme.colorScheme.background
    val contentInsets = if (onNavigateBack == null) {
        WindowInsets.navigationBars.add(WindowInsets(bottom = Dimens.BottomBarReservedHeight))
    } else {
        WindowInsets.navigationBars
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = background,
        contentWindowInsets = contentInsets,
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        GpBackButton(onClick = onNavigateBack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = background,
                    scrolledContainerColor = background.copy(alpha = SCROLLED_BAR_ALPHA),
                ),
                scrollBehavior = scrollBehavior,
            )
        },
        content = { innerPadding -> content(innerPadding) },
    )
}
