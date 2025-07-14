package com.github.nick45chen.draggablebutton

import android.content.Context

/**
 * Abstract base class for managing overlay windows.
 * Follows Single Responsibility Principle by handling only overlay lifecycle.
 */
abstract class OverlayManager(
    protected val context: Context,
    protected val configuration: DraggableButtonConfiguration
) {
    
    protected var isShowing = false
    
    /**
     * Shows the overlay.
     */
    abstract fun show()
    
    /**
     * Hides the overlay.
     */
    abstract fun hide()
    
    /**
     * Disposes the overlay and releases resources.
     */
    abstract fun dispose()
    
    /**
     * Returns true if the overlay is currently showing.
     */
    fun isVisible(): Boolean = isShowing
}