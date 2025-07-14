package com.github.nick45chen.draggablebutton

import android.graphics.Color
import android.graphics.drawable.Drawable

/**
 * Single Responsibility Principle: This class only handles configuration
 */
data class DefaultDraggableButtonConfiguration(
    override val buttonSize: Int = 150,
    override val icon: Drawable? = null,
    override val backgroundColor: Int = Color.BLUE,
    override val isClickable: Boolean = true,
    override val isDraggable: Boolean = true,
    override val snapToEdge: Boolean = true,
    override val marginFromEdge: Int = 16
) : DraggableButtonConfiguration

/**
 * Builder pattern for easy configuration creation
 */
class DraggableButtonConfigurationBuilder {
    private var buttonSize: Int = 150
    private var icon: Drawable? = null
    private var backgroundColor: Int = Color.BLUE
    private var isClickable: Boolean = true
    private var isDraggable: Boolean = true
    private var snapToEdge: Boolean = true
    private var marginFromEdge: Int = 16

    fun setButtonSize(size: Int) = apply { this.buttonSize = size }
    fun setIcon(drawable: Drawable?) = apply { this.icon = drawable }
    fun setBackgroundColor(color: Int) = apply { this.backgroundColor = color }
    fun setClickable(clickable: Boolean) = apply { this.isClickable = clickable }
    fun setDraggable(draggable: Boolean) = apply { this.isDraggable = draggable }
    fun setSnapToEdge(snap: Boolean) = apply { this.snapToEdge = snap }
    fun setMarginFromEdge(margin: Int) = apply { this.marginFromEdge = margin }

    fun build(): DraggableButtonConfiguration = DefaultDraggableButtonConfiguration(
        buttonSize = buttonSize,
        icon = icon,
        backgroundColor = backgroundColor,
        isClickable = isClickable,
        isDraggable = isDraggable,
        snapToEdge = snapToEdge,
        marginFromEdge = marginFromEdge
    )
}