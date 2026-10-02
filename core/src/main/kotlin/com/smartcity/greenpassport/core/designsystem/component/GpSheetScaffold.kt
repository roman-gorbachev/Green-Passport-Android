package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.smartcity.greenpassport.core.designsystem.theme.Dimens

private const val NO_DIM = 0f

val SheetDialogProperties = DialogProperties(
    usePlatformDefaultWidth = false,
    decorFitsSystemWindows = false,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpSheetScaffold(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val window = (LocalView.current.parent as? DialogWindowProvider)?.window
    if (window != null) {
        DisposableEffect(window) {
            window.setDimAmount(NO_DIM)
            onDispose {}
        }
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = Dimens.CornerRadiusSheet, topEnd = Dimens.CornerRadiusSheet),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier.padding(
                start = Dimens.SheetContentPadding,
                end = Dimens.SheetContentPadding,
                bottom = Dimens.CardPadding,
            ),
            content = content,
        )
    }
}
