# Core #1 Stability Plan

> **Status:** Point 1 closed; Task 2.1 code ready | **Last Updated:** 2026-07-25  
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

### Task 2.1 — Orbit gesture (smooth, no fight with autoRotate)

| Sub-step | Status | Notes |
|----------|--------|-------|
| 2.1.1 Pause autoRotate during pointer gesture + cooldown | **Done (code)** | `CameraState.begin/endGestureInteraction` |
| 2.1.2 autoRotate ticks use Animation source | **Done (code)** | Does not outrank Gesture in runtime |
| 2.1.3 Natural pitch (invert screen dy) | **Done (code)** | `resolveOrbitGestureDelta` |
| 2.1.4 Unit tests | **Done (code)** | `OrbitGestureTask21Test` |
| 2.1.5 **Manual on device** | **Pending owner** | Drag orbit on Shapes with autoRotate on; camera follows finger without fighting |

**Manual check:**
1. Open Shapes (autoRotate active).
2. Drag horizontally on a Scene preview → yaw follows finger; autoRotate pauses while dragging.
3. After release, autoRotate resumes after ~0.35s.
4. Drag vertically → pitch feels natural (finger up → view from higher).

### Task 2.2+ — Pinch / bg-fg / recomposition

Pending after 2.1 device sign-off.

---

## Regression checklist (device only)

- [X] App launches without blank/black first frame
- [X] Cube visible on first frame
- [ ] Orbit gesture works smoothly (task 2.1)
- [ ] Pinch zoom works without crash
- [X] Rotating the device does not crash
- [ ] Backgrounding and foregrounding does not crash
- [ ] Recomposition with `cameraState` does not black-screen

---

## Core #1 device closure summary

- **Point 1:** CLOSED on device
- **Task 2.1:** code ready; awaiting manual orbit verification
- **Ready for Core #2:** NO

---

## Changelog

| Date | Agent / owner | Change |
|------|---------------|--------|
| 2026-07-25 | Owner | Point 1 closed (1.1 suite + 1.2 manual) |
| 2026-07-25 | Grok | **Task 2.1:** autoRotate yields to orbit; pitch invert; unit tests; tracker |
