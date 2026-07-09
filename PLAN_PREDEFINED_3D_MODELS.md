# Plan de Acción: Carga de Modelos 3D Predefinidos en Spatial

> **Verificación de disponibilidad + plan de implementación**
> **Fecha:** 2026-07-09
> **Scope:** Verificar si Spatial puede cargar modelos 3D predefinidos y, ante la ausencia, definir un plan de acción para implementarlo integrándolo a la API pública Compose y cargándolo desde recursos de la app (estilo `painterResource`).

---

## 1. Verificación de disponibilidad

### 1.1 Resultado: **NO disponible** actualmente

**Conclusión:** Spatial **no tiene** la capacidad de cargar modelos 3D predefinidos desde recursos ni de ninguna otra fuente externa. Solo soporta 3 primitivas procedurales: `Cube`, `Sphere` y `Plane`.

### 1.2 Evidencia encontrada en la arquitectura

| Componente | Estado actual | Implicación |
|---|---|---|
| **API pública Compose** (`Scene3D.kt`) | Solo expone `Element.Cube`, `Element.Sphere`, `Element.Plane` | No existe `Element.Model(...)` ni equivalente |
| **`PrimitiveShape`** (`shapes/PrimitiveShape.kt`) | `enum` interno cerrado en `Cube/Sphere/Plane` | No es extensible con nuevos tipos de malla |
| **`SceneNode`** (`scene/SceneNode.kt`) | Estructura inmutable con `shape: PrimitiveShape` | No transporta una referencia a un modelo |
| **Conversión a `RenderableNode`** (`SceneNode.toRenderableNode.kt`) | `meshId = shape.name` | meshId es siempre el nombre de la primitiva |
| **`PrimitiveMeshRegistry`** (`gl/PrimitiveMesh.kt`) | Registro estático con `defaultMeshes()` hardcodeado | Las mallas se generan proceduralmente; no se cargan |
| **`SpatialGlRenderer.onSurfaceCreated`** | Solo construye buffers para `defaultMeshes()` | No acepta mallas dinámicas |
| **Módulo `spatial-geometry`** (`Geometry.kt`) | Clase vacía `class Geometry {}` | Placeholder sin implementación |
| **Módulo `spatial-scene`** (`SpatialScene.kt`) | Clase vacía `class SpatialScene {}` | Placeholder sin implementación |
| **Recursos de la app** (`app/src/main/res/`) | Solo `drawable/`, `mipmap/`, `values/`, `xml/` | No existe `assets/` ni carpeta `raw/` para modelos |
| **Dependencias** (`libs.versions.toml`, build.gradle.kts) | Ningún loader de modelos (glTF, OBJ, STL, GLB) | No hay librería de parsing de formatos 3D |

### 1.3 Referencia explícita en el README

El `readme.md` (líneas 68-94) confirma que la carga de modelos externos está **explícitamente excluida** de Core #1 y declarada como visión a largo plazo (línea 785):

> "Intentionally Excluded: External model loading"
> "Over Core #1: These items are explicitly outside the Core #1 scope and must not be pulled into Core #1 API, renderer, or test commitments: External model loading"
> "Long-Term Vision: glTF loading"

---

## 2. Análisis de la arquitectura actual (puntos de extensión)

La arquitectura está diseñada para ser **modular y extensible**. Los puntos naturales de extensión son:

### 2.1 Pipeline de render actual

```text
Scene3D.kt (API pública)
    └─ Element.X(modifier)              →  SceneElement(shape, modifier)
          └─ SceneNode(shape, modifier) ← SceneBuilder + LocalSceneContentScope
                └─ toRenderableNode()   → RenderableNode(meshId, modelMatrix, material)
                      └─ SceneRenderHost.renderSceneFrame(nodes, camera, clearColor)
                            └─ SpatialRuntime.requestFrame(...)
                                  └─ RenderBackend.render(RenderFrame)
                                        └─ SpatialGlRenderer.onDrawFrame
                                              └─ meshRegistry.resolveOrNull(meshId)
                                                    └─ meshBuffers[meshId]
```

### 2.2 Puntos de extensión identificados

1. **`MeshData`** (`spatial-renderer/gl/MeshData.kt`): estructura `data class` pura y genérica (`vertices: FloatArray`, `indices: IntArray`, `drawMode`). Ya es apta para cualquier malla, no solo primitivas. ✅ **No requiere cambios.**
2. **`PrimitiveMeshRegistry`** (`spatial-renderer/gl/PrimitiveMesh.kt`): su `meshes: Map<String, MeshData>` ya acepta cualquier `meshId`. Es extensible si se le pasa un mapa personalizado. ⚠️ **Requiere modificación** para aceptar mallas cargadas externamente.
3. **`RenderableNode.meshId: String`** (`spatial-core/scene/SceneContracts.kt`): es un String arbitrario, ya desacoplado de la noción de primitiva. ✅ **No requiere cambios.**
4. **API `Element`** (`Scene3D.kt` + `core/Element.kt`): cerrada a 3 métodos. ⚠️ **Requiere extensión** con `Element.Model(...)`.
5. **Módulo `spatial-geometry`**: vacío pero **dedicado a "Mesh and primitive generation"** según el README (líneas 427-443). ✅ **Lugar natural** para un loader.
6. **Módulo `spatial-scene`**: vacío pero dedicado a "Scene graph and hierarchical structure". ✅ **Lugar natural** para gestión de mallas registradas.
7. **`SceneRenderHostFactory`**: es `fun interface` y ya permite que el app suministre su propio host. ✅ **No requiere cambios.**

### 2.3 Contratos respetados

El plan **no rompe** estos contratos críticos:

- **Test de contrato de API pública** (`ScenePublicApiContractTest.kt`): lista los símbolos públicos aceptados. Una nueva API `Element.Model` o tipo `ModelResource` requiere añadirlos al set `expectedPublicSymbols` del test.
- **Dirección de dependencias** (`readme.md:558-575`): Renderer nunca conoce Compose; Scene nunca conoce Android; Math permanece framework-independent. El loader vivo (que necesita I/O) **no puede vivir en `spatial-math` ni `spatial-core`** (ambos son Kotlin Libraries puras).
- **Scope de Core #1** (`readme.md:84-94`): "External model loading" está explícitamente fuera. La implementación debe declararse como **post-Core #1** o como extensión opt-in, no como modificación retroactiva al Core #1.

---

## 3. Plan de Acción

### 3.1 Decisiones de diseño (pre-requisitos a definir)

Antes de implementar, resolver estas decisiones:

| Decisión | Opciones | Recomendación |
|---|---|---|
| Formato de modelo 3D a soportar | glTF 2.0 (`.gltf`/`.glb`), OBJ, STL, FBX | **glTF 2.0** — estándar de facto, binario (`.glb`) y JSON (`.gltf`), soporta meshes, materiales y nodos. Coincide con la visión del README. |
| Librería de parsing | glTF sceneform, own parser, java-gltf-model, kwik | **Own minimal parser** inicialmente (vínculo simple a `MeshData`), para mantener bajo el acoplamiento y respetar la filosofía "no over-engineering" |
| Ubicación de los archivos en la app | `res/raw/`, `assets/`, empaquetados en `.aar` | **`res/raw/`** — permite uso análogo a `painterResource(R.raw.x)` (ver sección 3.2). `assets/` como fallback para streaming. |
| Caché de modelos cargados | Sin cache, LRU en memoria, disk cache | **LRU en memoria** (un `Map<String, MeshData>` similar a `PrimitiveMeshRegistry`) |
| Threading de carga | Main thread, IO Dispatcher, background GL thread | **IO Dispatcher** (Coroutines) para parseo + `queueEvent` para subida de buffers GL |
| Soporte de materiales | Solo color (como Core #1), texturas, PBR | **Solo color** — consistente con Core #1 ("Flat-color materials for Core #1", `readme.md:419`). PBR pospuesto. |
| API a exponer | `Element.Model(R.raw.x)`, `Element.Model(modelRef)`, nuevo `ModelResource` | **`Element.Model(model: ModelResource, modifier)`** siguiendo el patrón de `painterResource` (sección 3.2) |

### 3.2 Integración con la API pública Compose (patrón `painterResource`)

Jetpack Compose expone recursos como `painterResource(@DrawableRes id: Int)`. El patrón equivalente para Spatial sería:

```kotlin
// En com.elitec.spatial_compose (nuevo archivo ModelResource.kt)

@Immutable
sealed interface ModelResource {
    val id: String  // clave única para cache y meshId

    companion object {
        @Stable
        @Immutable
        private class RawResourceModel(
            @androidx.annotation.RawRes val resId: Int,
        ) : ModelResource {
            override val id: String get() = "raw:$resId"
        }

        fun fromRawResource(@androidx.annotation.RawRes resId: Int): ModelResource =
            RawResourceModel(resId)
    }
}

@Composable
fun rememberModel(model: ModelResource): MeshData {
    // Implementa: cachea por model.id, carga vía LaunchedEffect + IO dispatcher,
    // usa LocalContext para abrir Resources.openRawResource(resId)
    // Devuelve MeshData procedurales como fallback mientras carga
}
```

Y la API pública de `Element`:

```kotlin
// En com.elitec.spatial_compose.Scene3D (extiende el object Element existente)
@Composable
public fun Element.Model(
    model: ModelResource,
    modifier: Modifier3D = Modifier3D.Default,
) {
    CoreElement.Model(model = model, modifier = modifier)
}
```

Uso desde la app (análogo a `painterResource`):

```kotlin
import com.elitec.spatial_compose.ModelResource
import com.elitec.spatial_compose.Element
import com.elitec.spatial.R

Scene(
    renderHostFactory = DefaultSceneRenderHostFactory,
    ...
) {
    Element.Model(
        model = ModelResource.fromRawResource(R.raw.my_model),
        modifier = Modifier3D.Default.size(2f.meters)
    )
}
```

> **Nota:** `painterResource` está en `androidx.compose.ui.res` y opera con `Resources` de Android. La implementación de `rememberModel` debe seguir el mismo patrón: resolver `@Composable` el `LocalContext`, leer `context.resources.openRawResource(resId)`, parsear en `Dispatchers.IO` con `LaunchedEffect(model.id)`, y exponer `MeshData` como `State<MeshData>`.

### 3.3 Fases de implementación

#### Fase 0 — Remover placeholders y sentar módulos
- [ ] Implementar `spatial-geometry/Geometry.kt`: tipo `MeshData` puro (reutilizar/extraer de `spatial-renderer/gl/MeshData.kt` moviéndolo a `spatial-geometry` como módulo更低 nivel, o mantener duplicado como contrato). Idealmente: **mover `MeshData` a `spatial-geometry`** y que `spatial-renderer` dependa de `spatial-geometry` (ya lo hace, según `spatial-scene/build.gradle.kts:20`).
- [ ] Implementar `spatial-scene/SpatialScene.kt`: `MeshRegistry` genérica (cualquier `MeshData` por `String` id), desacoplada del renderer.
- [ ] Definir contrato `MeshLoader` en `spatial-geometry`:

  ```kotlin
  interface MeshLoader {
      fun load(input: java.io.InputStream): MeshData
  }
  ```

- [ ] Tests unitarios JVM para `MeshRegistry` (puro Kotlin, no necesita Android).

#### Fase 1 — Parser mínimo glTF 2.0 (.glb)
- [ ] Crear `spatial-geometry/src/main/kotlin/com/elitec/spatial_geometry/GltfParser.kt`:
  - Parser binario de header de `.glb` (12 bytes + chunks JSON+BIN).
  - Parser del JSON de glTF (`accessors`, `bufferViews`, `meshes`, `primitives`).
  - Conversión a `MeshData` (vertices, indices; ignorar texturas/normales en esta fase).
- [ ] Tests unitarios con archivos `.glb` de prueba en `src/test/resources/` (módulo `spatial-geometry` es Java Library pura → puede testear en JVM).
- [ ] Limitar scope: solo meshes trianguladas, sin skinning, sin morph targets.

#### Fase 2 — Carga desde recursos Android (capa Compose)
- [ ] Crear `spatial-compose/.../ModelResource.kt` (contrato público, sección 3.2).
- [ ] Crear `spatial-compose/.../rememberModel.kt`:
  - Usa `LocalContext`, `remember(model.id)`, `LaunchedEffect`.
  - Crea `ioDispatcher = Dispatchers.IO`.
  - Llama `context.resources.openRawResource(resId)` + `GltfParser.load(stream)`.
  - Cachea en un `CompositionLocal` con `MeshRegistry` compartida (o un `staticCompositionLocalOf<MeshRegistry>`).
- [ ] Añadir `CoreElement.Model(model, modifier)` en `core/Element.kt`.
- [ ] Añadir `Element.Model(model, modifier)` en `Scene3D.kt` (API pública raíz).

#### Fase 3 — Rendereregistry extensible
- [ ] Modificar `PrimitiveMeshRegistry` → renombrar/extender a `MeshRegistry` con:
  - `defaultMeshes()` como base primitivas.
  - API `register(meshId, meshData)` para añadir mallas cargadas desde `spatial-geometry`.
  - `registerAll(map)` para cargas masivas.
- [ ] Modificar `SpatialGlRenderer.onSurfaceCreated` para que, además de `defaultMeshes()`, consulte el registro extensible y construya los GL buffers para todas las mallas disponibles.
- [ ] Añadir API `fun registerMesh(meshId, meshData)` invocable desde `SpatialRuntimeSceneRenderHost.requestFrame` cuando llegue un `meshId` no en buffers (p. ej. `onDrawFrame` detecta `meshId` desconocido y lo encola para el siguiente `onSurfaceCreated` o vía `glGenBuffers` inmediato en el thread GL).

#### Fase 4 — Integración de SceneNode con modelo
- [ ] Modificar `SceneNode` para aceptar **o** bien `shape: PrimitiveShape` o bien `modelRef: ModelResource` (usar sealed class o campo nullable con `require` mutuo exclusivo).
- [ ] Modificar `SceneNode.toRenderableNode()`: si tiene `modelRef`, el `meshId` sale del `modelRef.id` y el `material` del modelo (o color por defecto).
- [ ] Actualizar test de contrato `ScenePublicApiContractTest`: añadir `ModelResource` y `rememberModel` a `expectedPublicSymbols`; **no** eliminar símbolos existentes.

#### Fase 5 — Recursos de la app + demo
- [ ] Crear `app/src/main/res/raw/` con un modelo de prueba simple (p. ej. `cube_ascii.glb` minimal generado o un modelo CC0 pequeño).
- [ ] Añadir pantalla "Models" en ` ShapesScreen.kt` o nueva `ModelsScreen.kt` que use `Element.Model(ModelResource.fromRawResource(R.raw.demo))`.
- [ ] Actualizar `MainActivity.kt` con navegación a la nueva pantalla.

#### Fase 6 — Documentación y revisión de contratos
- [ ] Actualizar `readme.md`:
  - Mover "External model loading" de "Intentionally Excluded" a "Post-Core #1 Supported".
  - Añadir `Element.Model` y `ModelResource` a la sección "Core #1 public API" si se incluye como extensión, o crear nueva sección "Extensions".
- [ ] Actualizar `CORE1_STABILITY.md`: nueva fase documentando las decisiones tomadas.
- [ ] Documentar KDoc del patrón `rememberModel` análogo a `painterResource`.

#### Fase 7 — Verificación y tests
- [ ] Tests instrumentados: cargar `.glb` desde `res/raw`, renderizar primer frame y leer un píxel (similar a `CubeRendersOnFirstFrameTest.kt`).
- [ ] Tests unitarios del parser glTF con varios modelos canónicos (Box.glb, TriangleWithoutIndices.glb de KhronosGroup).
- [ ] Actualizar y correr `./gradlew test` y agregar Robolectric si no está.
- [ ] Correr Regression Checklist de `CORE1_STABILITY.md` (líneas 393-409) en dispositivo.

### 3.4 Cambios por módulo (resumen)

| Módulo | Cambios |
|---|---|
| `spatial-geometry` | Implementar `Geometry.kt` (o eliminar), mover `MeshData` aquí, crear `GltfParser`, contrato `MeshLoader` |
| `spatial-scene` | Implementar `SpatialScene.kt` como `MeshRegistry` genérica |
| `spatial-renderer` | `PrimitiveMeshRegistry` → extensible; `SpatialGlRenderer.onSurfaceCreated`/`onDrawFrame` maneja mallas dinámicas |
| `spatial-compose` | Nuevo `ModelResource.kt`, `rememberModel.kt`; `CoreElement.Model`; `Scene3D.kt` expone `Element.Model` |
| `spatial-core` | Posiblemente nada (los contratos `RenderableNode` ya son genéricos); si se mueve `MeshData` aquí, ajustar dependencias |
| `app` | Crear `res/raw/`, nueva pantalla, navegación |
| `spatial-compose-runtime-adapter` | `SpatialRuntimeSceneRenderHost` ya es agnóstico; opcionalmente expone constructor con `MeshRegistry` custom |

### 3.5 Archivos nuevos y modificados (predicción)

**Nuevos:**
- `spatial-geometry/src/main/java/com/elitec/spatial_geometry/GltfParser.kt`
- `spatial-geometry/src/main/java/com/elitec/spatial_geometry/MeshLoader.kt`
- `spatial-geometry/src/main/java/com/elitec/spatial_geometry/MeshData.kt` (migrado desde renderer)
- `spatial-scene/src/main/java/com/elitec/spatial_scene/MeshRegistry.kt`
- `spatial-compose/src/main/java/com/elitec/spatial_compose/ModelResource.kt`
- `spatial-compose/src/main/java/com/elitec/spatial_compose/rememberModel.kt`
- `spatial-compose/src/main/java/com/elitec/spatial_compose/core/Element.Model.kt`
- `app/src/main/res/raw/demo.glb`
- `app/src/main/java/.../presentation/screens/ModelsScreen.kt`

**Modificados:**
- `spatial-geometry/src/main/java/com/elitec/spatial_geometry/Geometry.kt` (vaciar o eliminar)
- `spatial-scene/src/main/java/com/elitec/spatial_scene/SpatialScene.kt`
- `spatial-renderer/src/main/java/com/elitec/spatial_renderer/gl/PrimitiveMesh.kt`
- `spatial-renderer/src/main/java/com/elitec/spatial_renderer/gl/SpatialGlRenderer.kt`
- `spatial-renderer/src/main/java/com/elitec/spatial_renderer/gl/MeshData.kt` (si se migra, eliminar o typealias)
- `spatial-compose/src/main/java/com/elitec/spatial_compose/core/Element.kt`
- `spatial-compose/src/main/java/com/elitec/spatial_compose/Scene3D.kt`
- `spatial-compose/src/main/java/com/elitec/spatial_compose/scene/SceneNode.kt`
- `spatial-compose/src/main/java/com/elitec/spatial_compose/scene/SceneNode.toRenderableNode.kt`
- `spatial-compose/src/test/java/com/elitec/spatial_compose/ScenePublicApiContractTest.kt`
- `spatial-renderer/build.gradle.kts` (añadir `project(":spatial-geometry")`)
- `spatial-compose/build.gradle.kts` (añadir `project(":spatial-geometry")`)
- `app/src/main/java/com/elitec/spatial/MainActivity.kt`
- `readme.md`
- `CORE1_STABILITY.md`

---

## 4. Riesgos y mitigaciones

| Riesgo | Impacto | Mitigación |
|---|---|---|
| Parser glTF incompleto | Modelos complejos no cargan | Empezar con subset GLB sin skinning/animación. Documentar limitaciones. |
| Carga de modelos bloquea el hilo de UI | Lag, stutter | `Dispatchers.IO` + `LaunchedEffect` + caché en memoria. |
| Mallas GL no cargadas a tiempo | Primer frame negro | Reusar el mecanismo `queuedFrame` existente (`SpatialRuntimeSceneRenderHost:84-100`). |
| `meshId` colisiones entre primitivas y modelos | Render incorrecto | Prefijar ids de modelo con `raw:` (ya en sección 3.2). |
| Leak de buffers GL | OOM | Integrar cleanup con `releaseGlResources()` existente. |
| Violación de dirección de dependencias | Acoplamiento | Loader vivo solo en `spatial-geometry`/`spatial-compose`. `spatial-math` permanece puro. |
| Contrato de API pública roto | Test `ScenePublicApiContractTest` falla | Añadir símbolos nuevos a `expectedPublicSymbols`; no eliminar existentes. |
| Tamaño de APK | +KB por modelo embebido | Usar `.glb` binario (compacto); considerar ASset Packs si se necesita streaming. |

---

## 5. Consideración sobre el scope de Core #1

El `readme.md` (**líneas 84-94**) declara explícitamente que "External model loading" está fuera del scope de Core #1. Implementar este plan **miente sobre el scope** si se marca como parte de Core #1.

**Recomendación:** Tratar esta implementación como **post-Core #1 o como extensión opcional (opt-in)**:
- Marcar el contrato con `@ExperimentalSpatialApi` (anotación a crear).
- Documentar como "Extension Module" en el README.
- No invalidar el checklist de Core #1 (`CORE1_STABILITY.md:393-409`) para no romper la promesa de que Core #1 es estable por sí mismo.

---

## 6. Conclusión

Spatial **no tiene** capacidad actual para cargar modelos 3D predefinidos, pero su arquitectura modular facilita la extensión. El `MeshData` ya es genérico, `RenderableNode.meshId` ya es un String arbitrario y el módulo `spatial-geometry` está vacío esperando esta funcionalidad. La integración con la API pública Compose puede seguir el patrón `painterResource` mediante una nueva API `ModelResource` + `rememberModel`. No rompe contratos siempre que se respete la dirección de dependencias y se actualice el test `ScenePublicApiContractTest` en paralelo.

El plan respeta la filosofía del proyecto (Simple first, complex later), es incremental (7 fases desde el placeholder vacío hasta el demo final) y es reversible si resulta inviable en algún punto.
