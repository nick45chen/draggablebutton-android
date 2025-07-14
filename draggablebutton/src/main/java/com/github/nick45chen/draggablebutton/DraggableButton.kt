package com.github.nick45chen.draggablebutton

import android.app.Activity
import androidx.lifecycle.LifecycleOwner

/**
 * Facade Pattern: Simplifies the interface for using the draggable button
 * Single Responsibility Principle: This class only provides a simple API
 * 
 * Note: This object no longer holds static references to avoid memory leaks.
 * Users should keep their own reference to the DraggableButtonManager.
 */
object DraggableButton {
    
    /**
     * Creates and initializes a draggable button for the given activity
     * 
     * @param activity The activity to attach the button to
     * @param lifecycleOwner The lifecycle owner to observe (usually the activity)
     * @param configuration Optional configuration for the button
     * @param callback Optional callback for button events
     * @return DraggableButtonManager instance - keep this reference to control the button
     */
    fun create(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        configuration: DraggableButtonConfiguration = DefaultDraggableButtonConfiguration(),
        callback: DraggableButtonCallback? = null
    ): DraggableButtonManager {
        val manager = DraggableButtonManager.create(activity, configuration, callback)
        manager.initialize(lifecycleOwner)
        return manager
    }
    
    /**
     * Quick create method with builder pattern support
     * 
     * @return DraggableButtonManager instance - keep this reference to control the button
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
}