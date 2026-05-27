package com.elitec.spatial_core.render

import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.scene.RenderableNode

/**
 * Contrato agnóstico de plataforma para inicialización y ciclo de render.
 * No acopla APIs de Android ni OpenGL.
 */
interface SpatialRenderLoopContract {
    fun onInitialize()

    /**
     * Entrega un [FrameSnapshot] con tipos de valor inmutables.
     *
     * Política de ownership:
     * - El productor crea el snapshot por frame.
     * - El consumidor puede leerlo durante la ejecución de este callback.
     * - Las instancias anidadas son inmutables y nunca comparten buffers mutables.
     */
    fun onFrame(snapshot: FrameSnapshot)


    fun onShutdown()
}