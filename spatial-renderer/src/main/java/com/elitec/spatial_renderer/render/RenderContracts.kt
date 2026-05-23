package com.elitec.spatial_renderer.render

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
    val resources: List<GpuResourceHandle> = emptyList(),
)