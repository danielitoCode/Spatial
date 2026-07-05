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

/**
 * VSYNC-aligned asynchronous scheduler using Android Choreographer.
 *
 * Audit note (Core #1 Stability, item 2.2): the original implementation coalesced *scheduling*
 * (only one `postFrameCallback` in flight at a time) but not *data*: if [requestFrame] was called
 * more than once before the pending VSYNC fired, every call after the first was silently dropped,
 * including the closure carrying the freshest `nodes`/`cameraSnapshot`. The frame that actually
 * rendered was the *first*, stalest, call - not the latest one - which reads as dropped gesture
 * updates and one-frame-behind stutter under load. [latestOnFrame] is now updated on every call
 * (even while a callback is already pending) so the VSYNC tick always replays the most recent state.
 */
class ChoreographerFrameScheduler : FrameScheduler {
    private val choreographer: Choreographer = Choreographer.getInstance()
    private val lock = Any()
    private var pending = false
    private var latestOnFrame: ((RenderFrame) -> Unit)? = null

    override fun requestFrame(onFrame: (RenderFrame) -> Unit) {
        val shouldSchedule = synchronized(lock) {
            latestOnFrame = onFrame
            if (pending) {
                false
            } else {
                pending = true
                true
            }
        }
        if (!shouldSchedule) return

        choreographer.postFrameCallback(object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                val callback = synchronized(lock) {
                    pending = false
                    latestOnFrame.also { latestOnFrame = null }
                }
                callback?.invoke(RenderFrame(frameTimeNanos = frameTimeNanos))
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