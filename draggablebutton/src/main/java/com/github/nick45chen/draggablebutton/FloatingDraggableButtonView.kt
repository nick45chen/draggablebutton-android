package com.github.nick45chen.draggablebutton

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs
import kotlin.math.max

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
    private var safeArea = Rect()

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
                            // Ensure safe area is calculated for dragging
                            if (safeArea.isEmpty) {
                                updateSafeArea()
                            }
                            callback?.onButtonDragStarted()
                        }

                        if (isDragging) {
                            val newX = initialX + deltaX
                            val newY = initialY + deltaY

                            // Keep button within safe area bounds
                            val (constrainedX, constrainedY) = constrainToSafeArea(newX, newY)

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

    private fun updateSafeArea() {
        // Always calculate fallback first to ensure we have something
        calculateSafeAreaFallback()

        // Get window insets to determine safe area (this may update later)
        ViewCompat.setOnApplyWindowInsetsListener(parentView) { _, insets ->
            calculateSafeAreaFromInsets(insets)
            insets
        }
    }

    private fun calculateSafeAreaFromInsets(insets: WindowInsetsCompat) {
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val displayCutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())

        // Calculate safe area with system bars and display cutout
        safeArea.left = max(systemBars.left, displayCutout.left) + configuration.marginFromEdge
        safeArea.top = max(systemBars.top, displayCutout.top) + configuration.marginFromEdge
        safeArea.right = parentView.width - max(
            systemBars.right,
            displayCutout.right
        ) - configuration.marginFromEdge - configuration.buttonSize
        safeArea.bottom = parentView.height - max(
            systemBars.bottom,
            displayCutout.bottom
        ) - configuration.marginFromEdge - configuration.buttonSize

        ensureMinimumSafeArea()
    }

    private fun calculateSafeAreaFallback() {
        // Ensure parent view has dimensions
        if (parentView.width <= 0 || parentView.height <= 0) {
            // Set a minimal safe area as fallback
            safeArea.set(
                configuration.marginFromEdge,
                configuration.marginFromEdge,
                configuration.marginFromEdge + configuration.buttonSize,
                configuration.marginFromEdge + configuration.buttonSize
            )
            return
        }

        // Fallback calculation using status bar and navigation bar estimates
        val statusBarHeight = getStatusBarHeight()
        val navigationBarHeight = getNavigationBarHeight()

        safeArea.left = configuration.marginFromEdge
        safeArea.top = statusBarHeight + configuration.marginFromEdge
        safeArea.right = parentView.width - configuration.marginFromEdge - configuration.buttonSize
        safeArea.bottom =
            parentView.height - navigationBarHeight - configuration.marginFromEdge - configuration.buttonSize

        ensureMinimumSafeArea()
    }

    private fun ensureMinimumSafeArea() {
        // Ensure minimum safe area
        if (safeArea.right <= safeArea.left) {
            safeArea.right = safeArea.left + configuration.buttonSize
        }
        if (safeArea.bottom <= safeArea.top) {
            safeArea.bottom = safeArea.top + configuration.buttonSize
        }
    }

    private fun getStatusBarHeight(): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            // Default status bar height for API 21+
            (24 * context.resources.displayMetrics.density).toInt()
        }
    }

    private fun getNavigationBarHeight(): Int {
        val resourceId =
            context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            // Default navigation bar height
            (48 * context.resources.displayMetrics.density).toInt()
        }
    }

    private fun constrainToSafeArea(x: Float, y: Float): Pair<Float, Float> {
        // If safe area is not properly calculated, use simple bounds
        if (safeArea.isEmpty || safeArea.width() < configuration.buttonSize || safeArea.height() < configuration.buttonSize) {
            val constrainedX =
                x.coerceIn(0f, (parentView.width - configuration.buttonSize).toFloat())
            val constrainedY =
                y.coerceIn(0f, (parentView.height - configuration.buttonSize).toFloat())
            return Pair(constrainedX, constrainedY)
        }

        val constrainedX = x.coerceIn(safeArea.left.toFloat(), safeArea.right.toFloat())
        val constrainedY = y.coerceIn(safeArea.top.toFloat(), safeArea.bottom.toFloat())
        return Pair(constrainedX, constrainedY)
    }

    private fun snapToNearestEdge() {
        val buttonCenterX = buttonView.x + (buttonView.width / 2)
        val safeAreaCenterX = (safeArea.left + safeArea.right) / 2f

        val targetX = if (buttonCenterX < safeAreaCenterX) {
            safeArea.left.toFloat()
        } else {
            safeArea.right.toFloat()
        }

        // Ensure target is within safe area
        val (constrainedTargetX, constrainedTargetY) = constrainToSafeArea(targetX, buttonView.y)

        // Animate to edge within safe area
        ValueAnimator.ofFloat(buttonView.x, constrainedTargetX).apply {
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
            isVisible = true

            // Post to ensure view is laid out before calculating safe area
            parentView.post {
                updateSafeArea()

                // Set initial position (right side of safe area, center vertically within safe area)
                val initialX = safeArea.right.toFloat()
                val initialY = (safeArea.top + safeArea.bottom) / 2f

                val (constrainedX, constrainedY) = constrainToSafeArea(initialX, initialY)
                buttonView.x = constrainedX
                buttonView.y = constrainedY
            }
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
            val (constrainedX, constrainedY) = constrainToSafeArea(x, y)
            buttonView.x = constrainedX
            buttonView.y = constrainedY
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