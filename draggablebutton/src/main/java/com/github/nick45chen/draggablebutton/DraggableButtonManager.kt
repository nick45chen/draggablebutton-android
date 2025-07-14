package com.github.nick45chen.draggablebutton

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable

/**
 * Main entry point for creating and managing draggable floating buttons.
 * Follows builder pattern for configuration.
 */
class DraggableButtonManager private constructor(
    private val context: Context,
    private val configuration: DraggableButtonConfiguration
) {
    
    private var overlayManager: ActivityOverlayManager? = null
    private var isShowing = false
    
    /**
     * Builder class for configuring the draggable button.
     */
    class Builder(private val context: Context) {
        private var clickListener: (() -> Unit)? = null
        private var dragListener: ((DragEvent) -> Unit)? = null
        private var disposeListener: (() -> Unit)? = null
        private var composeContent: (@Composable () -> Unit)? = null
        private var width: Int = 100
        private var height: Int = 100
        private var initialPosition: Position = Position(0f, 0f)
        
        fun setClickListener(listener: () -> Unit): Builder {
            this.clickListener = listener
            return this
        }
        
        fun setDragListener(listener: (DragEvent) -> Unit): Builder {
            this.dragListener = listener
            return this
        }
        
        fun setDisposeListener(listener: () -> Unit): Builder {
            this.disposeListener = listener
            return this
        }
        
        fun setComposeContent(content: @Composable () -> Unit): Builder {
            this.composeContent = content
            return this
        }
        
        fun setSize(width: Int, height: Int): Builder {
            this.width = width
            this.height = height
            return this
        }
        
        fun setInitialPosition(x: Float, y: Float): Builder {
            this.initialPosition = Position(x, y)
            return this
        }
        
        fun build(): DraggableButtonManager {
            val configuration = DraggableButtonConfiguration(
                clickListener = clickListener,
                dragListener = dragListener,
                disposeListener = disposeListener,
                composeContent = composeContent,
                width = width,
                height = height,
                initialPosition = initialPosition
            )
            
            return DraggableButtonManager(context, configuration)
        }
    }
    
    /**
     * Shows the draggable button overlay.
     */
    fun show() {
        if (isShowing) return
        
        overlayManager = ActivityOverlayManager(context, configuration)
        
        overlayManager?.show()
        isShowing = true
    }
    
    /**
     * Hides the draggable button overlay.
     */
    fun hide() {
        if (!isShowing) return
        
        overlayManager?.hide()
        isShowing = false
    }
    
    /**
     * Disposes the draggable button and releases resources.
     */
    fun dispose() {
        hide()
        overlayManager?.dispose()
        overlayManager = null
    }
    
    /**
     * Returns true if the button is currently showing.
     */
    fun isVisible(): Boolean = isShowing
    
    companion object {
        /**
         * Creates a new builder instance.
         */
        fun builder(context: Context): Builder = Builder(context)
    }
}