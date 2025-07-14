package com.github.nick45chen.draggablebutton

import androidx.compose.runtime.Composable

/**
 * Configuration data class for the draggable button.
 */
data class DraggableButtonConfiguration(
    val clickListener: (() -> Unit)? = null,
    val dragListener: ((DragEvent) -> Unit)? = null,
    val composeContent: (@Composable () -> Unit)? = null,
    val width: Int = 100,
    val height: Int = 100,
    val scope: ButtonScope = ButtonScope.ACTIVITY,
    val initialPosition: Position = Position(0f, 0f)
)

/**
 * Represents the scope/lifetime of the draggable button.
 */
enum class ButtonScope {
    /** Button is tied to a single Activity lifecycle */
    ACTIVITY,
    /** Button is global and overlays the entire app */
    GLOBAL
}

/**
 * Represents a position with x and y coordinates.
 */
data class Position(
    val x: Float,
    val y: Float
)

/**
 * Represents a drag event with position and velocity information.
 */
data class DragEvent(
    val position: Position,
    val velocity: Position,
    val state: DragState
)

/**
 * Represents the state of a drag operation.
 */
enum class DragState {
    START,
    DRAG,
    END
}

/**
 * Represents screen bounds for boundary detection.
 */
data class ScreenBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)