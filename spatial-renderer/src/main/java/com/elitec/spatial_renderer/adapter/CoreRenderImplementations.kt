package com.elitec.spatial_renderer.adapter

import com.elitec.spatial_renderer.render.FrameScheduler
import com.elitec.spatial_renderer.render.GpuResourceHandle
import com.elitec.spatial_renderer.render.RenderBackend
import com.elitec.spatial_renderer.render.RenderFrame

class DefaultRenderBackend : RenderBackend {
    override fun render(frame: RenderFrame) {
        // Punto de integración real con pipeline gráfico.
        frame.resources.forEach { _ -> }
    }
}

class ChoreographerFrameScheduler : FrameScheduler {
    override fun requestFrame(onFrame: (RenderFrame) -> Unit) {
        onFrame(RenderFrame(frameTimeNanos = System.nanoTime()))
    }
}

class OpenGlResourceHandle(
    override val id: String,
) : GpuResourceHandle {

    private var released = false

    override fun release() {
        if (!released) {
            released = true
        }
    }
}