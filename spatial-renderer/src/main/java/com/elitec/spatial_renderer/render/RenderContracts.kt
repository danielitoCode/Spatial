package com.elitec.spatial_renderer.render

import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.scene.RenderableNode

/** Contrato mínimo para disparar render de un frame. */
interface RenderBackend {
    fun render(frame: RenderFrame)
}

/** Contrato mínimo para agendar el siguiente frame. */
interface FrameScheduler {
    fun requestFrame(onFrame: (RenderFrame) -> Unit)
}

/** Handle opaco de recursos GPU para evitar filtrar implementación concreta. */
interface GpuResourceHandle {
    val id: String
    fun release()
}

data class RenderFrame(
    val frameTimeNanos: Long,
    val nodes: List<RenderableNode> = emptyList(),
    val resources: List<GpuResourceHandle> = emptyList(),
    val cameraState: CameraSnapshot = CameraSnapshot(),
)