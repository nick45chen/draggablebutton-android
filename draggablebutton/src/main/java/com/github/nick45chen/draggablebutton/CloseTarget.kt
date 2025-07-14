package com.github.nick45chen.draggablebutton

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Circular close target that appears during dragging.
 * Changes color based on overlap state.
 */
@Composable
fun CloseTarget(
    isOverlapping: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isOverlapping) {
        Color.Red.copy(alpha = 0.8f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
    }
    
    val borderColor = if (isOverlapping) {
        Color.Red
    } else {
        MaterialTheme.colorScheme.outline
    }
    
    val iconColor = if (isOverlapping) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    Box(
        modifier = modifier
            .size(80.dp)
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .border(
                width = 2.dp,
                color = borderColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = iconColor,
            modifier = Modifier.size(32.dp)
        )
    }
}