# DraggableButton Android Library

[![](https://jitpack.io/v/nick45chen/DraggableButton-Android.svg)](https://jitpack.io/#nick45chen/DraggableButton-Android)
[![API](https://img.shields.io/badge/API-31%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=31)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)

A modern, feature-rich Android library that provides floating draggable buttons with Jetpack Compose support. Perfect for creating intuitive floating action buttons, quick access controls, or interactive overlay elements.

## ✨ Features

### 🎯 Core Functionality
- **Smooth Dragging**: Responsive touch handling with configurable sensitivity
- **Activity Lifecycle Integration**: Automatically tied to Activity lifecycle
- **Builder Pattern API**: Fluent, easy-to-use configuration
- **Custom Content Support**: Use any Composable as button content

### 🚀 Advanced Features
- **Drag-to-Delete**: Visual close target with red highlight feedback
- **Smart Boundary Detection**: Auto-disposal when crossing screen boundaries
- **Safe Area Handling**: Automatic calculation considering system UI insets
- **Density-Aware Positioning**: Consistent sizing across different screen densities

### 🎨 Visual Features
- **Material 3 Design**: Follows modern Material Design principles
- **Customizable Appearance**: Support for custom colors, sizes, and content
- **Smooth Animations**: Fluid drag animations and visual feedback
- **Edge Constraints**: Smart boundary handling and snapping

## 📱 Requirements

- **Min SDK**: 31 (Android 12)
- **Target SDK**: 36
- **Kotlin**: 2.0.21+
- **Jetpack Compose**: Required

## 📦 Installation

### Step 1: Add JitPack repository

Add JitPack repository to your root `build.gradle.kts`:

```kotlin
repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

### Step 2: Add dependency

Add the dependency to your app `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.nick45chen:DraggableButton-Android:1.0.0")
}
```

## 🚀 Quick Start

### Basic Usage

```kotlin
class MainActivity : ComponentActivity() {
    private var draggableButtonManager: DraggableButtonManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create and configure draggable button
        draggableButtonManager = DraggableButtonManager.builder(this)
            .setClickListener {
                Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show()
            }
            .setSize(150, 150)
            .build()

        // Show the button
        draggableButtonManager?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        draggableButtonManager?.dispose()
    }
}
```

### Advanced Configuration

```kotlin
// Calculate screen position
val displayMetrics = resources.displayMetrics
val screenWidth = displayMetrics.widthPixels.toFloat()
val screenHeight = displayMetrics.heightPixels.toFloat()

val buttonSize = 150f
val rightMargin = 32f
val centerRightX = screenWidth - buttonSize - rightMargin
val centerRightY = (screenHeight - buttonSize) / 2f

// Create advanced draggable button
draggableButtonManager = DraggableButtonManager.builder(this@MainActivity)
    .setClickListener {
        Toast.makeText(this, "Draggable button clicked!", Toast.LENGTH_SHORT).show()
    }
    .setDragListener { event: DragEvent ->
        // Handle drag events
        when (event.state) {
            DragState.START -> Log.d("DragButton", "Drag started")
            DragState.DRAG -> Log.d("DragButton", "Dragging at ${event.position}")
            DragState.END -> Log.d("DragButton", "Drag ended")
        }
    }
    .setDisposeListener {
        Toast.makeText(this, "Button auto-disposed!", Toast.LENGTH_SHORT).show()
    }
    .setComposeContent {
        // Custom content with Material 3 icon
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
    .setSize(150, 150)
    .setInitialPosition(centerRightX, centerRightY)
    .build()
```

### Custom Composable Content

```kotlin
.setComposeContent {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Favorite",
            tint = Color.White
        )
        Text(
            text = "❤️",
            fontSize = 16.sp,
            color = Color.White
        )
    }
}
```

## 📚 API Reference

### DraggableButtonManager.Builder

| Method | Description | Default |
|--------|-------------|---------|
| `setClickListener(listener)` | Set click event handler | `null` |
| `setDragListener(listener)` | Set drag event handler | `null` |
| `setDisposeListener(listener)` | Set disposal event handler | `null` |
| `setComposeContent(content)` | Set custom Composable content | Default FAB |
| `setSize(width, height)` | Set button size in pixels | `100x100` |
| `setInitialPosition(x, y)` | Set initial position | `(0, 0)` |

### DragEvent

```kotlin
data class DragEvent(
    val position: Position,    // Current position
    val velocity: Position,    // Current velocity
    val state: DragState      // START, DRAG, or END
)
```

### Manager Methods

```kotlin
draggableButtonManager?.show()      // Display the button
draggableButtonManager?.hide()      // Hide the button
draggableButtonManager?.dispose()   // Clean up resources
draggableButtonManager?.isVisible() // Check visibility
```

## 🎯 Auto-Disposal Rules

The library automatically disposes the button in these scenarios:

### Drag-to-Delete
- **Close Target**: Appears at bottom center when dragging
- **Visual Feedback**: Target turns red when button overlaps
- **Disposal**: Button disposed when released over red target

### Boundary Disposal
- **Horizontal**: When >50% of button width crosses screen edges
- **Vertical**: When any part of button moves outside safe area (status/navigation bars)

### Safe Area Handling
- Automatically calculates safe areas considering system UI
- Prevents button from being hidden behind status bar or navigation bar
- Constrains movement to visible screen area

## 🎨 Customization Examples

### Different Sizes and Positions

```kotlin
// Small button in top-left
DraggableButtonManager.builder(this)
    .setSize(80, 80)
    .setInitialPosition(50f, 100f)
    .build()

// Large button in center
DraggableButtonManager.builder(this)
    .setSize(200, 200)
    .setInitialPosition(centerX, centerY)
    .build()
```

### Custom Styling

```kotlin
.setComposeContent {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Blue, Color.Purple)
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🚀",
            fontSize = 24.sp
        )
    }
}
```

## 📱 Sample App

The sample app demonstrates **all public APIs** in a single screen for quick understanding:

### ✨ What You'll See
- **Custom Content Button**: Add icon with all listeners configured
- **Default Button**: Library's default appearance 
- **Star Button**: Different size and custom styling
- **Interactive Cards**: Clear explanations of each feature

### 🎬 Features Demonstrated
- Builder pattern configuration
- Click, drag, and dispose listeners
- Custom Composable content vs default appearance
- Different sizes and positions
- Close target disposal (drag to red circle)
- Boundary disposal (drag past edges)
- Safe area handling

### 🚀 Quick Start
```bash
./gradlew :app:installDebug
```

**📹 Demo Recording**: *To be added - record the sample app showing drag interactions, close target behavior, and boundary disposal for a visual preview.*

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🛠️ Build Information

- **Library Version**: 1.0.0
- **Kotlin Version**: 2.0.21
- **Android Gradle Plugin**: 8.11.1
- **Compose BOM**: 2024.09.00

## 📞 Support

If you have any questions or issues, please:

1. Check the [sample app](app/) for usage examples
2. Review the [API documentation](#-api-reference)
3. Open an [issue](https://github.com/nick45chen/DraggableButton-Android/issues) on GitHub

---

Made with ❤️ for the Android community