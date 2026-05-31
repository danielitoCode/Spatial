package com.elitec.spatial_renderer.adapter

import com.elitec.spatial_renderer.render.FrameScheduler
import com.elitec.spatial_renderer.render.GpuResourceHandle
import com.elitec.spatial_renderer.render.RenderBackend
import com.elitec.spatial_renderer.render.RenderFrame

/**
 * Explicit bridge from the Core render backend contract to a concrete frame sink.
 *
 * The backend is intentionally sink-backed instead of silently dropping frames: callers must provide
 * the real renderer, fake renderer, or recorder that owns frame consumption.
 */
class DefaultRenderBackend (
    private val frameSink: RenderFrameSink,
) : RenderBackend {
    override fun render(frame: RenderFrame) {
        frameSink.render(frame)
    }

    fun interface RenderFrameSink {
        fun render(frame: RenderFrame)
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