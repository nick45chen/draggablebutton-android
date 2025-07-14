package com.github.nick45chen.draggablebutton

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy

/**
 * Manages global overlay for system-wide draggable buttons.
 * Uses WindowManager to create a system overlay window.
 */
class GlobalOverlayManager(
    context: Context,
    configuration: DraggableButtonConfiguration
) : OverlayManager(context, configuration) {
    
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var composeView: ComposeView? = null
    private var dragController: DragController? = null
    
    override fun show() {
        if (isShowing) return
        
        // Check if we have overlay permission
        if (!canDrawOverlays()) {
            throw SecurityException("SYSTEM_ALERT_WINDOW permission not granted")
        }
        
        dragController = DragController(
            dragListener = configuration.dragListener,
            clickListener = configuration.clickListener,
            initialPosition = configuration.initialPosition,
            buttonWidth = configuration.width,
            buttonHeight = configuration.height
        )
        
        // Calculate screen bounds for global overlay
        val screenBounds = calculateGlobalScreenBounds()
        dragController!!.setScreenBounds(screenBounds)
        
        composeView = ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                DraggableButtonCompose(
                    configuration = configuration,
                    dragController = dragController!!
                )
            }
        }
        
        val layoutParams = WindowManager.LayoutParams(
            configuration.width,
            configuration.height,
            getOverlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = configuration.initialPosition.x.toInt()
            y = configuration.initialPosition.y.toInt()
        }
        
        windowManager.addView(composeView, layoutParams)
        isShowing = true
    }
    
    override fun hide() {
        if (!isShowing) return
        
        composeView?.let { view ->
            windowManager.removeView(view)
        }
        
        isShowing = false
    }
    
    override fun dispose() {
        hide()
        composeView = null
        dragController = null
    }
    
    private fun canDrawOverlays(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }
    
    private fun getOverlayType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
    }
    
    /**
     * Calculates screen bounds for global overlay using system display metrics.
     */
    private fun calculateGlobalScreenBounds(): ScreenBounds {
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()
        
        // Add padding to keep button away from edges
        val padding = 16f
        
        // Get status bar height
        val statusBarHeight = getStatusBarHeight()
        
        return ScreenBounds(
            left = padding,
            top = statusBarHeight + padding,
            right = screenWidth - padding,
            bottom = screenHeight - padding
        )
    }
    
    /**
     * Gets the status bar height.
     */
    private fun getStatusBarHeight(): Float {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId).toFloat()
        } else {
            0f
        }
    }
}