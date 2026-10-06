package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.blur.materials.HazeMaterials
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

private const val PREVIEW_ROW_COUNT = 30

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassHeaderScaffold(
    title: String,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    bottomPadding: Dp = Dimens.SpacingNone,
    search: SearchBarContent? = null,
    actions: @Composable () -> Unit = {},
    content: @Composable (contentPadding: PaddingValues) -> Unit,
) {
    val hazeState = rememberHazeState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val isContentUnderHeader by remember { derivedStateOf { scrollBehavior.state.contentOffset < 0f } }
    val density = LocalDensity.current
    var headerHeight by remember { mutableStateOf(Dimens.SpacingNone) }
    val background = MaterialTheme.colorScheme.background
    val keyboardPadding = WindowInsets.ime
        .exclude(WindowInsets.navigationBars)
        .asPaddingValues()
        .calculateBottomPadding()
    val contentBottomPadding = if (search == null) {
        bottomPadding
    } else {
        bottomPadding + Dimens.SearchBarReservedHeight + keyboardPadding
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState),
        ) {
            content(PaddingValues(top = headerHeight, bottom = contentBottomPadding))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { size -> headerHeight = with(density) { size.height.toDp() } }
                .then(
                    if (isContentUnderHeader) {
                        Modifier.hazeBlur(
                            input = HazeInput.Sources(hazeState),
                            style = HazeMaterials.thin(containerColor = background),
                        )
                    } else {
                        Modifier
                    },
                ),
        ) {
            ScreenHeader(title = title, onNavigateBack = onNavigateBack, actions = actions)
            if (isContentUnderHeader) {
                HorizontalDivider(
                    thickness = Dimens.DividerThickness,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
        if (search != null) {
            GlassSearchBar(
                content = search,
                hazeState = hazeState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                    .padding(Dimens.BottomBarOuterPadding),
            )
        }
    }
}

@Preview
@Composable
private fun GlassHeaderScaffoldPreview() {
    GreenPassportTheme {
        GlassHeaderScaffold(title = "Задания", onNavigateBack = {}) { contentPadding ->
            LazyColumn(contentPadding = contentPadding) {
                items(PREVIEW_ROW_COUNT) { index ->
                    Text(text = "Строка $index")
                }
            }
        }
    }
}
