package com.github.nick45chen.draggablebutton

import android.view.MotionEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.abs

/**
 * Handles drag logic independent of UI framework.
 * Follows Single Responsibility Principle by managing only drag state and calculations.
 */
class DragController(
    private val dragListener: ((DragEvent) -> Unit)?,
    private val clickListener: (() -> Unit)?,
    private val disposeCallback: (() -> Unit)? = null,
    initialPosition: Position = Position(0f, 0f),
    private val buttonWidth: Int = 100,
    private val buttonHeight: Int = 100
) {
    
    var currentPosition by mutableStateOf(initialPosition)
        private set
    
    private var isDragging = false
    private var dragStartPosition = Position(0f, 0f)
    private var lastPosition = Position(0f, 0f)
    private var clickThreshold = 10f // pixels
    private var screenBounds: ScreenBounds? = null
    
    /**
     * Sets the screen bounds for boundary detection.
     */
    fun setScreenBounds(bounds: ScreenBounds) {
        screenBounds = bounds
        // Ensure current position is within bounds
        currentPosition = constrainToBounds(currentPosition)
    }
    
    /**
     * Constrains a position to stay within screen bounds.
     */
    private fun constrainToBounds(position: Position): Position {
        val bounds = screenBounds ?: return position
        
        val constrainedX = position.x.coerceIn(
            bounds.left,
            bounds.right - buttonWidth.toFloat()
        )
        
        val constrainedY = position.y.coerceIn(
            bounds.top,
            bounds.bottom - buttonHeight.toFloat()
        )
        
        return Position(constrainedX, constrainedY)
    }
    
    /**
     * Checks if the button should be disposed based on position:
     * - Horizontal: More than 50% of button width crosses screen edge
     * - Vertical: Any part of button moves outside vertical safe area
     */
    private fun shouldDisposeButton(position: Position): Boolean {
        val bounds = screenBounds ?: return false
        
        // Check horizontal boundaries (50% threshold)
        val buttonRight = position.x + buttonWidth
        val halfButtonWidth = buttonWidth / 2f
        
        // Check if more than 50% extends beyond the right edge
        val rightOverlap = buttonRight - bounds.right
        val rightExtension = if (rightOverlap > 0) rightOverlap else 0f
        
        // Check if more than 50% extends beyond the left edge
        val leftOverlap = bounds.left - position.x
        val leftExtension = if (leftOverlap > 0) leftOverlap else 0f
        
        val horizontalDisposal = rightExtension > halfButtonWidth || leftExtension > halfButtonWidth
        
        // Check vertical boundaries (immediate disposal if outside safe area)
        val buttonBottom = position.y + buttonHeight
        val verticalDisposal = position.y < bounds.top || buttonBottom > bounds.bottom
        
        return horizontalDisposal || verticalDisposal
    }
    
    /**
     * Handles touch events and determines drag vs click behavior.
     */
    fun handleTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDragging = false
                dragStartPosition = Position(event.rawX, event.rawY)
                lastPosition = currentPosition
                
                dragListener?.invoke(
                    DragEvent(
                        position = currentPosition,
                        velocity = Position(0f, 0f),
                        state = DragState.START
                    )
                )
                return true
            }
            
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.rawX - dragStartPosition.x
                val deltaY = event.rawY - dragStartPosition.y
                
                // Check if we've moved enough to consider it a drag
                if (!isDragging && (abs(deltaX) > clickThreshold || abs(deltaY) > clickThreshold)) {
                    isDragging = true
                }
                
                if (isDragging) {
                    val newPosition = Position(
                        lastPosition.x + deltaX,
                        lastPosition.y + deltaY
                    )
                    
                    val velocity = Position(
                        event.rawX - currentPosition.x,
                        event.rawY - currentPosition.y
                    )
                    
                    // During drag, allow movement outside safe area
                    currentPosition = newPosition
                    
                    dragListener?.invoke(
                        DragEvent(
                            position = currentPosition,
                            velocity = velocity,
                            state = DragState.DRAG
                        )
                    )
                }
                return true
            }
            
            MotionEvent.ACTION_UP -> {
                if (isDragging) {
                    // Check disposal conditions:
                    // Horizontal: >50% of width crosses screen edge
                    // Vertical: Any part moves outside safe area
                    if (shouldDisposeButton(currentPosition)) {
                        // Dispose the button
                        disposeCallback?.invoke()
                    } else {
                        // Constrain back to safe area if partially outside
                        currentPosition = constrainToBounds(currentPosition)
                        
                        dragListener?.invoke(
                            DragEvent(
                                position = currentPosition,
                                velocity = Position(0f, 0f),
                                state = DragState.END
                            )
                        )
                    }
                } else {
                    // It's a click
                    clickListener?.invoke()
                }
                
                isDragging = false
                return true
            }
        }
        
        return false
    }
    
    /**
     * Updates the current position programmatically.
     */
    fun updatePosition(newPosition: Position) {
        currentPosition = constrainToBounds(newPosition)
    }
    
    /**
     * Returns true if currently dragging.
     */
    fun isDragging(): Boolean = isDragging
}