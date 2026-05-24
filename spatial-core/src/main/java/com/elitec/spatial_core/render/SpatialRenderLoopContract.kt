package com.elitec.spatial_core.render

/**
 * Contrato agnóstico de plataforma para inicialización y ciclo de render.
 * No acopla APIs de Android ni OpenGL.
 */
interface SpatialRenderLoopContract {
    fun onInitialize()

    fun onFrame(frameTimeNanos: Long)

    fun onShutdown()
}