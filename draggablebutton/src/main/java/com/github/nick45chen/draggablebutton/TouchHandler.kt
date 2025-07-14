package com.github.nick45chen.draggablebutton

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import android.view.MotionEvent
import androidx.compose.ui.input.pointer.pointerInteropFilter

/**
 * Modifier that handles touch events for the draggable button.
 * Bridges between Compose touch handling and the DragController.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.touchHandler(dragController: DragController): Modifier = this.then(
    Modifier.pointerInteropFilter { event ->
        dragController.handleTouchEvent(event)
    }
)