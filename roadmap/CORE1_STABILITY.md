# Core #1 Stability Plan

> **Status:** Finalized / Stable | **Last Updated:** 2026-07-09  
> **Owner:** Agent Session | **Purpose:** Track and resolve all blockers for a stable Core #1 release.

Este documento sirve como referencia canónica para el esfuerzo de estabilización del Core #1. Se han resuelto todos los cuellos de botella críticos en el pipeline de renderizado, la programación de frames y la gestión del ciclo de vida.

---

## Filosofía de Estabilidad

- Lo simple primero, lo complejo después.
- No marcar una casilla como completa hasta que la corrección esté confirmada, probada y verificada.
- Si una corrección introduce una regresión, se revierte y se actualiza la nota.

---

## Fase 1: Bloqueadores Críticos (Completado)

### 1.0 Vincular `FrameSnapshot.clearColor` en `SpatialGlRenderer`
- [X] **Hecho**
- **Solución:** Se propagó el color de fondo desde Compose hasta OpenGL. Se añadió `GLES30.glClear` para asegurar que el buffer se limpie realmente con el color solicitado en cada frame.

### 1.1 Reemplazar `ChoreographerFrameScheduler` síncrono
- [X] **Hecho**
- **Solución:** Implementación asíncrona real alineada con VSYNC usando `android.view.Choreographer`. Se añadió `ImmediateFrameScheduler` para tests unitarios.

### 1.2 Corregir condición de carrera del primer frame
- [X] **Hecho**
- **Solución:** El Host de renderizado ahora encola las peticiones de frame hasta que la superficie GL está lista (`glReady`), evitando el "flash negro" inicial.

### 1.3 Sanitizar el ciclo de vida de `releaseGlResources`
- [X] **Hecho**
- **Solución:** Se añadieron guardas `isAttachedToWindow` y bloques `try/catch` de defensa en profundidad para evitar crashes durante rotaciones rápidas o salida de la app.

---

## Fase 2: Robustez de Contrato (Completado)

### 2.0 Completar datos de `FrameSnapshot`
- [X] **Hecho**
- **Solución:** Se implementó `Mat4Math` (Kotlin puro) para calcular matrices de vista y proyección reales en el Snapshot, permitiendo que la API pública exponga datos veraces.

### 2.1 Cachear matriz de proyección
- [X] **Hecho**
- **Solución:** La matriz de proyección solo se recalcula cuando cambia el tamaño del Viewport, optimizando el uso de CPU.

### 2.2 Coalescencia de peticiones de frame (Backpressure)
- [X] **Hecho**
- **Solución:** El scheduler ahora garantiza que solo se procese la *última* petición de frame recibida antes del pulso VSYNC, evitando saturación por gestos rápidos.

---

## Fase 3: Pruebas de Estabilidad Finales (Completado)

### 3.1 Test de estrés de ciclo de vida
- [X] **Verificado**
- **Resultado:** El test instrumentado `GlLifecycleStressTest` confirmó estabilidad tras 50 ciclos rápidos de recreación de contexto.

### 3.2 Test de estabilidad de cámara en límites
- [X] **Verificado**
- **Resultado:** `CameraStabilityTest` confirmó que el motor no produce NaNs ni crashes al mirar directamente a los polos o usar zooms extremos.

---

## Registro de Cambios Final (Core #1)

| Fecha | Cambio |
|-------|--------|
| 2026-07-03 | Creación del plan de estabilidad. |
| 2026-07-05 | Implementación de VSYNC y corrección de concurrencia. |
| 2026-07-08 | Solución de bugs de renderizado (glClear) y persistencia de nodos en recomposición. |
| 2026-07-09 | Soporte de colores Compose/Material, transparencia y cierre oficial de Core #1. |

---

**Cierre de Core #1:** El motor se declara **ESTABLE** y listo para la expansión hacia el Core #2.
