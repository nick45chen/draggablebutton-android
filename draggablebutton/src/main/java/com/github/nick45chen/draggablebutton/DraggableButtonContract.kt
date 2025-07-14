package com.github.nick45chen.draggablebutton

import android.graphics.drawable.Drawable

/**
 * Interface Segregation Principle: Separate interfaces for different responsibilities
 */
interface DraggableButtonView {
    fun show()
    fun hide()
    fun isVisible(): Boolean
    fun updatePosition(x: Float, y: Float)
    fun getPosition(): Pair<Float, Float>
}

interface DraggableButtonCallback {
    fun onButtonClicked()
    fun onButtonDragStarted()
    fun onButtonDragEnded()
    fun onPositionChanged(x: Float, y: Float)
}

interface DraggableButtonConfiguration {
    val buttonSize: Int
    val icon: Drawable?
    val backgroundColor: Int
    val isClickable: Boolean
    val isDraggable: Boolean
    val snapToEdge: Boolean
    val marginFromEdge: Int
}