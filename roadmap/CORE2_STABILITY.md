# Core #2 Stability Plan

> **Status:** Draft / Planning | **Last Updated:** 2026-07-18
> **Owner:** Agent Session | **Purpose:** Break `roadmap/CORE2_PROPOSAL.md` into
> verifiable microtasks and track their real implementation status.
>
> This document was created from a code-level audit against `CORE2_PROPOSAL.md`,
> not from the proposal text alone. Several items the proposal describes as
> future work are already implemented in `master`; several the proposal
> implies are simple are, on inspection, greenfield. Every checkbox below
> starts unchecked. None are marked `[X]` until the fix/feature is committed
> **and** verified, with the verification source stated explicitly — same
> discipline as `CORE1_STABILITY.md`.

This document is the canonical reference for Core #2 planning. Agents
picking up Core #2 work should consult and update this file before touching
the asset pipeline, layout system, or camera sequencing code.

---

## Philosophy

- Audit code, not just the proposal document — the proposal is a design intent, not a status report.
- Do not mark a checkbox complete until the fix is committed, tested, and verified.
- Distinguish verification sources explicitly: pure JVM/unit-test logic vs. Android-Gradle-Plugin build vs. on-device/instrumented confirmation.
- If an environment cannot run a given verification (no device, no emulator, no network to Google Maven), the checkbox stays unchecked and the reason is recorded — never rubber-stamped.
- This file is append-only; do not delete past entries, only add dated notes.

---

## Build infrastructure alignment (2026-07-19)

Separate from the Pilar 1/2/3 work above, but relevant to "ready to start
Core #2": the project owner is building a `build-logic` included-build
convention-plugin system (`spatial.base`, `spatial.android.library`, and
unregistered work-in-progress plugins for Compose/publishing/quality/
testing/documentation/binary-compatibility). This is infra, not roadmap
work, but two real bugs were found and fixed while checking whether it was
safe to build on:

- `spatial.android.library` was registered in
  `build-logic/plugins/build.gradle.kts` pointing at
  `SpatialAndroidLibraryPlugin`, but that class didn't exist anywhere in
  the codebase - applying the plugin ID would have thrown
  `ClassNotFoundException`. Fixed by adding the missing `Plugin<Project>`
  entry point (mirrors `SpatialBasePlugin`'s existing pattern exactly) and
  renaming the file that actually contained `AndroidLibraryConfiguration`
  to match its real class name.
- `Kotlin.JVM_TOOLCHAIN` in `build-logic`'s constants was `3` - not a real
  JDK toolchain version - while `Java.VERSION`/`Kotlin.JVM_TARGET`
  elsewhere in the same constants package both say Java 11. Fixed to `11`.
- `build-logic/plugins/build.gradle.kts` pinned its own AGP/Kotlin
  Gradle-plugin classpath to `8.12.3`/`2.2.20`, while the root project's
  `gradle/libs.versions.toml` pins `9.2.1`/`2.2.10`. A composite build
  resolving two different AGP/Kotlin classpaths for the same overall
  project is fragile. Aligned to match the root catalog.

**Status, deliberately left as-is:** only `spatial-core` currently applies
`spatial.base` (harmless - it only creates a `spatial` extension and
registers base tasks, no publishing/compose/lint configuration yet).
`PublishingConfiguration`, `TestingConfiguration`, and
`DependencyConfiguration` inside `build-logic` are still empty stubs. No
other module has been migrated, and `spatial.android.library` is not yet
applied anywhere. This is intentional: migrating the other 14 modules or
wiring publishing through `build-logic` before those stubs are finished
would either duplicate or conflict with the existing root
`build.gradle.kts` `subprojects{}` publishing convention (see
`PUBLISHING.md`) - the same class of "configured twice" bug already hit
and fixed twice in that convention. Migrate module-by-module once
`PublishingConfiguration` is implemented, not all at once.

---

## Baseline audit (2026-07-18)

Findings from comparing `CORE2_PROPOSAL.md` against the actual state of
`spatial-geometry`, `spatial-compose`, `spatial-renderer`, `spatial-camera`,
and `spatial-motion` on `master`:

- The GLB loading pipeline (`GltfBinaryParser` → `GlobalMeshRegistry` →
  `rememberModel` → JIT GPU upload in `SpatialGlRenderer`) is implemented
  and wired end to end, **except** the public entry point: `object Element`
  in `spatial-compose/core/Element.kt` only exposes `Cube`/`Sphere`/`Plane`.
  `Element.Model(...)` does not exist, even though `ModelSceneElement(...)`
  (its would-be implementation) already exists in
  `scene/rememberSceneGraph.kt`, and both `ModelResource.kt` and
  `Scene3D.kt` document `Element.Model(...)` in KDoc as if it were callable.
  This is the single highest-leverage fix in this document — see 1.0.
- `GltfBinaryParser` only reads the `POSITION` attribute of the first
  primitive of the first mesh; no `NORMAL`, no `TEXCOORD_0`, no multi-mesh
  support, no material data.
- `spatial-motion`'s `resolveCameraMotionPlan` explicitly documents its own
  scope limit in its KDoc: *"Skeletal animation, clip blending, keyframe
  timelines, and advanced sequencing belong to future motion systems layered
  above this primitive."* Core #2's Pilar 3 starts exactly where that
  primitive stops.
- `MotionEasing` only defines `Linear` and `SmoothStep`; the proposal's
  `FastInFastOut` and `CubicBezier` do not exist yet.
- `FixedStepCameraAnimationScheduler` drives animation with an artificial
  fixed 16ms step loop, not `Choreographer`/VSYNC. A working
  `ChoreographerFrameScheduler` already exists in
  `spatial-renderer/adapter/CoreRenderImplementations.kt` for the render
  loop and is a candidate to adapt/reuse for camera sequencing (3.2.0).
- No bounding-box, intrinsic-size, or layout code exists anywhere in the
  repository (`BoundingBox`, `AABB`, `IntrinsicSize`, `Box3D`, `Row3D`,
  `Column3D`, `Stack3D` all return zero matches). Pilar 2 is greenfield.

---

## Phase 1: Asset & Material Pipeline

### 1.0 Implement `Element.Model(...)`

**Problem:** The entire GLB loading pipeline is wired and functional
internally, but there is no public composable to invoke it. Consumers
cannot load a model at all through the documented API.

**File(s):**
- `spatial-compose/src/main/java/com/elitec/spatial_compose/core/Element.kt`

**Fix:** Add a `Model(model: ModelResource, modifier: Modifier3D = Modifier3D.Default)`
composable to `object Element` that delegates to the existing internal
`ModelSceneElement(model, modifier)`.

- [ ] **Planned**

### 1.1 Verify renderer applies transforms to `Model` nodes identically to `Primitive` nodes

**Problem:** Needs confirmation that `SpatialGlRenderer`'s per-node
transform/draw path treats `SceneNode.Model` the same as
`SceneNode.Primitive` once 1.0 makes `Model` nodes reachable from real apps.

**File(s):**
- `spatial-renderer/src/main/java/com/elitec/spatial_renderer/gl/SpatialGlRenderer.kt`

**Fix:** Code review + a JVM-level test of the node-to-draw-call mapping;
full confidence requires an on-device render, tracked separately in 1.7.

- [ ] **Planned**

### 1.2 Read `NORMAL` attribute in `GltfBinaryParser`

**Problem:** Only `POSITION` is parsed. Loaded models have no normals, so
`spatial-light` cannot shade them correctly (flat/incorrect lighting).

**File(s):**
- `spatial-geometry/src/main/java/com/elitec/spatial_geometry/GltfBinaryParser.kt`
- `spatial-geometry/src/main/java/com/elitec/spatial_geometry/MeshData.kt` (needs a normals field)

**Fix:** Extend `parseGltfJson` to read the `NORMAL` accessor when present,
extend `MeshData` to carry per-vertex normals, fall back gracefully
(e.g. flat/face-derived normals) when the source glTF omits them.

- [ ] **Planned**

### 1.3 Read `TEXCOORD_0` attribute in `GltfBinaryParser`

**Problem:** No UV data is parsed, blocking any future texture/material work.

**File(s):**
- `spatial-geometry/src/main/java/com/elitec/spatial_geometry/GltfBinaryParser.kt`
- `spatial-geometry/src/main/java/com/elitec/spatial_geometry/MeshData.kt`

**Fix:** Extend the parser and `MeshData` analogously to 1.2.

- [ ] **Planned**

### 1.4 Support multiple meshes/primitives

**Problem:** `parseGltfJson` hardcodes `meshes.list[0]` and
`primitives.list[0]`. A glTF file with more than one mesh or primitive
silently loses geometry with no warning.

**File(s):**
- `spatial-geometry/src/main/java/com/elitec/spatial_geometry/GltfBinaryParser.kt`

**Fix:** Either explicitly document and enforce the single-mesh/
single-primitive limitation (throw a clear error instead of silently
dropping data) as a near-term scope decision, or extend `MeshData`/the
renderer to carry a list of sub-meshes. Needs a scope decision before
implementation — flag for owner input.

- [ ] **Planned**

### 1.5 Material Override API

**Problem:** `CORE2_PROPOSAL.md` 1.1 calls for overriding a loaded model's
material via `Modifier3D`. No such API exists; `spatial-material` is not
wired to `Modifier3D` or to model loading at all yet.

**File(s):**
- `spatial-compose/src/main/java/com/elitec/spatial_compose/modifier/Modifier3D.kt`
- `spatial-material/` (new integration surface)

**Fix:** Design first (needs an ADR-style decision: does an override apply
per-primitive or per-model? does it require normals from 1.2 first for PBR
lighting to make sense?), then implement.

- [ ] **Planned**

### 1.6 Replace silent fallback-on-any-exception with a recognizable error mesh

**Problem:** `rememberModel` catches all exceptions during load and
silently substitutes `MeshData.FallbackTriangle`, with a `// TODO` in the
source acknowledging this. A malformed/corrupt model fails silently
in-place of a visible error signal.

**File(s):**
- `spatial-compose/src/main/java/com/elitec/spatial_compose/rememberModel.kt`

**Fix:** Introduce a distinct error-state mesh (e.g. a red cube, as the
existing TODO suggests) and surface the exception via logging at minimum.

- [ ] **Planned**

### 1.7 Instrumented on-device test: real `.glb` loads and renders

**Problem:** Nothing in this phase can be confirmed to actually render
correctly without a device/emulator running GPU code.

**File(s):** new instrumented test, mirroring `CubeRendersOnFirstFrameTest`.

- [ ] **Planned — requires device/emulator, not verifiable in this sandbox**

### 1.8 `GlobalMeshRegistry` eviction/lifecycle policy

**Problem:** `GlobalMeshRegistry.clear()` exists but nothing calls it.
Long-lived apps loading many distinct models will grow this registry
without bound across recompositions/scene changes.

**File(s):**
- `spatial-geometry/src/main/java/com/elitec/spatial_geometry/GlobalMeshRegistry.kt`

**Fix:** Define and implement an eviction policy (e.g. on scene disposal,
or LRU with a cap). Needs a design decision on trigger point before coding.

- [ ] **Planned**

### 1.9 Audit JIT GPU-upload path for races against registry mutation

**Problem:** `SpatialGlRenderer` uploads GL buffers just-in-time by
resolving `PrimitiveMeshRegistry.resolveOrNull` → falling back to
`GlobalMeshRegistry.get`. Core #1's history includes a real race condition
(`glReady`/`queuedFrame` check-then-act bug), so this JIT path deserves the
same scrutiny before being trusted under concurrent model loads.

**File(s):**
- `spatial-renderer/src/main/java/com/elitec/spatial_renderer/gl/SpatialGlRenderer.kt`
- `spatial-renderer/src/main/java/com/elitec/spatial_renderer/gl/PrimitiveMesh.kt`

**Fix:** Code review for check-then-act patterns across the GL thread vs.
the Compose/IO loading thread; add a regression test if a race is found.

- [ ] **Planned**

---

## Phase 2: 3D Layout System ("Spatial Box Model")

Fully greenfield — no bounding-box or layout code exists anywhere in the
repository as of this audit. Reordered from the proposal's 2.1-then-2.2 to
reflect an actual dependency: containers cannot position children correctly
without intrinsic size first.

### 2.0 `BoundingBox3D` value type

**File(s):** new, likely `spatial-math` or `spatial-geometry`.

**Fix:** Immutable min/max Vec3 type with derived `center`, `size` helpers.
Pure value type, fully unit-testable.

- [ ] **Planned**

### 2.1 Intrinsic bounding box for `PrimitiveShape`

**File(s):** `spatial-compose/.../shapes/PrimitiveShape.kt` (or a new
geometry-facing counterpart).

**Fix:** Deterministic bbox per shape type at a given `Modifier3D.size()`.
Pure function.

- [ ] **Planned**

### 2.2 Intrinsic bounding box for loaded `MeshData`

**File(s):** `spatial-geometry/src/main/java/com/elitec/spatial_geometry/MeshData.kt`

**Fix:** Min/max scan over `vertices`. Pure function, fully unit-testable
without a device — good candidate for immediate sandbox verification.

- [ ] **Planned**

### 2.3 `Measurable3D` / `Placeable3D` contract

**Problem:** Layout containers need a way to query a child's intrinsic size
and then assign it a final position — the 3D analogue of Compose UI's
`Measurable`/`Placeable`. This is an architecture decision, not a small fix.

**File(s):** new, likely `spatial-core`.

**Fix:** Design first (candidate for a short ADR given it shapes every
container below), then implement.

- [ ] **Planned**

### 2.4 `Box3D`

**Fix:** Overlays children at a shared origin; container size = the union
of children's bounding boxes.

- [ ] **Planned**

### 2.5 `Row3D`

**Fix:** Sequential placement along X using cumulative child widths from 2.3.

- [ ] **Planned**

### 2.6 `Column3D`

**Fix:** Sequential placement along Y.

- [ ] **Planned**

### 2.7 `Stack3D`

**Fix:** Sequential placement along Z (depth).

- [ ] **Planned**

### 2.8 `Spacing` support

**Fix:** Uniform gap parameter across `Row3D`/`Column3D`/`Stack3D`.

- [ ] **Planned**

### 2.9 `Alignment` support (Start/Center/End) on transverse axes

**Fix:** Cross-axis alignment for each sequential container.

- [ ] **Planned**

### 2.10 Unit tests for layout math

**Fix:** Pure JVM tests covering bbox computation and placement math for
all four containers. Fully verifiable in a sandbox without a device.

- [ ] **Planned**

### 2.11 On-device visual confirmation

**Problem:** `CORE2_PROPOSAL.md`'s stated acceptance criterion — two 1m
cubes in a `Row3D` sit exactly 1m apart, visually confirmed — requires a
real render.

- [ ] **Planned — requires device/emulator, not verifiable in this sandbox**

---

## Phase 3: Cinematic Motion (Camera Sequencer)

Foundational pieces exist (`MotionEasing`, `CameraAnimationSpec.Tween`,
`FixedStepCameraAnimationScheduler`, `resolveCameraMotionPlan`), but they
explicitly stop short of multi-keyframe sequencing per their own KDoc.

### 3.0 Extend `MotionEasing` with `FastInFastOut` and `CubicBezier`

**Problem:** Only `Linear` and `SmoothStep` exist today.

**File(s):**
- `spatial-motion/src/main/java/com/elitec/spatial_motion/SpatialMotion.kt`

**Fix:** Add both curves. Pure math, fully unit-testable.

- [ ] **Planned**

### 3.1 `CameraKeyframe` type and `CameraSequence` DSL

**Problem:** No keyframe/sequence type exists. Needs to match the
proposal's example syntax (`keyframe(yaw, pitch, zoom, duration, easing)`,
`stay(duration)`).

**File(s):** new, likely `spatial-camera/animation/`.

**Fix:** Design the DSL surface, then implement as an immutable builder
producing an ordered list of keyframes.

- [ ] **Planned**

### 3.2 Resolve a `CameraSequence` into ordered motion segments

**Fix:** Reuse `resolveCameraMotionPlan`'s shortest-angle-path and clamping
logic per segment, chaining start/end state across keyframes. Pure logic,
fully unit-testable — verify in particular that shortest-path yaw
resolution composes correctly across chained keyframes, not just a single
transition (this is untested territory today).

- [ ] **Planned**

### 3.3 Replace `FixedStepCameraAnimationScheduler`'s artificial timing with real VSYNC

**Problem:** The current scheduler advances by a fixed 16ms step
regardless of actual frame timing. `CoreRenderImplementations.kt` already
has a working `ChoreographerFrameScheduler` for the render loop that is a
candidate to adapt/reuse here.

**File(s):**
- `spatial-camera/src/main/java/com/elitec/spatial_camera/animation/FixedStepCameraAnimationScheduler.kt`
- `spatial-renderer/src/main/java/com/elitec/spatial_renderer/adapter/CoreRenderImplementations.kt`

- [ ] **Planned — real timing behavior only confirmable on-device**

### 3.4 `CameraSequencePlayer`

**Fix:** Drives 3.2's resolved segments through the 3.3 scheduler, updating
`CameraState` every frame.

- [ ] **Planned**

### 3.5 User-interruptible playback

**Problem:** `CORE2_PROPOSAL.md`'s technical acceptance explicitly requires
the sequencer to be interruptible by user gestures. Needs integration with
existing `GestureMotionPolicy`/`CameraDelta`.

**File(s):**
- `spatial-camera/src/main/java/com/elitec/spatial_camera/camera/CameraDelta.kt`
- `spatial-camera/src/main/java/com/elitec/spatial_camera/gesture/GestureMotionPolicy.kt`

**Fix:** Design first — decide whether a gesture cancels the sequence
outright or hands control back at the current interpolated state.

- [ ] **Planned**

### 3.6 On-device visual confirmation of smooth playback

- [ ] **Planned — requires device/emulator, not verifiable in this sandbox**

---

## Suggested execution order

Ordered to front-load everything verifiable as pure JVM/Kotlin logic in a
sandbox with no Android device and no Google Maven network access, before
work that requires a build with the Android Gradle Plugin or a device:

1. **1.0** — `Element.Model(...)`. Highest leverage: unblocks the entire
   already-built GLB pipeline publicly.
2. **2.0, 2.1, 2.2** — bounding boxes. Pure JVM, fully testable here.
3. **3.0, 3.2** — easing curves and sequence resolution math. Pure JVM.
4. **1.2, 1.3, 1.6** — parser robustness (normals, UVs, error mesh).
5. **2.3, 2.4–2.7, 2.8–2.10** — layout contract and containers.
6. **1.4, 1.5, 1.8, 1.9** — scope decisions and hardening (multi-mesh,
   material overrides, registry eviction, JIT-upload race audit).
7. **3.1, 3.3 (design), 3.4, 3.5 (design)** — sequencer DSL and player;
   VSYNC timing and gesture-interrupt behavior flagged as design-now,
   verify-on-device-later.
8. **1.1, 1.7, 2.11, 3.3 (verification), 3.6** — everything that can only
   be confirmed on a real device/emulator, tracked but not checked off
   until that verification happens.
