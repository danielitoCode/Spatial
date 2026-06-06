package com.elitec.spatial_renderer.adapter

import android.view.Choreographer
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

/** Synchronous (for tests / non-UI contexts). */
class ImmediateFrameScheduler : FrameScheduler {
    override fun requestFrame(onFrame: (RenderFrame) -> Unit) {
        onFrame(RenderFrame(frameTimeNanos = System.nanoTime()))
    }
}

/** VSYNC-aligned asynchronous scheduler using Android Choreographer. Thread-safe coalescing. */
class ChoreographerFrameScheduler : FrameScheduler {
    private val choreographer: Choreographer = Choreographer.getInstance()
    @Volatile private var pending = false

    override fun requestFrame(onFrame: (RenderFrame) -> Unit) {
        if (pending) return  // coalesce
        pending = true
        choreographer.postFrameCallback(object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                pending = false
                onFrame(RenderFrame(frameTimeNanos = frameTimeNanos))
            }
        })
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