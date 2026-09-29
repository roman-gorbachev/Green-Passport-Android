package com.smartcity.greenpassport.core.designsystem.component

import android.os.Build
import android.view.WindowManager
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val SHEET_SURFACE_ALPHA = 0.94f
private const val SCRIM_ALPHA = 0.35f
private const val SHEET_BLUR_RADIUS = 48
private const val DISMISS_VELOCITY = 1200f
private const val DISMISS_OFFSET_PX = 240f

val SheetDialogProperties = DialogProperties(
    usePlatformDefaultWidth = false,
    decorFitsSystemWindows = false,
)

@Composable
fun GpSheetScaffold(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    ConfigureSheetWindow()
    val scope = rememberCoroutineScope()
    val dragOffset = remember { Animatable(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = SCRIM_ALPHA))
            .clickable(interactionSource = null, indication = null, onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .offset { IntOffset(0, dragOffset.value.roundToInt()) }
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        topStart = Dimens.CornerRadiusExtraLarge,
                        topEnd = Dimens.CornerRadiusExtraLarge,
                    ),
                )
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = SHEET_SURFACE_ALPHA))
                .clickable(interactionSource = null, indication = null, onClick = {})
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        scope.launch { dragOffset.snapTo((dragOffset.value + delta).coerceAtLeast(0f)) }
                    },
                    onDragStopped = { velocity ->
                        if (velocity > DISMISS_VELOCITY || dragOffset.value > DISMISS_OFFSET_PX) {
                            onDismiss()
                        } else {
                            dragOffset.animateTo(0f)
                        }
                    },
                )
                .navigationBarsPadding()
                .padding(
                    start = Dimens.CardPadding,
                    end = Dimens.CardPadding,
                    bottom = Dimens.CardPadding,
                ),
        ) {
            SheetHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
            content()
        }
    }
}

@Composable
private fun SheetHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(vertical = Dimens.SpacingSmall + Dimens.SpacingExtraSmall)
            .size(width = Dimens.SheetHandleWidth, height = Dimens.SheetHandleHeight)
            .background(
                MaterialTheme.colorScheme.outline,
                RoundedCornerShape(Dimens.CornerRadiusPill),
            ),
    )
}

@Composable
private fun ConfigureSheetWindow() {
    val window = (LocalView.current.parent as? DialogWindowProvider)?.window ?: return
    DisposableEffect(window) {
        window.setDimAmount(0f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            window.attributes = window.attributes.also { it.blurBehindRadius = SHEET_BLUR_RADIUS }
        }
        onDispose {}
    }
}
