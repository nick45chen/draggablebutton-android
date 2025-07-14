package com.github.nick45chen.draggablebutton

import android.app.Activity
import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

/**
 * Single Responsibility Principle: This class only manages the lifecycle and state of the draggable button
 * Open/Closed Principle: Open for extension, closed for modification
 * Dependency Inversion Principle: Depends on abstractions, not concretions
 */
@Suppress("unused")
class DraggableButtonManager private constructor(
    private val activity: Activity,
    private val configuration: DraggableButtonConfiguration,
    private val callback: DraggableButtonCallback?
) : DefaultLifecycleObserver {

    private var draggableButtonView: DraggableButtonView? = null
    private var isInitialized = false

    companion object {
        fun create(
            activity: Activity,
            configuration: DraggableButtonConfiguration = DefaultDraggableButtonConfiguration(),
            callback: DraggableButtonCallback? = null
        ): DraggableButtonManager {
            return DraggableButtonManager(activity, configuration, callback)
        }
    }

    fun initialize(lifecycleOwner: LifecycleOwner) {
        if (isInitialized) return

        lifecycleOwner.lifecycle.addObserver(this)
        createDraggableButton()
        isInitialized = true

        // Show button immediately if lifecycle is already started
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            show()
        }
    }

    private fun createDraggableButton() {
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
        draggableButtonView = FloatingDraggableButtonView(
            activity,
            rootView,
            configuration,
            callback
        )
    }

    fun show() {
        draggableButtonView?.show()
    }

    fun hide() {
        draggableButtonView?.hide()
    }

    fun isVisible(): Boolean {
        return draggableButtonView?.isVisible() ?: false
    }

    fun updatePosition(x: Float, y: Float) {
        draggableButtonView?.updatePosition(x, y)
    }

    fun getPosition(): Pair<Float, Float> {
        return draggableButtonView?.getPosition() ?: Pair(0f, 0f)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (isInitialized) {
            show()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        hide()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        if (isInitialized) {
            show()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        cleanup()
    }

    private fun cleanup() {
        draggableButtonView?.hide()
        draggableButtonView = null
        isInitialized = false
    }
}