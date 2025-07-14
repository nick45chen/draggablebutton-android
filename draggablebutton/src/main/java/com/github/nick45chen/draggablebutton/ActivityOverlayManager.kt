package com.github.nick45chen.draggablebutton

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Manages overlay for Activity-scoped draggable buttons.
 * Uses the Activity's content view as the overlay container.
 */
class ActivityOverlayManager(
    context: Context,
    configuration: DraggableButtonConfiguration
) : OverlayManager(context, configuration) {
    
    private val activity = context as Activity
    private var composeView: ComposeView? = null
    private var dragController: DragController? = null
    
    override fun show() {
        if (isShowing) return
        
        val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
        
        dragController = DragController(
            dragListener = configuration.dragListener,
            clickListener = configuration.clickListener,
            initialPosition = configuration.initialPosition,
            buttonWidth = configuration.width,
            buttonHeight = configuration.height
        )
        
        // Calculate safe area bounds
        val screenBounds = calculateSafeAreaBounds(contentView)
        dragController!!.setScreenBounds(screenBounds)
        
        composeView = ComposeView(activity).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DraggableButtonCompose(
                    configuration = configuration,
                    dragController = dragController!!
                )
            }
        }
        
        // Add to the activity's content view with explicit layout parameters
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        contentView.addView(composeView, layoutParams)
        isShowing = true
    }
    
    override fun hide() {
        if (!isShowing) return
        
        composeView?.let { view ->
            val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
            contentView.removeView(view)
        }
        
        isShowing = false
    }
    
    override fun dispose() {
        hide()
        composeView = null
        dragController = null
    }
    
    /**
     * Calculates the safe area bounds considering system UI insets.
     */
    private fun calculateSafeAreaBounds(contentView: ViewGroup): ScreenBounds {
        val rect = Rect()
        contentView.getWindowVisibleDisplayFrame(rect)
        
        // Get window insets for safe area calculations
        val windowInsets = ViewCompat.getRootWindowInsets(contentView)
        
        val left = if (windowInsets != null) {
            maxOf(rect.left.toFloat(), windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).left.toFloat())
        } else {
            rect.left.toFloat()
        }
        
        val top = if (windowInsets != null) {
            maxOf(rect.top.toFloat(), windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top.toFloat())
        } else {
            rect.top.toFloat()
        }
        
        val right = if (windowInsets != null) {
            minOf(rect.right.toFloat(), rect.right - windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).right.toFloat())
        } else {
            rect.right.toFloat()
        }
        
        val bottom = if (windowInsets != null) {
            minOf(rect.bottom.toFloat(), rect.bottom - windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom.toFloat())
        } else {
            rect.bottom.toFloat()
        }
        
        // Add some padding to keep button away from edges
        val padding = 16f
        return ScreenBounds(
            left = left + padding,
            top = top + padding,
            right = right - padding,
            bottom = bottom - padding
        )
    }
}