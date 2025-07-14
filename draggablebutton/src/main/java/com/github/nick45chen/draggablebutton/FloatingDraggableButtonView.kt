package com.github.nick45chen.draggablebutton

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import kotlin.math.abs

/**
 * Custom ImageView that properly handles click events for accessibility
 */
private class AccessibleImageView(context: Context) : AppCompatImageView(context) {
    private var onClickCallback: (() -> Unit)? = null

    fun setOnClickCallback(callback: () -> Unit) {
        onClickCallback = callback
    }

    override fun performClick(): Boolean {
        super.performClick()
        onClickCallback?.invoke()
        return true
    }
}

/**
 * Single Responsibility Principle: This class only handles the view logic and touch events
 * Liskov Substitution Principle: Can be substituted for DraggableButtonView interface
 */
class FloatingDraggableButtonView(
    private val context: Context,
    private val parentView: ViewGroup,
    private val configuration: DraggableButtonConfiguration,
    private val callback: DraggableButtonCallback?
) : DraggableButtonView {

    private val buttonView: AccessibleImageView
    private var initialX = 0f
    private var initialY = 0f
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false
    private var isVisible = false

    init {
        buttonView = createButtonView()
        setupTouchListener()
    }

    private fun createButtonView(): AccessibleImageView {
        return AccessibleImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                configuration.buttonSize,
                configuration.buttonSize
            )

            scaleType = ImageView.ScaleType.CENTER_INSIDE
            isClickable = configuration.isClickable

            // Set icon if provided
            configuration.icon?.let { setImageDrawable(it) }

            // Create circular background
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(configuration.backgroundColor)
            }

            // Add elevation for material design
            ViewCompat.setElevation(this, 8f)

            // Set up accessibility click callback
            setOnClickCallback {
                if (configuration.isClickable && !isDragging) {
                    callback?.onButtonClicked()
                }
            }
        }
    }

    private fun setupTouchListener() {
        buttonView.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (configuration.isDraggable) {
                        initialX = view.x
                        initialY = view.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        isDragging = false
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    if (configuration.isDraggable) {
                        val deltaX = event.rawX - initialTouchX
                        val deltaY = event.rawY - initialTouchY

                        // Check if movement threshold is exceeded
                        if (!isDragging && (abs(deltaX) > 10 || abs(deltaY) > 10)) {
                            isDragging = true
                            callback?.onButtonDragStarted()
                        }

                        if (isDragging) {
                            val newX = initialX + deltaX
                            val newY = initialY + deltaY

                            // Keep button within screen bounds
                            val constrainedX =
                                newX.coerceIn(0f, (parentView.width - view.width).toFloat())
                            val constrainedY =
                                newY.coerceIn(0f, (parentView.height - view.height).toFloat())

                            view.x = constrainedX
                            view.y = constrainedY

                            callback?.onPositionChanged(constrainedX, constrainedY)
                        }
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    if (configuration.isDraggable && isDragging) {
                        isDragging = false
                        callback?.onButtonDragEnded()

                        if (configuration.snapToEdge) {
                            snapToNearestEdge()
                        }
                    } else if (configuration.isClickable && !isDragging) {
                        view.performClick()
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun snapToNearestEdge() {
        val screenWidth = parentView.width.toFloat()
        val buttonCenterX = buttonView.x + (buttonView.width / 2)

        val targetX = if (buttonCenterX < screenWidth / 2) {
            configuration.marginFromEdge.toFloat()
        } else {
            screenWidth - buttonView.width - configuration.marginFromEdge
        }

        // Animate to edge
        ValueAnimator.ofFloat(buttonView.x, targetX).apply {
            duration = 200
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                buttonView.x = animatedValue
                callback?.onPositionChanged(animatedValue, buttonView.y)
            }
            start()
        }
    }

    override fun show() {
        if (!isVisible) {
            parentView.addView(buttonView)

            // Set initial position (right side, center vertically)
            buttonView.x =
                parentView.width - configuration.buttonSize - configuration.marginFromEdge.toFloat()
            buttonView.y = (parentView.height - configuration.buttonSize) / 2f

            isVisible = true
        }
    }

    override fun hide() {
        if (isVisible) {
            parentView.removeView(buttonView)
            isVisible = false
        }
    }

    override fun isVisible(): Boolean = isVisible

    override fun updatePosition(x: Float, y: Float) {
        if (isVisible) {
            buttonView.x = x
            buttonView.y = y
        }
    }

    override fun getPosition(): Pair<Float, Float> {
        return if (isVisible) {
            Pair(buttonView.x, buttonView.y)
        } else {
            Pair(0f, 0f)
        }
    }
}