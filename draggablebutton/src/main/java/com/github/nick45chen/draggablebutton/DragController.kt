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
    private val buttonHeight: Int = 100,
    private val density: Float = 3f // Density for dp to px conversion
) {
    
    var currentPosition by mutableStateOf(initialPosition)
        private set
    
    var isDragging by mutableStateOf(false)
        private set
    
    var isOverlappingCloseTarget by mutableStateOf(false)
        private set
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
                    
                    // Check overlap with close target while dragging
                    checkCloseTargetOverlap()
                    
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
                    // 1. Button released over close target (priority)
                    // 2. Horizontal: >50% of width crosses screen edge
                    // 3. Vertical: Any part moves outside safe area
                    if (isOverlappingCloseTarget) {
                        // Dispose the button - released over close target
                        disposeCallback?.invoke()
                    } else if (shouldDisposeButton(currentPosition)) {
                        // Dispose the button - boundary disposal
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
                isOverlappingCloseTarget = false
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
     * Checks if the button overlaps with the close target.
     * Close target is positioned at the bottom center of the screen.
     */
    private fun checkCloseTargetOverlap() {
        val bounds = screenBounds ?: return
        
        // Close target specs: 80dp size, bottom center position
        val closeTargetSize = 80f * density
        val closeTargetX = (bounds.right - bounds.left) / 2f - closeTargetSize / 2f
        val closeTargetY = bounds.bottom - closeTargetSize - (100f * density) // 100dp from bottom
        
        // Button bounds
        val buttonCenterX = currentPosition.x + buttonWidth / 2f
        val buttonCenterY = currentPosition.y + buttonHeight / 2f
        
        // Close target bounds
        val closeTargetCenterX = closeTargetX + closeTargetSize / 2f
        val closeTargetCenterY = closeTargetY + closeTargetSize / 2f
        
        // Calculate distance between centers
        val deltaX = buttonCenterX - closeTargetCenterX
        val deltaY = buttonCenterY - closeTargetCenterY
        val distance = kotlin.math.sqrt(deltaX * deltaX + deltaY * deltaY)
        
        // Consider overlapping if distance is less than combined radii
        val buttonRadius = kotlin.math.min(buttonWidth, buttonHeight) / 2f
        val closeTargetRadius = closeTargetSize / 2f
        
        isOverlappingCloseTarget = distance < (buttonRadius + closeTargetRadius)
    }
    
    /**
     * Gets the close target position for rendering.
     */
    fun getCloseTargetPosition(): Position? {
        val bounds = screenBounds ?: return null
        val closeTargetSize = 80f * density
        return Position(
            x = (bounds.right - bounds.left) / 2f - closeTargetSize / 2f,
            y = bounds.bottom - closeTargetSize - (100f * density)
        )
    }
}