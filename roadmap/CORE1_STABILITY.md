# Core #1 Stability Plan

> **Status:** Point 1 + Task 2.1 CLOSED; Task 2.2 in progress | **Last Updated:** 2026-07-27  
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
| 2.1.1 Pause autoRotate during pointer gesture + cooldown | **Done** | `CameraState.begin/endGestureInteraction` |
| 2.1.2 autoRotate ticks use Animation source | **Done** | Does not outrank Gesture |
| 2.1.3 Turntable signs (yaw `-dx`, pitch `+dy`) | **Done** | Iterated with device feedback |
| 2.1.4 Unit tests | **Done** | `OrbitGestureTask21Test` |
| 2.1.5 **Manual on device** | **Done (on-device)** | Owner: horizontal follows finger; vertical fixed to `+dy` (finger down → figure follows) |

### Task 2.2 — Pinch zoom (no crash, coherent scale)

| Sub-step | Status | Notes |
|----------|--------|-------|
| 2.2.1 Pipeline already wired (`OrbitAndZoom` → `zoomBy`) | **Done (code)** | `Gestures.orbitAndZoom()` in Shapes/Main |
| 2.2.2 Unit tests scale + two-finger state | **Done (code)** | `PinchZoomTask22Test` |
| 2.2.3 Pinch pauses autoRotate (same gesture gate) | **Done (code)** | `beginGestureInteraction` on POINTER_DOWN |
| 2.2.4 **Manual on device** | **Pending owner** | Two-finger pinch in / out on Shapes; no crash; zoom changes |

**Manual check (2.2.4):**
1. Open Shapes (or Main playground) with `Gestures.orbitAndZoom()`.
2. Two fingers **pinch out** → scene zooms in (objects larger).
3. Two fingers **pinch in** → scene zooms out.
4. During pinch, autoRotate does not fight; after release, resumes after cooldown.
5. Repeat 3× without crash / black screen.

### Task 2.3+ — bg→fg / recomposition

Pending after 2.2 device sign-off.

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
- **Task 2.2:** code + unit tests ready; awaiting pinch manual verification
- **Ready for Core #2:** NO

---

## Changelog

| Date | Agent / owner | Change |
|------|---------------|--------|
| 2026-07-25 | Owner | Point 1 closed (1.1 suite + 1.2 manual) |
| 2026-07-25 | Grok | Task 2.1 code: autoRotate yield; turntable signs; tests |
| 2026-07-27 | Owner + Grok | **2.1.5 CLOSED** (device orbit signs OK); start **2.2** pinch tests + tracker |
