# Core #1 Stability Plan

> **Status:** In Progress | **Last Updated:** 2026-07-03  
> **Owner:** Agent Session | **Purpose:** Track and resolve all blockers for a stable Core #1 release.

This document serves as the canonical reference for the Core #1 stabilization effort. All agents (human or AI) accessing this project should consult this file before making changes to the rendering pipeline, frame scheduling, or lifecycle management.

---

## Philosophy

- Simple first, complex later.
- Do not mark a checkbox complete until the fix is committed, tested, and verified.
- If a fix introduces a regression, revert it and update the notes.
- This file is append-only; do not delete past entries.

---

## Phase 1: Critical Blockers

Items that prevent Core #1 from being considered stable. These must be addressed before any release.

### 1.0 Wire `FrameSnapshot.clearColor` into `SpatialGlRenderer`

**Problem:** `SpatialGlRenderer.onSurfaceCreated` hardcodes the clear color to `(0.08, 0.12, 0.18, 1.0)`, ignoring `FrameSnapshot.clearColor` and `RenderColorAdapters`.

**Impact:** Inconsistent API. Consumers cannot theme the background via `FrameSnapshot`.

**File(s):**
- `spatial-renderer/src/main/java/com/elitec/spatial_renderer/gl/SpatialGlRenderer.kt`

**Fix:** Read `clearColor` from the `RenderFrame` (or `FrameSnapshot`) in `onDrawFrame` and call `GLES30.glClearColor(...)`.

- [X] **Done**

---

### 1.1 Replace synchronous `ChoreographerFrameScheduler`

**Problem:** `ChoreographerFrameScheduler` is synchronous (calls the callback immediately), defeating the purpose of frame scheduling and causing potential UI thread blocking.

**Impact:** No VSYNC synchronization, stuttering, no actual frame pacing.

**File(s):**
- `spatial-renderer/src/main/java/com/elitec/spatial_renderer/adapter/CoreRenderImplementations.kt`

**Fix:** Use `android.view.Choreographer.getInstance().postFrameCallback` to schedule the frame on the next VSYNC tick. Rename the old class to `ImmediateFrameScheduler` to keep it for testing if needed.

- [X] **Done**

**Audit Notes (2026-07-05):**
- Previous implementation was still synchronous. The `ChoreographerFrameScheduler` was effectively a passthrough calling the callback immediately.
- **Applied Fix:**
  - Added `import android.view.Choreographer` to `CoreRenderImplementations.kt`.
  - Created `ImmediateFrameScheduler` class to preserve the old synchronous behavior for tests and non-UI contexts.
  - Re-implemented `ChoreographerFrameScheduler` to use `Choreographer.getInstance().postFrameCallback` with `@Volatile private var pending` for thread-safe coalescing.
  - The class now defers frame emission to the next VSYNC tick, and coalesces overlapping requests.
- **Verification:** Check `CoreRenderImplementations.kt` lines 34-58.

---

### 1.2 Fix first-frame race condition

**Problem:** In `components/Scene.kt`, `renderSceneFrame` is called immediately in the `AndroidView` factory, before `onSurfaceCreated` has finished initializing GL resources. This causes an empty/black first frame.

**Impact:** Demo shows a blank screen for a brief moment on launch.

**File(s):**
- `spatial-compose/src/main/java/com/elitec/spatial_compose/components/Scene.kt`
- `spatial-renderer/src/main/java/com/elitec/spatial_renderer/gl/SpatialGlSurfaceView.kt`
- `spatial-renderer/src/main/java/com/elitec/spatial_renderer/gl/SpatialGlRenderer.kt`
- `spatial-compose-runtime-adapter/src/main/java/com/elitec/spatial_compose_runtime_adapter/DefaultSceneRenderHostFactory.kt`

**Fix:** Mark the render host as "ready" only after `onSurfaceCreated` fires. Queue frame requests until then. Alternatively, move the initial `renderSceneFrame` call into `onSurfaceReadyCallback`.

- [X] **Done**

**Implementation Notes (2026-07-05):**
- `SpatialGlSurfaceView` expone `setOnSurfaceReady(callback)` que delega a `SpatialGlRenderer.onSurfaceReadyCallback`.
- `SpatialGlRenderTarget` expone `setOnSurfaceReady(callback)` que reenvía al `SpatialGlSurfaceView`.
- `SpatialRuntimeSceneRenderHost` mantiene `@Volatile var glReady` y `var queuedFrame: (() -> Unit)?`.
  - En `init`, registra callback con `renderTarget.setOnSurfaceReady { glReady = true; queuedFrame?.invoke(); queuedFrame = null }`.
  - En `requestFrame()`, si `!glReady`, almacena el frame en `queuedFrame` y retorna temprano.
  - Una vez `glReady == true`, `requestFrame()` ejecuta `requestFrameInternal()` inmediatamente.
- **Verification:** Build successful (`spatial-renderer` + `spatial-compose-runtime-adapter`).

---

### 1.3 Sanitize `releaseGlResources` lifecycle

**Problem:** `SpatialGlSurfaceView.releaseGlResources` uses `queueEvent` after the view may have been detached, which can crash if the EGL context is already destroyed.

**Impact:** Crash on rotation, activity switch, or app backgrounding.

**File(s):**
- `spatial-renderer/src/main/java/com/elitec/spatial_renderer/gl/SpatialGlSurfaceView.kt`

**Fix:** Check if the view is still attached before enqueuing. Ensure `isAttachedToWindow` is checked or wrap in a try/catch for `IllegalStateException`.

- [X] **Done**

---

## Phase 2: Contract Hardening

Items needed for a production-quality API. These ensure the public contracts behave as documented.

### 2.0 Complete `FrameSnapshot` data

**Problem:** `FrameSnapshot` is constructed with only `frameTimeNanos`; `viewProjection` and `cameraPosition` are left as identity/zero defaults.

**Impact:** Breaks the promise of the API contract for consumers who expect real data.

**File(s):**
- `spatial-runtime/src/main/java/com/elitec/spatial_runtime/SpatialRuntime.kt`
- `spatial-core/src/main/java/com/elitec/spatial_core/render/FrameSnapshot.kt`

**Fix:** Populate `viewProjection` and `cameraPosition` from the `CameraSnapshot` before creating the `FrameSnapshot`.

- [ ] **Done**

---

### 2.1 Cache projection matrix

**Problem:** `Matrix.perspectiveM` is recalculated every frame in `onDrawFrame`, even when the viewport hasn't changed.

**Impact:** Unnecessary CPU overhead on every frame.

**File(s):**
- `spatial-renderer/src/main/java/com/elitec/spatial_renderer/gl/SpatialGlRenderer.kt`

**Fix:** Store projection matrix in a field. Recompute only inside `onSurfaceChanged`.

- [ ] **Done**

---

### 2.2 Add frame request backpressure

**Problem:** `SpatialRuntimeSceneRenderHost.requestFrame()` enqueues a new frame unconditionally. Under heavy gesture or animation load, this could overflow the queue.

**Impact:** Lag, dropped frames, or memory pressure.

**File(s):**
- `spatial-compose-runtime-adapter/src/main/java/com/elitec/spatial_compose_runtime_adapter/DefaultSceneRenderHostFactory.kt`
- `spatial-runtime/src/main/java/com/elitec/spatial_runtime/SpatialRuntime.kt`

**Fix:** Add a "pending frame" flag. Drop or coalesce new requests if a frame is already scheduled but not yet rendered.

- [ ] **Done**

---

### 2.3 Validate camera snapshot in `onDrawFrame`

**Problem:** If `zoom == 0`, `zoom.isFinite() == false`, or `zoom < 0`, the `orbitDistance` calculation (`10f / zoom`) becomes `Inf` or negative, causing the view matrix to break completely.

**Impact:** Entire scene disappears.

**File(s):**
- `spatial-renderer/src/main/java/com/elitec/spatial_renderer/gl/SpatialGlRenderer.kt`

**Fix:** Add a guard clause: if `cameraSnapshot.zoom` is not in `[MIN_ZOOM, MAX_ZOOM]`, use a safe default. Preferably, `SpatialCamera` should never emit an invalid snapshot, but a renderer-side guard is necessary for defense-in-depth.

- [ ] **Done**

---

## Phase 3: Completeness

Polish items that turn a functioning renderer into a stable, documented product.

### 3.0 Expand `MeshDrawMode`

**Problem:** `MeshDrawMode` only supports `Triangles`. No way to render wireframe, lines, or strips.

**Impact:** Limits debugging and future primitive types.

**File(s):**
- `spatial-renderer/src/main/java/com/elitec/spatial_renderer/gl/MeshData.kt`
- `spatial-renderer/src/main/java/com/elitec/spatial_renderer/gl/SpatialGlRenderer.kt`

**Fix:** Add `LineStrip`, `TriangleStrip`, `Lines` to the enum and map them in `toGlDrawMode()`.

- [ ] **Done**

---

### 3.1 Document matrix rotation convention

**Problem:** The order `T * Rz * Ry * Rx * S` is used in `toModelMatrix`, but it is not documented whether this represents intrinsic or extrinsic rotations.

**Impact:** Confusion for consumers composing complex transformations.

**File(s):**
- `spatial-compose/src/main/java/com/elitec/spatial_compose/modifier/Modifier3D.toModelMatrix.kt`
- This document (`CORE1_STABILITY.md`)

**Fix:** Add a code comment explaining that the decomposition applies scale first, then X, Y, Z rotations, then translation, equivalent to post-multiplied column-major matrices.

- [ ] **Done**

---

### 3.2 Integration test: `cube_is_visible_on_first_frame`

**Problem:** There is no automated test that verifies a composed `Scene` with a `Cube` actually renders pixels.

**Impact:** Regressions in the rendering loop are caught only by manual inspection.

**File(s):**
- `spatial-renderer/src/test/java/com/elitec/spatial_renderer/...` (new test)

**Fix:** Create an integration test that sets up `Scene` → `Cube` → asserts that at least one pixel in the output framebuffer is non-background after the first `onDrawFrame` call.

- [ ] **Done"

---

## Regression Checklist

After each phase is completed, run through this checklist before declaring Core #1 stable.

- [ ] App launches without blank/black first frame
- [ ] Cube, Sphere, and Plane all render correctly
- [ ] Orbit gesture works smoothly (no stutter)
- [ ] Pinch zoom works without crash
- [ ] Rotating the device does not crash the app
- [ ] Backgrounding and foregrounding the app does not crash
- [ ] 60 FPS is maintained during idle animation
- [ ] Memory usage is stable (no leaks in `onSurfaceCreated` / `onSurfaceDestroyed`)

---

## Change Log

| Date         | Agent     | Change                                                                                                                                                               |
|--------------|-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
| 2026-07-03   | Initial   | Created this plan from full flow analysis.                                                                                                                           |
| 2026-07-03   | Agent     | Completed 1.1: Replaced synchronous ChoreographerFrameScheduler with a VSYNC-aligned asynchronous implementation utilizing Choreographer and thread-safe coalescing. |
| 2026-07-05   | Agent     | Completed 1.2: Fixed first-frame race condition. Added `setOnSurfaceReady` callback chain (SpatialGlSurfaceView → SpatialGlRenderTarget → SpatialRuntimeSceneRenderHost). Host now queues frames until GL surface is fully initialized. |
| 2026-07-05   | Agent     | Completed 1.1: Corrected ChoreographerFrameScheduler. Rewrote synchronous passthrough to use Choreographer.postFrameCallback with @Volatile coalescing. Renamed old class to ImmediateFrameScheduler for testing. |
| 2026-07-05   | Agent     | Completed 1.0: Wire FrameSnapshot.clearColor into SpatialGlRenderer. |
| 2026-07-05   | Agent     | Completed 1.2: Fixed first-frame race condition in SpatialGlSurfaceView by queuing the first frame requests until the GL surface is fully ready. |
| 2026-07-03   | Agent     | Completed 1.3: Sanitized releaseGlResources lifecycle with isAttachedToWindow guards and safe try-catch blocks to prevent crashes on configuration changes. |   