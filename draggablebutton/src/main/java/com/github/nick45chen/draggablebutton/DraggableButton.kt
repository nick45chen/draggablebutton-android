package com.github.nick45chen.draggablebutton

import android.app.Activity
import androidx.lifecycle.LifecycleOwner

/**
 * Facade Pattern: Simplifies the interface for using the draggable button
 * Single Responsibility Principle: This class only provides a simple API
 */
object DraggableButton {
    
    private var currentManager: DraggableButtonManager? = null
    
    /**
     * Creates and initializes a draggable button for the given activity
     * 
     * @param activity The activity to attach the button to
     * @param lifecycleOwner The lifecycle owner to observe (usually the activity)
     * @param configuration Optional configuration for the button
     * @param callback Optional callback for button events
     */
    fun create(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        configuration: DraggableButtonConfiguration = DefaultDraggableButtonConfiguration(),
        callback: DraggableButtonCallback? = null
    ): DraggableButtonManager {
        // Clean up existing manager if any
        currentManager = null
        
        val manager = DraggableButtonManager.create(activity, configuration, callback)
        manager.initialize(lifecycleOwner)
        currentManager = manager
        
        return manager
    }
    
    /**
     * Quick create method with builder pattern support
     */
    fun create(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        callback: DraggableButtonCallback? = null,
        configBuilder: DraggableButtonConfigurationBuilder.() -> Unit = {}
    ): DraggableButtonManager {
        val configuration = DraggableButtonConfigurationBuilder()
            .apply(configBuilder)
            .build()
        
        return create(activity, lifecycleOwner, configuration, callback)
    }
    
    /**
     * Get the current active manager (if any)
     */
    fun getCurrentManager(): DraggableButtonManager? = currentManager
    
    /**
     * Show the current draggable button
     */
    fun show() {
        currentManager?.show()
    }
    
    /**
     * Hide the current draggable button
     */
    fun hide() {
        currentManager?.hide()
    }
    
    /**
     * Check if the current draggable button is visible
     */
    fun isVisible(): Boolean {
        return currentManager?.isVisible() ?: false
    }
}