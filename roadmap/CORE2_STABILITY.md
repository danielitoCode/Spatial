# Core #2 Stability Plan

> **Status:** Phase 1 partially implemented in code | **Last Updated:** 2026-07-30  
> **Owner:** Project owner + agents  
> **Purpose:** Track Core #2 against code reality (not only the original proposal audit).

**Rule:** code checkboxes can be marked **Done (code)** with commit evidence; device items need on-device proof.

---

## Phase 1: Asset & Material Pipeline

| Item | Status | Evidence |
|------|--------|----------|
| **1.0** `Element.Model(...)` | **Done (code)** | `Element.kt` / `Scene3D.kt` |
| **1.1** Model transforms = Primitive | **Done (code)** | `SceneNodeTransformParityTest` |
| **1.2** NORMAL in parser | **Done (code)** | `GltfBinaryParser` + `MeshData.normals` |
| **1.3** TEXCOORD_0 | **Done (code)** | `GltfBinaryParser` + `MeshData.texCoords` |
| **1.4** Multi-mesh / multi-primitive | **Done (code)** | Parser loops meshes/primitives |
| **1.5** Material override on `Modifier3D` | **Done (code)** | `SpatialMaterial` / `material()` |
| **1.6** Error mesh + logging | **Done (code)** | `MeshData.ErrorMesh` + `ModelLoadState.Error` |
| **1.7** Real `.glb` on device | **Partial** | Asset `app/.../raw/sample_model.glb`; Playground tab wired (`PlaygroundScreen`); parse+registry instrumented test; **GPU pixels still manual in Playground** |
| **1.8** Registry eviction / lifecycle | **Done (code)** | LRU `maxEntries` + `unregister` / `size` |
| **1.9** JIT upload race audit | **Pending** | Review still open |

### Playground alignment (2026-07-30)

- Bottom nav tag **Playground** → `MainRoutesKey.Playground` → `PlaygroundScreen`.
- `showModel` defaults **true** → `Element.Model(ModelResource.fromRawResource(R.raw.sample_model))`.
- Pipeline: `rememberModel` → `GltfBinaryParser` → `GlobalMeshRegistry` → renderer JIT.

```text
./gradlew :app:connectedDebugAndroidTest
./gradlew :spatial-renderer:connectedDebugAndroidTest
```

### 1.7 prep — registered mesh device test

```text
./gradlew :spatial-renderer:connectedDebugAndroidTest
```

Validates `GlobalMeshRegistry` → renderer JIT. Full GLB **visual** proof: open app → Playground → toggle Modelo GLB.

### 1.8 policy

- Default soft cap: **64** entries (`GlobalMeshRegistry.DEFAULT_MAX_ENTRIES`).
- LRU eviction on register when over cap.
- `unregister(meshId)` for explicit release; `clear()` for tests/process reset.
- Scene dispose does **not** wipe the global registry (models may be shared).

---

## Phase 2: 3D Layout System

| Item | Status |
|------|--------|
| 2.0–2.11 BoundingBox / Row3D / … | **Not started** |

---

## Phase 3: Cinematic Motion

| Item | Status |
|------|--------|
| Foundation (tween / motion plan) | Exists pre-Core #2 |
| 3.0–3.6 Sequence player / VSYNC / interrupt | **Not started** |

---

## Changelog

| Date | Change |
|------|--------|
| 2026-07-18 | Baseline audit (all Planned) |
| 2026-07-27–28 | Owner: Model API, parser, materials, rememberModel, CI javadoc |
| 2026-07-29 | Grok: tracker sync; registry LRU; transform parity test; registered-mesh device test prep |
| 2026-07-30 | Wire Playground to `sample_model.glb`; instrumented parse/registry test |
