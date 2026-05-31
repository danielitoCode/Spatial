# 🌌 Spatial

<div align="center">

![Spatial Logo](https://img.shields.io/badge/Spatial-Declarative%203D-blueviolet?style=for-the-badge)

<img src="banner.png" alt="Spatial Banner" width="100%" />

![Kotlin](https://img.shields.io/badge/Kotlin-2.x-orange?style=flat-square&logo=kotlin)
![Android](https://img.shields.io/badge/Android-OpenGL%20ES%203.0-green?style=flat-square&logo=android)
![Compose](https://img.shields.io/badge/Jetpack-Compose-blue?style=flat-square)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20Feature%20First-red?style=flat-square)
![Status](https://img.shields.io/badge/Core%20%231-In%20Development-yellow?style=flat-square)

### Declarative 3D Rendering for Android inspired by Jetpack Compose

*Modern • Reactive • Cinematic • GPU-Abstracted*

</div>

---

# ✨ Vision

**Spatial** is a modern declarative 3D rendering library for Android inspired by Jetpack Compose.

Its goal is to make 3D scene creation feel as natural and expressive as Compose, while completely hiding the complexity of OpenGL and GPU pipelines from the developer.

Spatial is **not** intended to become a full game engine.

Instead, it focuses on:

- Declarative scene composition
- Smooth cinematic motion
- State-driven rendering
- Compose-first APIs
- Natural gestures
- Modular rendering architecture
- Clean GPU abstraction

---

# 🎯 Core #1 Goals

Core #1 focuses on building the foundation for a premium 3D experience:

## Included

- Essential 3D primitives
- Orbit camera
- Smooth zoom
- Inertia and damping
- Declarative scene API
- Basic camera/transform motion system
- Gesture system
- Flat-color material rendering (no active lighting/shading in Core #1)
- Material abstraction
- Units system
- GPU abstraction layer
- Compose integration

## Intentionally Excluded

- Physics
- ECS
- PBR
- Shadows
- Real lighting and shaded light evaluation
- Vulkan
- Skeletal animation
- Post-processing
- Advanced animation timelines and clip sequencing
- Multiplayer systems
- Editor tooling

---
## Core #1 Lighting Decision

Core #1 keeps lighting as contracts only. `LightData` exists so scene, light, and future renderer modules can agree on shape, direction, color, and intensity metadata, but Core #1 does **not** transport lights through the render frame and does **not** evaluate real lighting in shaders.

The active Core #1 renderer supports flat-color materials: the material color is passed directly to the shader without directional, ambient, point, or physically based light contribution.

---

# 🧠 Core Philosophy

Spatial follows a strict design philosophy:

## Declarative

Scenes describe state.

---

## Reactive

State changes update rendering automatically.

---

## Compose-first

Inspired by Compose mental models and APIs.

---

## Cinematic

Motion quality matters more than feature quantity.

---

## Opinionated

Good defaults and minimal boilerplate.

---

# 🧩 Example API

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

# 🏗️ Architecture

Spatial uses a hybrid architecture:

# Clean Architecture
+
# Feature First
+
# Rendering-Oriented Pragmatism

The architecture prioritizes:

- Ownership boundaries
- Module independence
- Rendering performance
- Scalability
- Low coupling
- Maintainability

Spatial avoids unnecessary enterprise abstractions such as:

- Excessive repositories
- DTO overengineering
- Artificial use cases
- Massive inheritance hierarchies

---

# 📦 Workspace Structure

```text
spatial/
│
├── app/
│
├── spatial-renderer/
├── spatial-scene/
├── spatial-camera/
├── spatial-motion/
├── spatial-gesture/
├── spatial-material/
├── spatial-geometry/
├── spatial-light/
├── spatial-math/
├── spatial-units/
├── spatial-compose/
└── spatial-core/
```

---

# 🧱 Module Responsibilities

# app/

## Type
Android Application

## Purpose
Playground and showcase application.

Used for:
- Rendering validation
- Gesture testing
- Motion tuning
- FPS monitoring
- Demo scenes
- Visual experimentation

---

# spatial-renderer/

## Type
Android Library

## Purpose
Shared Core #1 motion primitives for basic camera and transform animation.

## Responsibilities
- Basic camera/transform interpolation
- Shared easing curves
- Shared camera animation duration planning
- Runtime/Compose motion parity helpers

## Out of scope for Core #1
- Skeletal animation
- Advanced keyframe timelines
- Clip blending and sequencer-style orchestration

## Internal Features

```text
renderer/
 ├── shader/
 ├── pipeline/
 ├── buffer/
 ├── texture/
 ├── framebuffer/
 ├── renderloop/
 └── context/
```

---

# spatial-scene/

## Type
Kotlin Library

## Purpose
Scene graph and hierarchical structure.

## Responsibilities
- Node hierarchy
- Transform propagation
- Traversal
- Visibility management
- Dirty flags
- Parent-child relationships

---

# spatial-camera/

## Type
Kotlin Library

## Purpose
Camera behavior and cinematic motion.

## Responsibilities
- Orbit camera
- Pan
- Zoom
- Damping
- Inertia
- Smooth transitions
- Projection systems

## Internal Features

```text
camera/
 ├── orbit/
 ├── zoom/
 ├── inertia/
 ├── interpolation/
 └── projection/
```

---

# spatial-motion/

## Type
Kotlin Library

## Purpose
Animation and motion engine.

## Responsibilities
- Animation timelines
- Interpolation
- Spring systems
- Easing
- Transitions
- Motion orchestration

---

# spatial-gesture/

## Type
Android Library

## Purpose
Touch and gesture input system.

## Responsibilities
- Multi-touch
- Pinch zoom
- Orbit gestures
- Velocity tracking
- Gesture smoothing
- MotionEvent handling

---

# spatial-material/

## Type
Kotlin Library

## Purpose
Material abstraction layer.

## Responsibilities
- Flat-color materials for Core #1
- Texture materials (future)
- Shader metadata (future)
- Material bindings
- Lighting properties as metadata only; active light evaluation is outside Core #1

---

# spatial-geometry/

## Type
Kotlin Library

## Purpose
Mesh and primitive generation.

## Responsibilities
- Cube generation
- Sphere generation
- Cylinder generation
- Plane generation
- Mesh data
- Normals
- UV coordinates

---

# spatial-light/

## Type
Kotlin Library

## Purpose
Lighting models.

## Responsibilities
- Light contracts and helper factories
- Directional light metadata
- Light intensity metadata
- Light direction metadata

## Out of scope for Core #1
- Active lighting in shaders
- Shaded material evaluation
- Shadows

---

# spatial-math/

## Type
Kotlin Library

## Purpose
Pure mathematical foundation.

## Responsibilities
- Vec2
- Vec3
- Vec4
- Quaternion
- Matrix4
- Projection math

## Important
This module must remain:
- Lightweight
- Pure Kotlin
- Allocation-friendly
- Dependency-free

---

# spatial-units/

## Type
Kotlin Library

## Purpose
Consistent spatial units system.

## Responsibilities
- Meters
- Centimeters
- Degrees
- Unit conversions
- Spatial consistency

## Example

```kotlin
1.meters
45.deg
50.cm
```

---

# spatial-compose/

## Type
Android Library

## Purpose
Declarative Compose integration layer.

## Responsibilities
- Scene composables
- Element composables
- Modifier3D
- remember states
- Compose adapters

## Important
This module does NOT render directly.

It orchestrates:
- state
- composition
- declarative APIs

---

# spatial-core/

## Type
Kotlin Library

## Purpose
Core orchestration and public contracts.

## Responsibilities
- Public APIs
- Module orchestration
- Shared contracts
- Engine coordination

---

# 🔄 Dependency Direction

```text
Compose
   ↓
Core
   ↓
Scene
   ↓
Renderer
```

Rules:
- High-level modules never depend on UI.
- Renderer never knows Compose.
- Scene never knows Android.
- Math remains framework-independent.

---

# 🧪 Development Workflow

Spatial is developed using a parallel playground application.

## Recommended workflow

```text
Modify renderer
      ↓
Run playground
      ↓
Validate motion
      ↓
Tune gestures
      ↓
Refactor APIs
      ↓
Repeat
```

---

# 🎮 Playground Purpose

The playground app acts as:

- Rendering sandbox
- Motion laboratory
- Camera tuning environment
- FPS validator
- Material previewer
- Demo showcase

---

# 🧰 Technology Stack

## Core Technologies

- Kotlin
- OpenGL ES 3.0+
- Jetpack Compose
- Coroutines
- Gradle Kotlin DSL

---

## Dependency Injection

Recommended:
- Koin (limited usage)

Use DI ONLY for:
- Renderer services
- Shader registries
- Texture caches
- Motion orchestrators

Avoid DI for:
- Vec3
- Matrix4
- Geometry
- Units
- Transforms

---

# 📐 Units System

Spatial avoids arbitrary floats whenever possible.

Example:

```kotlin
Modifier3D
    .size(2.meters)
    .rotateY(45.deg)
```

This improves:
- readability
- spatial consistency
- API ergonomics

---

# 🎥 Motion System

Motion quality is one of Spatial's highest priorities.

Core #1 focuses heavily on:
- smooth orbit camera
- cinematic transitions
- natural zoom
- inertia
- damping
- interpolation

The renderer should feel premium even with simple cubes.

---

# 🚀 Development Phases

# Phase 1
Rendering foundations:
- EGL
- OpenGL setup
- Triangle rendering
- Render loop

---

# Phase 2
Math engine:
- Vec3
- Matrix4
- Quaternion

---

# Phase 3
Camera system:
- Orbit
- Zoom
- Inertia
- Gestures

---

# Phase 4
Scene graph:
- Nodes
- Hierarchy
- Traversal

---

# Phase 5
Geometry:
- Cube
- Sphere
- Plane
- Cylinder

---

# Phase 6
Materials:
- Solid materials
- Texture materials
- Basic lighting

---

# Phase 7
Compose DSL:
- Scene
- Element
- Modifier3D

---

# Phase 8
Basic motion system:
- animateTo for camera/transform changes
- interpolation
- - shared easing/duration planning

Advanced timelines, skeletal animation, and clip sequencing are intentionally outside Core #1.

---

# Phase 9
Polish:
- smooth gestures
- frame pacing
- shader caching
- lifecycle cleanup

---

# Phase 10
Showcase playground.

---

# ✅ Core #1 Success Criteria

Core #1 is complete when:

- Primitives render correctly
- Camera feels cinematic
- Zoom feels smooth
- Gestures feel natural
- Motion transitions work
- Compose integration works
- API feels elegant
- 60 FPS remain stable
- OpenGL complexity stays hidden

---

# 🔮 Long-Term Vision

Potential future expansions:

- glTF loading
- PBR
- Real lighting and shaded light evaluation
- Shadows
- Vulkan backend
- Compose Multiplatform
- Spatial UI
- Physics integration
- WebGPU backend

---

# 💡 Guiding Principle

> Less GPU complexity. More declarative intent.

---

<div align="center">

### Spatial — Declarative 3D Rendering for Android

Built for modern Android graphics experimentation.

</div>
