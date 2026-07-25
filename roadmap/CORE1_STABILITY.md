# Core #1 Stability Plan

> **Status:** Device closure in progress | **Last Updated:** 2026-07-25  
> **Canonical tracker:** this file (`roadmap/CORE1_STABILITY.md`)  
> **Owner:** Project owner (on-device evidence) + agents (code/docs)  
> **Purpose:** Track blockers and **device-verified** closure for Core #1 before Core #2.

**Rule:** code/docs can say "implemented"; only **on-device evidence** can mark device items **Done**.

---

## Philosophy

- Simple first, complex later.
- Do not mark a checkbox complete until the fix is committed, tested, and **verified on device** when the item requires GPU/lifecycle.
- If a fix introduces a regression, revert it and update the notes.
- Append-only changelog at the bottom.

---

## Device closure plan (active)

### Task 1.1 — Cubo visible en el primer frame (automatizado)

| Sub-step | Status | Notes |
|----------|--------|-------|
| 1.1.1 Locate/create `CubeRendersOnFirstFrameTest` | **Done (code)** | `spatial-renderer/src/androidTest/.../CubeRendersOnFirstFrameTest.kt` |
| 1.1.2 Flow: surface ready → first `onDrawFrame` → `glReadPixels` | **Done (code)** | Capture only first frame after valid viewport |
| 1.1.3 Distinctive clear vs cube color | **Done (code)** | Magenta clear `Color4(1,0,1,1)` vs white cube |
| 1.1.4 Run `connectedDebugAndroidTest` on device/emulator | **Pending owner** | See command below |
| 1.1.5 Repeat ×3 cold starts | **Pending owner** | Same test, process kill between runs preferred |

**Command:**

```bash
./gradlew :spatial-renderer:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.elitec.spatial_renderer.gl.CubeRendersOnFirstFrameTest
```

**Evidence template (fill when 1.1.4–1.1.5 pass):**

```text
- [ ] **Done (on-device)** for first-frame cube
- Evidence: connectedDebugAndroidTest PASS ×3
- Device: <model / API / emulator>
- Date: YYYY-MM-DD
- Run by: <name>
```

### Task 1.2 — Manual cold start (app) + code hardening

**Goal:** Force-stop → open app → 3D content visible without a **persistent** black/empty frame; rotation does not crash.

| Sub-step | Status | Notes |
|----------|--------|-------|
| 1.2.1 Skip empty factory enqueue | **Done (code)** | `Scene` factory only calls `renderSceneFrame` when `renderableNodes` non-empty |
| 1.2.2 SideEffect re-push after graph fills | **Done (code)** | Ensures frame after `DisposableEffect` adds elements |
| 1.2.3 Surface-ready fallback to pending nodes | **Done (code)** | Host paints `pendingNodes` if queue was empty at ready |
| 1.2.4 **M1** Force-stop → open | **Pending owner** | Cube/shape visible; no stuck black surface |
| 1.2.5 **M2** Repeat ×5 | **Pending owner** | Same behavior each cold start |
| 1.2.6 **M3** Rotate landscape ↔ portrait | **Pending owner** | No crash; content returns |

**Manual path in app:** open playground → shapes section (cards with `Scene` + Cube/Sphere/Plane).

**Evidence template:**

```text
- [ ] **Done (on-device)** task 1.2
- Evidence: M1–M3 checklist OK
- Device: <model / API>
- Date: YYYY-MM-DD
- Run by: <name>
```

### Task 2.x — Orbit / pinch / bg-fg / recomposition

Pending after 1.x device evidence.

---

## Phase 1: Critical blockers (code)

### 1.0 Wire `FrameSnapshot.clearColor` into `SpatialGlRenderer`
- [X] **Done (code)**

### 1.1 Replace synchronous `ChoreographerFrameScheduler`
- [X] **Done (code)**

### 1.2 Fix first-frame race condition
- [X] **Done (code)** — queue until `glReady`
- **2026-07-25 follow-up (device closure 1.2):** empty-first-composition enqueue avoided; surface-ready falls back to `pendingNodes`.

### 1.3 Sanitize `releaseGlResources` lifecycle
- [X] **Done (code)**

---

## Phase 2: Contract robustness (code)

### 2.0 Complete `FrameSnapshot` data
- [X] **Done (code)**

### 2.1 Cache projection matrix
- [X] **Done (code)**

### 2.2 Frame request backpressure / coalescing
- [X] **Done (code)** — instrumented `FrameCoalescingTest` present; still prefer on-device confirmation under load.

### 2.3 Validate camera snapshot / zoom guards
- [X] **Done (code)** — JVM `CameraStabilityTest` / zoom guards.

---

## Phase 3: Completeness & device gates

### 3.0 Expand `MeshDrawMode`
- [X] **Done (code)**

### 3.1 Document matrix rotation convention
- [X] **Done (code)** — numerically corrected KDoc + regression test.

### 3.2 Integration test: `cube_is_visible_on_first_frame`
- [ ] **Done (on-device)** — **blocked on owner evidence**
- **Code ready (2026-07-25):** hardened instrumented test + `FirstFrameTestActivity`.
- **Path:** `spatial-renderer/src/androidTest/.../CubeRendersOnFirstFrameTest.kt`

### 3.3 Lifecycle stress (device)
- [ ] **Done (on-device)** — `GlLifecycleStressTest` exists; checkbox only after owner run.

---

## Regression checklist (device only)

- [ ] App launches without blank/black first frame
- [ ] Cube visible on first frame (task 1.1 + manual 1.2)
- [ ] Orbit gesture works smoothly
- [ ] Pinch zoom works without crash
- [ ] Rotating the device does not crash
- [ ] Backgrounding and foregrounding does not crash
- [ ] Recomposition with `cameraState` does not black-screen

---

## Core #1 device closure summary

- **Status:** OPEN — 1.1 test + 1.2 cold-start code ready; awaiting on-device PASS
- **Ready for Core #2:** NO until device checklist above is signed off

---

## Changelog

| Date | Agent / owner | Change |
|------|---------------|--------|
| 2026-07-03 | Initial | Stability plan created |
| 2026-07-05–09 | Agents | Phase 1–3 code fixes, audits, instrumented tests authored |
| 2026-07-09 | Docs | roadmap file briefly marked "Finalized" without full device evidence |
| 2026-07-25 | Grok | **Task 1.1:** hardened `CubeRendersOnFirstFrameTest`; canonical device tracker |
| 2026-07-25 | Grok | **Task 1.2:** cold-start hardening — no empty factory enqueue; `SideEffect` re-push; surface-ready fallback to `pendingNodes`; manual M1–M3 checklist in tracker |
