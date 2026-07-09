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
 * Audit notes (Core #1 Stability, items 1.1 + 2.2):
 * - **1.1** replaced a synchronous placeholder with this real, VSYNC-aligned implementation.
 * - **2.2** documents a data-coalescing bug found in an intermediate version of this class: it
 *   coalesced *scheduling* (only one `postFrameCallback` in flight at a time) but not *data* - if
 *   [requestFrame] was called more than once before the pending VSYNC fired, every call after the
 *   first was silently dropped, including the closure carrying the freshest `nodes`/`cameraSnapshot`.
 *   The frame that actually rendered was the *first*, stalest, call - not the latest one - which
 *   reads as dropped gesture updates and one-frame-behind stutter under load. [latestOnFrame] is
 *   updated on every call (even while a callback is already pending) so the VSYNC tick always
 *   replays the most recent state.
 *
 * [cancel] closes a lifecycle gap flagged during review: without it, a `postFrameCallback` already
 * in flight when the host is disposed would still fire `doFrame` against a torn-down instance.
 */
class ChoreographerFrameScheduler : FrameScheduler {
    private val choreographer: Choreographer = Choreographer.getInstance()
    private val lock = Any()
    private var pending = false
    private var latestOnFrame: ((RenderFrame) -> Unit)? = null
    private var scheduledCallback: Choreographer.FrameCallback? = null

    override fun requestFrame(onFrame: (RenderFrame) -> Unit) {
        // Built before the lock so the whole "decide + record" step below is a single atomic
        // synchronized block; a previous version split this into two separate `synchronized`
        // blocks (one to flip `pending`, a second to record `scheduledCallback`), leaving a
        // theoretical window where a concurrent `cancel()` between them would find
        // `scheduledCallback == null` and be unable to call `removeFrameCallback`, even though
        // `postFrameCallback` would still go on to run afterwards. Not exploitable today (this
        // scheduler is only ever driven from a single Looper thread), but cheap to close outright.
        val callback = Choreographer.FrameCallback { frameTimeNanos ->
            val onFrameToInvoke = synchronized(lock) {
                pending = false
                scheduledCallback = null
                latestOnFrame.also { latestOnFrame = null }
            }
            onFrameToInvoke?.invoke(RenderFrame(frameTimeNanos = frameTimeNanos))
        }

        val shouldSchedule = synchronized(lock) {
            latestOnFrame = onFrame
            if (pending) {
                false
            } else {
                pending = true
                scheduledCallback = callback
                true
            }
        }
        if (shouldSchedule) {
            choreographer.postFrameCallback(callback)
        }
    }

    /**
     * Cancels a pending, not-yet-fired [requestFrame] callback, if any. Intended to be called from a
     * host's `dispose()`/`onDetachedFromWindow` so the scheduler never invokes `onFrame` after the
     * owner has already torn down its GL resources.
     */
    override fun cancel() {
        val callback = synchronized(lock) {
            pending = false
            latestOnFrame = null
            scheduledCallback.also { scheduledCallback = null }
        }
        callback?.let { choreographer.removeFrameCallback(it) }
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