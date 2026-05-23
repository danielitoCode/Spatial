# Spatial

> Declarative 3D rendering for Android inspired by Jetpack Compose.

---

# Vision

Spatial is a pragmatic 3D rendering library for Android focused on:

- Declarative APIs
- Smooth camera motion
- Compose-first ergonomics
- GPU abstraction
- Natural gestures
- Cinematic transitions
- State-driven scene control

The goal of Core #1 is NOT to build a full game engine.

The goal is:

> Make 3D rendering in Android feel as natural and elegant as Jetpack Compose.

---

# Core #1 Goals

Core #1 focuses on:

- Essential 3D primitives
- Smooth orbit camera
- Zoom and inertia
- Motion transitions
- Gesture system
- Declarative scene API
- Basic materials
- Units system
- Internal GPU abstraction

The developer should never interact directly with:

- OpenGL buffers
- Vertex arrays
- Shaders
- Matrix pipelines
- GPU state management

All low-level rendering details remain internal.

---

# Core Philosophy

Spatial must be:

## Declarative

The scene describes state.

---

## Reactive

Changes in state update rendering.

---

## Compose-first

Designed around Compose mental models.

---

## Opinionated

Good defaults.

---

## Cinematic

Motion quality matters.

---

# Example API

```kotlin
val cameraState = rememberCameraState()

Scene(
    cameraState = cameraState,
    gestures = Gestures.orbit()
) {

    Element.Cube(
        modifier = Modifier3D
            .size(2.meters)
            .position(0f, 0f, -5f)
    )

    Element.Sphere(
        modifier = Modifier3D
            .position(3f, 0f, -8f)
    )
}
```

---

# Technology Stack

## Language

- Kotlin
- Kotlin DSL

---

## Rendering Backend

### OpenGL ES 3.0+

Reasons:

- Stable Android support
- Mature ecosystem
- Lower complexity than Vulkan
- Perfect for Core #1

Vulkan is intentionally postponed.

---

## UI Integration

- Jetpack Compose
- AndroidView bridge
- GLSurfaceView or TextureView internally

---

## Math System

Custom lightweight math module:

- Vec2
- Vec3
- Vec4
- Quaternion
- Matrix4

---

## Assets

- glTF 2.0
- GLB support later

---

## Animations

- Coroutines
- State-driven transitions
- Custom interpolators

---

# Architecture

```text
spatial/
│
├── spatial-core
│   ├── math
│   ├── scenegraph
│   ├── renderer
│   ├── materials
│   ├── geometry
│   ├── camera
│   ├── motion
│   └── gestures
│
├── spatial-compose
│   ├── Scene composable
│   ├── Modifier3D
│   ├── remember states
│   └── Compose adapters
│
├── spatial-assets
│   └── gltf loader
│
└── sample-app
```

---

# Core Systems

# 1. Scene System

## Responsibilities

- Render loop
- Scheduler
- Lifecycle
- Active camera
- Gesture routing
- Scene graph root

---

## API

```kotlin
Scene(
    state = rememberSceneState()
) {

}
```

---

# 2. Element System

## Initial primitives

```kotlin
Element.Cube()
Element.Sphere()
Element.Cylinder()
Element.Plane()
```

---

## Responsibilities

Each element contains:

- Geometry
- Transform
- Material
- Visibility
- Motion bindings

---

# 3. Modifier3D

## Goal

Declarative transforms.

---

## API

```kotlin
Modifier3D
    .position(x, y, z)
    .rotation(x, y, z)
    .scale(value)
    .size(2.meters)
```

---

# 4. Camera System

## Highest priority system

Camera quality defines perceived quality.

---

## Features

- Orbit camera
- Pan
- Zoom
- Inertia
- Damping
- Smooth transitions

---

## API

```kotlin
val cameraState = rememberCameraState()
```

---

## Animation API

```kotlin
cameraState.animateTo(
    position = vec3(0f, 5f, 10f),
    lookAt = vec3(0f, 0f, 0f),
    zoom = 1.5f
)
```

---

# 5. Motion System

## Goal

Cinematic scene transitions.

---

## Features

- Interpolation
- Spring motion
- Ease curves
- Coroutine-based animations
- Camera transitions

---

# 6. Material System

## Initial materials

### Solid color

```kotlin
Material.Color(Color.Red)
```

---

### Texture

```kotlin
Material.Texture(R.drawable.metal)
```

---

## Lighting

Only:

- Ambient light
- Directional light

No advanced PBR yet.

---

# 7. Gesture System

## Features

- Orbit
- Pinch zoom
- Pan
- Velocity tracking
- Inertia
- Gesture smoothing

---

## API

```kotlin
gestures = Gestures.orbit()
```

---

# 8. Units System

## Goal

Avoid arbitrary floats.

---

## API

```kotlin
1.meters
50.cm
45.deg
```

---

# 9. Render Engine

## Internal only

The engine internally manages:

- VBOs
- VAOs
- EBOs
- Shader cache
- MVP matrices
- Resource cleanup
- Render loop
- GPU bindings

These concepts are never exposed publicly.

---

# 10. Scene Graph

```text
Root
 ├── Camera
 ├── Light
 ├── Cube
 │    └── Sphere
```

---

## Responsibilities

- Hierarchical transforms
- Dirty flags
- Traversal
- Visibility propagation

---

# Development Plan

# Phase 1 — Rendering Foundations

## Goal

Render a triangle correctly.

---

## Tasks

- EGL context
- OpenGL initialization
- Vertex shader
- Fragment shader
- Render loop
- Viewport handling
- Clear color

---

# Phase 2 — Math Engine

## Tasks

- Vec3
- Quaternion
- Matrix4
- Perspective projection
- View matrix
- Model matrix

---

# Phase 3 — Camera System

## Tasks

- Orbit camera
- Zoom
- Pan
- Inertia
- Damping
- Gesture input

---

## Milestone

The renderer already feels premium.

---

# Phase 4 — Scene Graph

## Tasks

- Nodes
- Hierarchy
- Transform propagation
- Traversal

---

# Phase 5 — Geometry

## Tasks

- Cube
- Sphere
- Plane
- Cylinder

---

# Phase 6 — Material System

## Tasks

- Solid material
- Texture material
- Ambient light
- Directional light

---

# Phase 7 — Compose DSL

## Tasks

```kotlin
Scene {

}
```

and:

```kotlin
Element.Cube()
```

---

# Phase 8 — Motion System

## Tasks

- animateTo
- interpolation
- easing
- transitions

---

# Phase 9 — Polish

## Tasks

- Smooth gestures
- Stable frame pacing
- Allocation reduction
- Shader caching
- Lifecycle cleanup

---

# Phase 10 — Demo Application

## Goal

Create a polished showcase application.

---

## Demo Features

- Orbit camera
- Zoom
- Inertia
- Scene transitions
- Multiple elements
- Compose UI integration
- Material showcase

---

# Development Environment

# Using Visual Studio Code

Yes, you can absolutely develop Spatial in VS Code.

Recommended setup:

## Required software

- Android Studio (SDK + Emulator only)
- VS Code
- Java 17
- Android SDK
- Gradle
- Kotlin extension

You do NOT need to code inside Android Studio.

Android Studio mainly provides:

- SDK management
- Emulator
- Device tools
- Logcat

VS Code handles:

- Coding
- Git
- Navigation
- Terminal
- Gradle tasks

---

# Recommended VS Code Extensions

## Kotlin

- Kotlin Language

---

## Android

- Android iOS Emulator

---

## Utilities

- Error Lens
- GitLens
- Material Icon Theme

---

# Recommended Workflow

# IMPORTANT

You SHOULD have a playground app running in parallel.

This is critical.

---

# Structure

```text
spatial/
│
├── spatial-core
├── spatial-compose
└── playground-app
```

---

# Why the playground matters

The playground acts as:

- Visual sandbox
- Rendering debugger
- Motion testing environment
- Gesture validation tool
- API experimentation space

Without it, iteration becomes painful.

---

# Playground Requirements

The playground should allow:

- Fast scene iteration
- Camera testing
- Gesture tuning
- FPS validation
- Material previews
- Motion experiments

---

# Playground Features

## Screen 1

Primitive showcase:

- Cube
- Sphere
- Cylinder
- Plane

---

## Screen 2

Camera testing:

- Orbit
- Zoom
- Inertia
- Bounds

---

## Screen 3

Motion showcase:

- animateTo
- Transitions
- Camera choreography

---

## Screen 4

Material showcase:

- Solid colors
- Textures
- Lighting

---

# Recommended Development Loop

## Step 1

Modify renderer.

---

## Step 2

Run playground instantly.

---

## Step 3

Validate:

- Motion
- FPS
- Gestures
- Camera smoothness

---

## Step 4

Refactor API.

---

# Most Important Technical Priorities

# PRIORITY #1

Perfect camera motion.

Even with simple cubes:

- smooth orbit
- good inertia
- elegant zoom
- natural damping

will make Spatial feel premium.

---

# PRIORITY #2

A clean declarative API.

---

# PRIORITY #3

Stable frame pacing.

---

# PRIORITY #4

Zero OpenGL exposure.

---

# Success Criteria

Core #1 is complete if:

- Primitives render correctly
- Camera feels smooth
- Zoom feels natural
- Gestures feel premium
- Motion transitions work
- Compose integration works
- API feels elegant
- Stable 60 FPS
- No OpenGL complexity exposed
- Playground demo looks polished

---

# Long-Term Vision

Future versions may include:

- glTF loading
- PBR
- Shadows
- Postprocessing
- Vulkan backend
- Physics integration
- Spatial UI
- Compose-native 3D layouts

But Core #1 intentionally remains focused and pragmatic.

