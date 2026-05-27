package com.elitec.spatial_core.input

/**
 * Delta de gesto de órbita.
 * Producido por el sistema de gestos, consumido por la cámara.
 */
data class OrbitDelta(
    val deltaYaw: Float,
    val deltaPitch: Float,
)

/**
 * Delta de gesto de zoom (pinch).
 * Producido por el sistema de gestos, consumido por la cámara.
 */
data class ZoomDelta(
    val scaleFactor: Float,
)

/**
 * Contrato para recibir eventos de input.
 * Implementado por cualquier módulo que consuma gestos (ej: cámara).
 * El dispatcher de gestos no necesita saber qué hace el consumidor.
 */
interface InputEventSink {
    fun onOrbit(delta: OrbitDelta)
    fun onZoom(delta: ZoomDelta)
}
