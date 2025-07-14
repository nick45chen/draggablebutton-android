# GEMINI.md

This file helps **Gemini AI CLI** understand and work with the codebase in this repository.

---

## Project Overview

This is an **Android library project** (`draggablebutton`) that provides a draggable floating button which can stay on top of any screen. A separate sample application demonstrates how to integrate and customize the component using **Jetpack Compose** for the UI layer. The codebase is written in **Kotlin**, follows **Clean Architecture** and SOLID principles, and is built with the Gradle build system.

---

## Coding Style

* Follow the [official Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html).
* Keep UI code declarative and stateless when using **Jetpack Compose**.
* Separate concerns clearly into data, domain, and presentation layers to preserve testability and maintainability.

---

## Module Structure

| Path               | Type            | Purpose                                                                      |
| ------------------ | --------------- | ---------------------------------------------------------------------------- |
| `draggablebutton/` | Android library | Contains the core draggable‑button implementation, free of app‑specific code |
| `app/`             | Sample app      | Demonstrates the library with a simple Jetpack Compose UI                    |

---

## Important Commands

### Build

```bash
# Build everything
y./gradlew build

# Clean and build
y./gradlew clean build

# Build only the library
y./gradlew :draggablebutton:build

# Build only the sample app
y./gradlew :app:build
```

### Test

```bash
# All unit tests
y./gradlew test

# Library unit tests only
y./gradlew :draggablebutton:test

# Instrumented tests (device/emulator required)
y./gradlew connectedAndroidTest
```

### Run the Sample App

```bash
# Install debug variant on a connected device/emulator
y./gradlew :app:installDebug

# Launch MainActivity (device/emulator required)
yadb shell am start -n com.github.nick45chen.draggablebutton.sample/.MainActivity
```

---

## Technical Configuration

| Setting                  | Value      |
| ------------------------ | ---------- |
| **Min SDK**              | 31         |
| **Target / Compile SDK** | 36         |
| **Java**                 | 11         |
| **Kotlin**               | 2.0.21     |
| **AGP**                  | 8.1.1      |
| **Compose BOM**          | 2024.09.00 |

---

## Architecture Notes

* The **draggablebutton** module is kept UI‑toolkit agnostic. A thin adapter exposes a `ComposeDraggableButton` composable so the component can be used directly in Jetpack Compose without relying on traditional Views.
* The sample app is written entirely in **Jetpack Compose** using **Material 3**.
* Versions are managed centrally in `gradle/libs.versions.toml`.
* Namespaces

  * Library – `com.github.nick45chen.draggablebutton`
  * Sample – `com.github.nick45chen.draggablebutton.sample`

---

## Getting Started with Jetpack Compose Integration

1. **Add the BOM** – Ensure the `compose-bom` entry is present in your dependency catalog and imported in the module’s `build.gradle`.
2. **Enable Compose** – In the module’s `android {}` block:

   ```groovy
   buildFeatures {
       compose true
   }
   composeOptions {
       kotlinCompilerExtensionVersion libs.versions.compose.compiler.get()
   }
   ```
3. **Use the Composable** –

   ```kotlin
   @Composable
   fun Example() {
       ComposeDraggableButton(
           modifier = Modifier
               .size(56.dp)
               .zIndex(10f),
           icon = Icons.Default.Add,
           onClick = { /* handle click */ }
       )
   }
   ```
4. **Overlay Considerations** – Use `Modifier.zIndex()` to ensure the button stays on top of other content. For system‑wide overlays, create an Activity‑scoped `Window` with `TYPE_APPLICATION_OVERLAY` when appropriate permissions are granted.

---