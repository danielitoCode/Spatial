# Core #1 Stability Plan

> **Status:** Point 1 + 2.1 CLOSED; 2.2 pending device; **Task 2.3 in progress** | **Last Updated:** 2026-07-27  
> **Canonical tracker:** this file (`roadmap/CORE1_STABILITY.md`)  
> **Owner:** Project owner (on-device evidence) + agents (code/docs)

**Rule:** only **on-device evidence** marks device items **Done**.

---

## Device closure plan (active)

### Task 1.1 — First-frame cube (automated) — **CLOSED**

| Sub-step | Status |
|----------|--------|
| 1.1.1–1.1.3 code | **Done** |
| 1.1.4 connectedAndroidTest | **Done (on-device)** — 3/3 PASS Pixel_9_Pro_XL AVD API 15 |
| 1.1.5 ×3 cold (optional) | Optional |

### Task 1.2 — Manual cold start — **CLOSED**

| Sub-step | Status |
|----------|--------|
| 1.2.1–1.2.3 code | **Done** |
| 1.2.4–1.2.6 M1–M3 | **Done (on-device)** — force-stop + rotation OK |

### Task 2.1 — Orbit gesture — **CLOSED**

| Sub-step | Status | Notes |
|----------|--------|-------|
| 2.1.1–2.1.4 code/tests | **Done** | autoRotate yield + turntable signs |
| 2.1.5 Manual on device | **Done (on-device)** | Horizontal + vertical follow finger |

### Task 2.2 — Pinch zoom

| Sub-step | Status | Notes |
|----------|--------|-------|
| 2.2.1–2.2.3 code/tests | **Done (code)** | `PinchZoomTask22Test` |
| 2.2.4 **Manual on device** | **Pending owner** | Pinch in/out on Shapes 3× |

### Task 2.3 — Background → foreground + recomposition with camera

| Sub-step | Status | Notes |
|----------|--------|-------|
| 2.3.1 Lifecycle ON_PAUSE / ON_RESUME → host | **Done (code)** | `Scene.kt` `LifecycleEventObserver` |
| 2.3.2 GLSurfaceView onPause/onResume + surface-ready gate | **Done (code)** | `SpatialGlSurfaceView` |
| 2.3.3 Host queues last scene on resume for surface-ready replay | **Done (code)** | `SpatialRuntimeSceneRenderHost.onResume` |
| 2.3.4 Camera snapshot observes Compose state (recomposition) | **Done (code)** | `CameraState.snapshot()` reads version/yaw/pitch/zoom |
| 2.3.5 **Manual bg→fg on device** | **Pending owner** | See checklist below |
| 2.3.6 **Manual recomposition + camera on device** | **Pending owner** | Orbit then leave composition / rotate |

**Manual check (2.3.5 — background → foreground):**
1. Open Shapes; confirm figures visible + autoRotate.
2. Press Home (app to background) for ~5s.
3. Return via recents → figures visible again (no permanent black/empty GL).
4. Repeat 3× (also once with screen off/on if possible).

**Manual check (2.3.6 — recomposition + camera):**
1. Orbit the shape (change yaw/pitch).
2. Rotate device or navigate away and back to Shapes if applicable.
3. Camera orientation should persist or recover without black screen; scene still draws.

---

## Regression checklist (device only)

- [X] App launches without blank/black first frame
- [X] Cube visible on first frame
- [X] Orbit gesture works smoothly (task 2.1)
- [ ] Pinch zoom works without crash
- [X] Rotating the device does not crash
- [ ] Backgrounding and foregrounding does not crash
- [ ] Recomposition with `cameraState` does not black-screen

---

## Core #1 device closure summary

- **Point 1:** CLOSED on device
- **Task 2.1:** CLOSED on device
- **Task 2.2:** code ready; device pinch still pending
- **Task 2.3:** code hardened; awaiting bg→fg + recomposition device checks
- **Ready for Core #2:** NO

---

## Changelog

| Date | Agent / owner | Change |
|------|---------------|--------|
| 2026-07-25 | Owner | Point 1 closed |
| 2026-07-25 | Grok | Task 2.1 code |
| 2026-07-27 | Owner + Grok | 2.1.5 CLOSED; 2.2 pinch tests |
| 2026-07-27 | Grok | **Task 2.3:** resume frame pre-queue + tracker/checklists |
