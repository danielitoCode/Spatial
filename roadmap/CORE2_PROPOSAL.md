# Core #2: Spatial Framework Expansion

> **Status:** Draft / Planning  
> **Target:** Asset Pipeline, 3D Layouts, and Cinematic Motion.
> **Owner:** Development Team

El Core #2 tiene como objetivo elevar la abstracción del motor Spatial, permitiendo a los desarrolladores trabajar con activos externos complejos, organizar escenas mediante reglas de maquetación (Layouts) similares a Compose UI, y crear narrativas visuales mediante secuencias de cámara programadas.

---

## Pilar 1: Asset & Material Pipeline

Transición de primitivas procedurales a modelos complejos de autoría externa.

### 1.1 Cargador de Modelos (GLB/GLTF)
- **Formato Estándar:** Adopción de **glTF 2.0 (.glb)** como formato nativo por su eficiencia y soporte de materiales PBR.
- **API Pública:** 
  - `ModelResource.fromRawResource(R.raw.my_model)` para identificación de activos.
  - `Element.Model(resource, modifier)` para inserción en el grafo.
- **Material Overrides:** Capacidad de sobreescribir el material del modelo cargado mediante la API de `Modifier3D` o parámetros específicos, permitiendo cambiar colores o propiedades de superficie sin modificar el activo original.

### 1.2 Registro Dinámico de Mallas
- Evolución del `PrimitiveMeshRegistry` a un sistema capaz de registrar y subir a GPU mallas de forma asíncrona durante el tiempo de ejecución.

---

## Pilar 2: 3D Layout System (The "Spatial Box Model")

Introducción de contenedores inteligentes que gestionan la posición relativa de sus hijos basándose en sus volúmenes (Bounding Boxes).

### 2.1 Contenedores de Maquetación
- **`Box3D`**: Superposición de elementos en el mismo espacio (eje central).
- **`Row3D`**: Alineación secuencial a lo largo del eje **X**. Si se añaden dos cubos de 1m, el segundo se posiciona automáticamente desplazado 1m en X.
- **`Column3D`**: Alineación secuencial a lo largo del eje **Y** (apilamiento vertical).
- **`Stack3D`**: Alineación secuencial a lo largo del eje **Z** (profundidad).

### 2.2 Algoritmo de Layout
- Cálculo de "Intrincic Size" para modelos 3D.
- Soporte de `Spacing` entre elementos y `Alignment` (Start, Center, End) en los ejes transversales.

---

## Pilar 3: Cinematic Motion (Camera Sequencer)

Sistema declarativo para definir escenas animadas y movimientos de cámara complejos.

### 3.1 DSL de Animación de Cámara
Definición de una secuencia de estados de cámara con tiempos de transición y permanencia.

```kotlin
CameraSequence {
    // Punto A: Inicio
    keyframe(yaw = 0f, pitch = 20f, zoom = 1f, duration = 0)
    
    // Transición al Punto B en 2 segundos con curva suave
    keyframe(yaw = 90f, pitch = 45f, zoom = 2f, duration = 2000, easing = FastInFastOut)
    
    // Permanencia de 1 segundo en el Punto B
    stay(1000)
    
    // Regreso al inicio en 3 segundos
    keyframe(yaw = 0f, pitch = 20f, zoom = 1f, duration = 3000, easing = Linear)
}
```

### 3.2 Motor de Animación
- Integración con el sistema de VSYNC para interpolación fluida.
- Soporte de curvas de easing: `Linear`, `FastInFastOut`, `CubicBezier`.
- Interpolación de cuaterniones (o ángulos de Euler suavizados) para evitar saltos visuales.

---

## Hoja de Ruta de Implementación (Hitos)

1.  **Hito 2.1: GLB Loader.** Implementación del parser mínimo para archivos binarios GLB y carga de buffers.
2.  **Hito 2.2: Bounding Box Engine.** Lógica para calcular dimensiones reales de mallas arbitrarias.
3.  **Hito 2.3: Layouts (Box, Row, Column).** Implementación de los contenedores y sus modificadores de alineación.
4.  **Hito 2.4: Motion Sequence DSL.** Creación del motor de animación de cámara y el lenguaje declarativo.

---

## Aceptación Técnica

- La carga de un modelo GLB de 50k polígonos no debe bloquear el hilo de UI.
- Los Layouts deben responder correctamente a cambios de escala en tiempo real.
- El secuenciador de cámara debe ser interrumpible por gestos de usuario si se desea (User-Interruptible Mode).
