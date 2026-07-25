# Official Spatial AI Knowledge Base

# 1. Knowledge Base Metadata
- **Project Name**: Spatial
- **Repository**: danielitoCode/Spatial
- **Purpose**: Canonical knowledge base for AI assistants specialized in the Spatial library.
- **Knowledge Generation Date**: 2025-03-08
- **Repository State Analyzed**: Core #1 (In Development)
- **Current Version**: 0.1.0-alpha01 (Verifiable via Gradle build logic)
- **Current Development Status**: In Development / Experimental
- **Primary Source of Truth**: Source Code and verified tests.

---

# 2. Project Identity
**Spatial** is a modern declarative 3D rendering library for Android, deeply inspired by the mental model of Jetpack Compose. 

### Core Purpose
Spatial solves the problem of high complexity in Android 3D development by hiding OpenGL ES details behind a reactive, declarative API. It is designed for developers who want to integrate cinematic 3D scenes into their apps without building a full game engine.

### Design Philosophy
- **Declarative**: You describe the state of the 3D scene; the library handles the rendering.
- **Reactive**: UI state changes automatically trigger scene updates.
- **Compose-first**: Seamlessly integrates with the Jetpack Compose lifecycle and state management.
- **Cinematic**: Prioritizes motion quality (smooth camera, interpolation, damping) over feature count.

---

# 3. Core Concepts

### Scene
The entry point for 3D rendering. It acts as the 3D canvas that hosts elements and manages the camera.
- **API**: `com.elitec.spatial_compose.Scene`

### Element
The building blocks of a scene. Elements represent 3D objects like primitives or external models.
- **API**: `com.elitec.spatial_compose.Element`

### Modifier3D
A sequence of operations applied to an Element to define its position, rotation, scale, and size.
- **API**: `com.elitec.spatial_compose.Modifier3D`

### CameraState
Manages the orbital camera's position (`yaw`, `pitch`) and `zoom`. Supports both instant jumps and smooth animations.
- **API**: `com.elitec.spatial_compose.CameraState`

### Units System
Uses type-safe units (`Distance` and `Angle`) to avoid "magic floats" and ensure spatial consistency.
- **API**: `com.elitec.spatial_units`

---

# 4. Installation and Setup

### Current Status
Spatial is currently in early development. It is structured as a multi-module Gradle project.

### Required Environment
- **Kotlin**: 2.2.10
- **Android Gradle Plugin (AGP)**: 9.2.1
- **Minimum SDK**: 21 (for basic rendering) / 24+ (for full feature support)
- **OpenGL**: GLES 3.0+

### Gradle Configuration (Example for local usage)
To use Spatial in a project, you need to include the modules in your `settings.gradle.kts`:
```kotlin
include(":spatial-compose")
include(":spatial-compose-runtime-adapter")
include(":spatial-units")
// ... and other required modules
```

---

# 5. Quick Start
The following example creates a basic 3D scene with an orbiting cube and sphere.

```kotlin
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.elitec.spatial_compose.Element
import com.elitec.spatial_compose.Gestures
import com.elitec.spatial_compose.Modifier3D
import com.elitec.spatial_compose.Scene
import com.elitec.spatial_compose.rememberCameraState
import com.elitec.spatial_compose_runtime_adapter.DefaultSceneRenderHostFactory
import com.elitec.spatial_units.deg
import com.elitec.spatial_units.meters

@Composable
fun MyFirstSpatialScene() {
    // 1. Initialize Camera State
    val cameraState = rememberCameraState(
        yaw = 45f.deg,
        pitch = (-15f).deg,
        zoom = 1.0f
    )

    // 2. Define the Scene
    Scene(
        modifier = Modifier.fillMaxSize(),
        renderHostFactory = DefaultSceneRenderHostFactory,
        cameraState = cameraState,
        gestures = Gestures.orbitAndZoom() // Enable touch controls
    ) {
        // 3. Add 3D Elements
        Element.Cube(
            modifier = Modifier3D.Default
                .size(1f.meters)
                .position(0f, 0f, 0f)
        )

        Element.Sphere(
            modifier = Modifier3D.Default
                .size(0.5f.meters)
                .position(2f.meters, 0f.meters, 0f.meters)
        )
    }
}
```
### Explanation
1. `rememberCameraState`: Creates a reactive state for the orbital camera.
2. `Scene`: The host composable. `DefaultSceneRenderHostFactory` provides the actual OpenGL implementation.
3. `Element.Cube`/`Sphere`: Declarative components that exist within the `Scene`.
4. `Modifier3D`: Used to transform objects. `1f.meters` and `45f.deg` use the type-safe units system.

---

# 6. Public API Reference

### `com.elitec.spatial_compose` (Root Package)
| Name | Type | Purpose |
| ---- | ---- | ------- |
| `Scene` | Composable | Primary host for 3D content. |
| `Element` | Object | Namespace for 3D primitives (`Cube`, `Sphere`, `Plane`, `Model`). |
| `Modifier3D` | Class | Builder for 3D transformations. |
| `CameraState` | Class | Controller for camera position and animations. |
| `Gestures` | Object | Factory for gesture configurations (`orbit`, `orbitAndZoom`). |
| `MotionSpec` | Class | Configuration for animations (`Adaptive`, `Instant`). |
| `rememberCameraState` | Function | Helper to create and remember `CameraState`. |
| `ModelResource` | Interface | Reference to external 3D assets (e.g., `fromRawResource`). |

### `com.elitec.spatial_units`
| Symbol | Extension | Purpose |
| ------ | --------- | ------- |
| `meters` | `Float/Int` | Creates a `Distance` unit. |
| `cm` | `Float/Int` | Creates a `Distance` unit from centimeters. |
| `deg` | `Float/Int` | Creates an `Angle` unit from degrees. |

---

# 7. Scene System
Scenes in Spatial are **stateless facades** over an internal scene graph.
- **Graph Construction**: The `content` block of a `Scene` uses a `SceneBuilder` to collect `SceneNode` instances.
- **Recomposition**: When Compose recompositions occur, the scene graph is updated and the new state is submitted to the renderer.
- **Lifecycle**: `Scene` automatically handles Android View lifecycle (Pause/Resume) through the `SceneRenderHost`.

---

# 8. Geometry System

### Supported Primitives (Verified)
- **Cube**: `Element.Cube(modifier)`.
- **Sphere**: `Element.Sphere(modifier)`.
- **Plane**: `Element.Plane(modifier)`.

### External Models (Verified)
- **glTF Binary (.glb)**: Supported via `Element.Model(model, modifier)`.
- **Loading**: Use `ModelResource.fromRawResource(R.raw.my_model)`.
- **Parser**: `GltfBinaryParser` extracts vertex positions and indices.

### Transforms
All geometries support:
- `.position(x, y, z)`
- `.rotateX/Y/Z(angle)`
- `.scale(x, y, z)`
- `.size(all)` or `.size(w, h, d)`

---

# 9. Camera System
Spatial uses an **Orbital Camera** model as its primary system.

- **State Properties**: `yaw` (horizontal rotation), `pitch` (vertical rotation), `zoom` (visual magnification).
- **Clamps**:
    - **Pitch**: Clamped between -89° and 89° to avoid gimbal lock.
    - **Zoom**: Clamped between 0.3x and 4.0x.
- **Cinematic Motion**: 
    - `cameraState.animateTo(...)` uses adaptive duration based on angular distance.
    - `cameraState.orbitBy(...)` and `zoomBy(...)` support smooth inertia and damping.

---

# 10. Gesture System
Gestures are wired directly into the `Scene` via the `gestures` parameter.

- **Orbit**: One-finger drag to rotate the camera.
- **Pinch Zoom**: Two-finger pinch to adjust the zoom level.
- **Sensitivity**: Controlled via `GestureSensitivity`. `Adaptive` is the default.

---

# 11. Motion and Animation
Spatial features an "Adaptive Motion" system.

### `MotionSpec`
- **Adaptive**: Automatically calculates duration and easing for the smoothest feel.
- **Instant**: Jumps to the target without interpolation.
- **Tween**: (Internal/Planned) fixed duration transitions.

###Shortest Path
The system always calculates the `shortestAngleDelta` for rotations (e.g., rotating from 350° to 10° will rotate +20° instead of -340°).

---

# 12. Materials
**PBR (Physically Based Rendering) is NOT currently implemented.**

### Current Capabilities (Core #1)
- **Flat Color**: Objects render with a solid base color.
- **Hardcoded Palette**: Primitives have default colors (Orange for Cube, Blue for Sphere, Slate for Plane).
- **Opacity**: Controlled via `Color4` alpha channel (Internal).

---

# 13. Lighting and Rendering

- **Pipeline**: OpenGL ES 3.0.
- **Shaders**: Simple Vertex/Fragment shaders. Fragment shader outputs a solid color.
- **Active Lighting**: `LightData` exists as a contract but **lighting evaluation is NOT implemented** in the current renderer.
- **Shadows**: **NOT supported** in Core #1.

---

# 14. Architecture
Spatial follows a **Modular, Feature-First** architecture with strict dependency directions.

### Main Layers
1. **Compose Layer (`spatial-compose`)**: Declarative UI and state management.
2. **Runtime Layer (`spatial-runtime`)**: Orchestrates frame scheduling and camera logic.
3. **Renderer Layer (`spatial-renderer`)**: Pure OpenGL ES implementation.
4. **Foundation Layer (`spatial-math`, `spatial-units`)**: Dependency-free math and units.

### Dependency Graph (High-Level)
`Compose -> Core -> Scene -> Renderer`

---

# 15. Module Reference

| Module | Purpose | Status |
| ------ | ------- | ------ |
| `:app` | Playground / Demo app. | Active |
| `:spatial-compose` | Compose Composable and public API. | Active |
| `:spatial-renderer` | OpenGL ES 3.0 rendering logic. | Active |
| `:spatial-camera` | Orbital camera logic and damping. | Active |
| `:spatial-math` | JVM-pure matrix and vector math. | Stable |
| `:spatial-units` | Type-safe units (meters, deg). | Stable |
| `:spatial-geometry` | Mesh generation and glTF parsing. | Active |
| `:spatial-core` | Shared contracts and orchestration. | Stable |
| `:spatial-motion` | Animation duration and easing planners. | Active |
| `:spatial-gesture` | Android MotionEvent processing. | Active |

---

# 16. API and Package Map
- **Public Entry Point**: `com.elitec.spatial_compose`
- **Units**: `com.elitec.spatial_units`
- **Internal Scene**: `com.elitec.spatial_compose.scene`
- **Internal State**: `com.elitec.spatial_compose.state`
- **Internal Math**: `com.elitec.spatial_math`

---

# 17. Examples and Usage Patterns

### Problem: Animating the camera to a new view.
**Solution**: Use `cameraState.animateTo`.
```kotlin
val cameraState = rememberCameraState()
LaunchedEffect(Unit) {
    cameraState.animateTo(yaw = 90f.deg, pitch = 20f.deg, zoom = 2f)
}
```

### Problem: Loading a custom model from resources.
**Solution**: Use `ModelResource.fromRawResource`.
```kotlin
Element.Model(
    model = ModelResource.fromRawResource(R.raw.car_model),
    modifier = Modifier3D.Default.size(5f.meters)
)
```

---

# 18. Current Capabilities
- **Stable**: Matrix math, units system, basic orbit camera.
- **Experimental**: Declarative `Scene` API, glTF parsing, adaptive animation.
- **In Development**: Shader caching, multi-mesh models.

---

# 19. Explicit Limitations (HALLUCINATION PREVENTION)
- **NO PBR**: Physically Based Rendering is not implemented.
- **NO Active Lighting**: Light evaluation in shaders is not supported.
- **NO Shadows**: No shadow mapping or casting.
- **NO Physics**: No rigid body or soft body physics.
- **NO ECS**: Not an Entity Component System based engine.
- **NO Skeletal Animation**: No support for rigged meshes.
- **NO Post-processing**: No bloom, HDR, or depth of field.
- **NO Vulkan**: Renderer is strictly OpenGL ES 3.0+.

---

# 20. Roadmap and Future Features
- **glTF PBR Support**: `PLANNED`
- **Active Point/Directional Lights**: `PLANNED`
- **Texture Support**: `PLANNED`
- **Vulkan Backend**: `PLANNED`

---

# 21. Troubleshooting

### Scene is black or objects are missing
- Verify that `DefaultSceneRenderHostFactory` is passed to the `Scene`.
- Check if your `position` is within a reasonable distance (default camera is at 10 units).
- Ensure your GLB file contains a valid mesh with `POSITION` attributes.

### Animation is not working
- Ensure you are calling `animateTo` from a Coroutine scope.
- Verify that you are reading from `cameraState` properties in your Compose code to register observation.

---

# 22. Frequently Asked Questions

**Q: Can I use Spatial with standard Android Views?**
A: Yes, via `SpatialGlSurfaceView` in the `:spatial-renderer` module, but the recommended way is using the Compose `Scene` wrapper.

**Q: Does it support `.obj` or `.fbx`?**
A: Currently, only `.glb` (glTF Binary) is verified via `GltfBinaryParser`.

**Q: Why does 45.meters not compile?**
A: You must import the extension: `import com.elitec.spatial_units.meters`.

---

# 23. Decision Rules for the Future AI Assistant
1. **Source Code Priority**: Always check `spatial-compose` for the latest public API signatures.
2. **Never Invent**: If an Element doesn't have a `color` parameter in the source, do not suggest one.
3. **Be Explicit**: Always state that Lighting and PBR are NOT supported in Core #1.
4. **Units First**: Always use `.meters` and `.deg` in examples.
5. **Verify Modules**: Ensure correct module names (e.g., `spatial-math` vs `spatial-common`).

---

# 24. AI Response Guidelines
- Provide direct, concise answers.
- Use the **Quick Start** example as a template for most "how-to" questions.
- For unsupported features, explain the **Roadmap** status if known.

---

# 25. Knowledge Gaps
- **Shader Caching**: It is unclear from the current code if shaders are cached across different `Scene` instances in the same process.
- **Texture Mapping**: While `MeshData` has a `UV` coordinate concept, the current `SpatialGlRenderer` fragment shader does not appear to bind or use textures.
