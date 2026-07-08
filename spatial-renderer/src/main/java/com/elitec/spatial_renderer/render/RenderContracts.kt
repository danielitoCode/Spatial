package com.elitec.spatial_renderer.render

import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.render.Color4
import com.elitec.spatial_core.scene.RenderableNode

/** Contrato mínimo para disparar render de un frame. */
interface RenderBackend {
    fun render(frame: RenderFrame)
}

/**
 * Contrato mínimo para agendar el siguiente frame.
 *
 * [cancel] permite a un host cancelar un callback pendiente durante su `dispose()`/lifecycle
 * teardown, para que un [ChoreographerFrameScheduler]-like scheduler no dispare `onFrame` sobre una
 * instancia ya descartada (limitación no bloqueante detectada en la auditoría del ítem 1.1: no había
 * forma de cancelar un `postFrameCallback` en vuelo). Tiene un default no-op para no romper
 * implementaciones existentes que no necesiten cancelación (p. ej. `ImmediateFrameScheduler`, que es
 * síncrono y no tiene nada en vuelo que cancelar).
 */
interface FrameScheduler {
    fun requestFrame(onFrame: (RenderFrame) -> Unit)
    fun cancel() {}
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
    val clearColor: Color4 = Color4.BLACK,
)