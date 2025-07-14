package com.github.nick45chen.draggablebutton.sample

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.nick45chen.draggablebutton.ButtonScope
import com.github.nick45chen.draggablebutton.DragEvent
import com.github.nick45chen.draggablebutton.DraggableButtonManager
import com.github.nick45chen.draggablebutton.sample.ui.theme.DraggableButtonSampleTheme

class MainActivity : ComponentActivity() {

    private var draggableButtonManager: DraggableButtonManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Calculate center-right position based on screen size
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()
        
        val buttonSize = 150f // Button size in pixels
        val rightMargin = 32f // Distance from right edge
        val centerRightX = screenWidth - buttonSize - rightMargin
        val centerRightY = (screenHeight - buttonSize) / 2f

        // Create draggable button manager with builder pattern
        draggableButtonManager = DraggableButtonManager.builder(this@MainActivity)
            .setClickListener {
                Toast.makeText(this, "Draggable button clicked!", Toast.LENGTH_SHORT).show()
            }
            .setDragListener { event: DragEvent ->
                // Handle drag events if needed
                // Toast.makeText(this, "Dragging: ${event.state}", Toast.LENGTH_SHORT).show()
            }
            .setComposeContent {
                // Custom content - FAB with add icon
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            .setSize(150, 150)
            .setScope(ButtonScope.ACTIVITY)
            .setInitialPosition(centerRightX, centerRightY)
            .build()

        setContent {
            DraggableButtonSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SampleContent(
                        modifier = Modifier.padding(innerPadding),
                        onShowClick = { draggableButtonManager?.show() },
                        onHideClick = { draggableButtonManager?.hide() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        draggableButtonManager?.dispose()
    }
}

@Composable
fun SampleContent(
    modifier: Modifier = Modifier,
    onShowClick: () -> Unit,
    onHideClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Draggable Button Sample",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Use the buttons below to control the draggable floating button.",
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = onShowClick,
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Text("Show Draggable Button")
        }

        Button(
            onClick = onHideClick
        ) {
            Text("Hide Draggable Button")
        }

        Text(
            text = "You can drag the floating button around the screen and tap it to see a toast message.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SampleContentPreview() {
    DraggableButtonSampleTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            SampleContent(
                modifier = Modifier.padding(innerPadding),
                onShowClick = {},
                onHideClick = {}
            )
        }
    }
}