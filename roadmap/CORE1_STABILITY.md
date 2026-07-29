# Core #1 Stability Plan

> **Status:** Point 1 + 2.1 CLOSED; 2.2–2.3 **code ready, pending physical device** | **Last Updated:** 2026-07-29  
> **Canonical tracker:** this file (`roadmap/CORE1_STABILITY.md`)  
> **Owner:** Project owner (on-device evidence) + agents (code/docs)

**Rule:** only **on-device evidence** marks device items **Done**.

**Note (2026-07-29):** Pinch and some multitouch gestures are unreliable on emulator. Prefer a **physical device** for 2.2.4. bg→fg and recomposition (2.3.5–2.3.6) can still be validated on emulator.

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
| 2.2.4 **Manual on device** | **Pending physical device** | Pinch in/out on Shapes 3× |

### Task 2.3 — Background → foreground + recomposition with camera

| Sub-step | Status | Notes |
|----------|--------|-------|
| 2.3.1–2.3.4 code | **Done (code)** | lifecycle + resume replay + camera observe |
| 2.3.5 **Manual bg→fg** | **Pending owner** | Emulator OK |
| 2.3.6 **Manual recomposition + camera** | **Pending owner** | Emulator OK |

---

## Regression checklist (device only)

- [X] App launches without blank/black first frame
- [X] Cube visible on first frame
- [X] Orbit gesture works smoothly (task 2.1)
- [ ] Pinch zoom works without crash (**prefer physical device**)
- [X] Rotating the device does not crash
- [ ] Backgrounding and foregrounding does not crash
- [ ] Recomposition with `cameraState` does not black-screen

---

## Core #1 device closure summary

- **Point 1:** CLOSED on device
- **Task 2.1:** CLOSED on device
- **Task 2.2:** code ready; pinch pending **physical device**
- **Task 2.3:** code ready; awaiting owner manual checks
- **Ready for Core #2:** NO (formal); Core #2 Phase 1 code may proceed in parallel

---

## Changelog

| Date | Agent / owner | Change |
|------|---------------|--------|
| 2026-07-25 | Owner | Point 1 closed |
| 2026-07-25 | Grok | Task 2.1 code |
| 2026-07-27 | Owner + Grok | 2.1.5 CLOSED; 2.2 pinch tests; 2.3 code |
| 2026-07-29 | Grok | Physical-device note for pinch; offline code track continues |
