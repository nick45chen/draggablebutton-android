package com.github.nick45chen.draggablebutton.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.nick45chen.draggablebutton.DragEvent
import com.github.nick45chen.draggablebutton.DraggableButtonManager
import com.github.nick45chen.draggablebutton.sample.ui.theme.DraggableButtonSampleTheme

class MainActivity : ComponentActivity() {

    private var customButtonManager: DraggableButtonManager? = null
    private var defaultButtonManager: DraggableButtonManager? = null
    private var starButtonManager: DraggableButtonManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Calculate positions based on screen size
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()

        // Custom button with all features (right side)
        customButtonManager = DraggableButtonManager.builder(this@MainActivity)
            .setClickListener {
                Toast.makeText(this, "Custom button clicked!", Toast.LENGTH_SHORT).show()
            }
            .setDragListener { event: DragEvent ->
                // Uncomment to see drag events in action
                // Toast.makeText(this, "Dragging: ${event.state}", Toast.LENGTH_SHORT).show()
            }
            .setDisposeListener {
                Toast.makeText(this, "Custom button disposed!", Toast.LENGTH_SHORT).show()
            }
            .setComposeContent {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White
                )
            }
            .setSize(150, 150)
            .setInitialPosition(screenWidth - 180f, screenHeight * 0.3f)
            .build()

        // Default button (no custom content) - left side
        defaultButtonManager = DraggableButtonManager.builder(this@MainActivity)
            .setClickListener {
                Toast.makeText(this, "Default button clicked!", Toast.LENGTH_SHORT).show()
            }
            .setDisposeListener {
                Toast.makeText(this, "Default button disposed!", Toast.LENGTH_SHORT).show()
            }
            .setSize(120, 120)
            .setInitialPosition(50f, screenHeight * 0.4f)
            .build()

        // Star button (different size) - top center
        starButtonManager = DraggableButtonManager.builder(this@MainActivity)
            .setClickListener {
                Toast.makeText(this, "Star button clicked!", Toast.LENGTH_SHORT).show()
            }
            .setDisposeListener {
                Toast.makeText(this, "Star button disposed!", Toast.LENGTH_SHORT).show()
            }
            .setComposeContent {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star",
                    tint = Color.Yellow
                )
            }
            .setSize(100, 100)
            .setInitialPosition(screenWidth * 0.4f, 150f)
            .build()

        setContent {
            DraggableButtonSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SampleContent(
                        modifier = Modifier.padding(innerPadding),
                        onShowCustom = { customButtonManager?.show() },
                        onShowDefault = { defaultButtonManager?.show() },
                        onShowStar = { starButtonManager?.show() },
                        onHideAll = {
                            customButtonManager?.hide()
                            defaultButtonManager?.hide()
                            starButtonManager?.hide()
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        customButtonManager?.dispose()
        defaultButtonManager?.dispose()
        starButtonManager?.dispose()
    }
}

@Composable
fun SampleContent(
    modifier: Modifier = Modifier,
    onShowCustom: () -> Unit,
    onShowDefault: () -> Unit,
    onShowStar: () -> Unit,
    onHideAll: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎯 Draggable Button Demo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Tap buttons below to see all API features in action",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Demo Controls
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🚀 Try These Examples",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onShowCustom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Custom ➕", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                    }

                    Button(
                        onClick = onShowDefault,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Default ⚪", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onShowStar,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Star ⭐", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                    }

                    OutlinedButton(
                        onClick = onHideAll,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Hide All", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                    }
                }
            }
        }

        // Instructions
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "📱 How to Use",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• Tap: Shows toast message\n" +
                            "• Drag: Move around the screen\n" +
                            "• Drag to bottom: Red close target appears\n" +
                            "• Release over red: Button disposes\n" +
                            "• Drag past edges: Auto-dispose",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Features
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "✨ API Features Shown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• Builder pattern configuration\n" +
                            "• Custom Composable content\n" +
                            "• Default appearance\n" +
                            "• Click & drag listeners\n" +
                            "• Dispose listener\n" +
                            "• Different sizes & positions\n" +
                            "• Safe area handling\n" +
                            "• Close target disposal",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Auto-Dispose Rules
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "🛡️ Auto-Dispose Rules",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• Close target: Drag to red circle at bottom\n" +
                            "• Horizontal: >50% width crosses screen edge\n" +
                            "• Vertical: Any part exits safe area",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SampleContentPreview() {
    DraggableButtonSampleTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            SampleContent(
                modifier = Modifier.padding(innerPadding),
                onShowCustom = {},
                onShowDefault = {},
                onShowStar = {},
                onHideAll = {}
            )
        }
    }
}