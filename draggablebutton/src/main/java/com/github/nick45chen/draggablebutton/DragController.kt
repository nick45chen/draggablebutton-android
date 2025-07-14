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
                    
                    // Constrain to screen bounds
                    currentPosition = constrainToBounds(newPosition)
                    
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
                    dragListener?.invoke(
                        DragEvent(
                            position = currentPosition,
                            velocity = Position(0f, 0f),
                            state = DragState.END
                        )
                    )
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