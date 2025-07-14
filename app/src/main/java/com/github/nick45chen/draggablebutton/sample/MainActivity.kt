package com.github.nick45chen.draggablebutton.sample

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.nick45chen.draggablebutton.DraggableButton
import com.github.nick45chen.draggablebutton.DraggableButtonCallback
import com.github.nick45chen.draggablebutton.sample.ui.theme.DraggableButtonSampleTheme

class MainActivity : ComponentActivity() {
    
    private var draggableButtonManager: com.github.nick45chen.draggablebutton.DraggableButtonManager? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize draggable button
        initializeDraggableButton()
        
        setContent {
            DraggableButtonSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DraggableButtonDemo(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
    
    private fun initializeDraggableButton() {
        draggableButtonManager = DraggableButton.create(
            activity = this,
            lifecycleOwner = this,
            callback = object : DraggableButtonCallback {
                override fun onButtonClicked() {
                    Toast.makeText(this@MainActivity, "Draggable button clicked!", Toast.LENGTH_SHORT).show()
                }
                
                override fun onButtonDragStarted() {
                    //Toast.makeText(this@MainActivity, "Drag started", Toast.LENGTH_SHORT).show()
                }
                
                override fun onButtonDragEnded() {
                    //Toast.makeText(this@MainActivity, "Drag ended", Toast.LENGTH_SHORT).show()
                }
                
                override fun onPositionChanged(x: Float, y: Float) {
                    // Optional: Handle position changes
                }
            }
        ) {
            setButtonSize(120)
            setBackgroundColor(Color.parseColor("#FF6200EE"))
            setSnapToEdge(false)
            setMarginFromEdge(20)
        }
        
        // Show the button immediately after creation
        draggableButtonManager?.show()
    }
}

@Composable
fun DraggableButtonDemo(modifier: Modifier = Modifier) {
    var isButtonVisible by remember { mutableStateOf(true) }
    
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Draggable Button Demo",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Text(
            text = "The floating button overlays on top of this screen",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "You can drag it around and it will snap to edges",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Button(
            onClick = {
                if (isButtonVisible) {
                    DraggableButton.hide()
                } else {
                    DraggableButton.show()
                }
                isButtonVisible = !isButtonVisible
            }
        ) {
            Text(if (isButtonVisible) "Hide Button" else "Show Button")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DraggableButtonDemoPreview() {
    DraggableButtonSampleTheme {
        DraggableButtonDemo()
    }
}