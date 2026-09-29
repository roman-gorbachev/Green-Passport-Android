package com.smartcity.greenpassport.core.designsystem.component

import android.view.MotionEvent
import android.view.WindowManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val SHEET_DIM_AMOUNT = 0.4f
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
    val window = (LocalView.current.parent as? DialogWindowProvider)?.window
    val scope = rememberCoroutineScope()
    val dragOffset = remember { Animatable(0f) }

    if (window != null) {
        DisposableEffect(window) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            window.setDimAmount(SHEET_DIM_AMOUNT)
            window.setWindowAnimations(android.R.style.Animation_InputMethod)
            onDispose {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(interactionSource = null, indication = null, onClick = onDismiss),
        )
        Surface(
            shape = RoundedCornerShape(topStart = Dimens.CornerRadiusSheet, topEnd = Dimens.CornerRadiusSheet),
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = modifier
                .align(Alignment.BottomCenter)
                .statusBarsPadding()
                .fillMaxWidth()
                .offset { IntOffset(x = 0, y = dragOffset.value.roundToInt()) },
        ) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(
                        start = Dimens.SheetContentPadding,
                        end = Dimens.SheetContentPadding,
                        bottom = Dimens.CardPadding,
                    ),
            ) {
                SheetDragHandle(
                    onDrag = { offset -> scope.launch { dragOffset.snapTo(offset) } },
                    onDragEnd = { velocity ->
                        if (velocity > DISMISS_VELOCITY || dragOffset.value > DISMISS_OFFSET_PX) {
                            onDismiss()
                        } else {
                            scope.launch {
                                dragOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
                            }
                        }
                    },
                )
                content()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SheetDragHandle(
    onDrag: (Float) -> Unit,
    onDragEnd: (velocity: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tracker = remember { DragTracker() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.SheetDragZoneHeight)
            .pointerInteropFilter { event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> tracker.start(event.rawY, event.eventTime)
                    MotionEvent.ACTION_MOVE -> onDrag(tracker.move(event.rawY, event.eventTime))
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> onDragEnd(tracker.velocity)
                }
                true
            },
    ) {
        Box(
            modifier = Modifier
                .size(width = Dimens.SheetHandleWidth, height = Dimens.SheetHandleHeight)
                .background(
                    MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(Dimens.CornerRadiusPill),
                ),
        )
    }
}

private class DragTracker {
    private var startY = 0f
    private var lastY = 0f
    private var lastTime = 0L

    var velocity = 0f
        private set

    fun start(rawY: Float, time: Long) {
        startY = rawY
        lastY = rawY
        lastTime = time
        velocity = 0f
    }

    fun move(rawY: Float, time: Long): Float {
        val elapsed = (time - lastTime).coerceAtLeast(1L)
        velocity = (rawY - lastY) / elapsed * MILLIS_PER_SECOND
        lastY = rawY
        lastTime = time
        return (rawY - startY).coerceAtLeast(0f)
    }

    companion object {
        private const val MILLIS_PER_SECOND = 1000f
    }
}
