package com.elitec.spatial_renderer.gl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.render.Color4
import com.elitec.spatial_core.scene.RenderableNode
import com.elitec.spatial_renderer.BuildConfig
import com.elitec.spatial_renderer.render.RenderBackend
import com.elitec.spatial_renderer.render.RenderFrame

class SpatialGlSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    private val spatialRenderer = SpatialGlRenderer()

    init {
        setEGLContextClientVersion(3)
        spatialRenderer.onSurfaceReadyCallback = { post { requestRender() } }
        setRenderer(spatialRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun updateScene(nodes: List<RenderableNode>) {
        queueEvent {
            spatialRenderer.updateNodes(nodes)
            requestRender()
        }
    }

    fun updateCamera(cameraSnapshot: CameraSnapshot) {
        queueEvent {
            spatialRenderer.updateCamera(cameraSnapshot)
            requestRender()
        }
    }

    fun render(frame: RenderFrame) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "render(frame): enqueueing ${frame.nodes.size} nodes")
        }
        queueEvent {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "render(frame): GL queue received ${frame.nodes.size} nodes")
            }
            spatialRenderer.updateClearColor(frame.clearColor)
            spatialRenderer.updateNodes(frame.nodes)
            spatialRenderer.updateCamera(frame.cameraState)
            requestRender()
        }
    }

    private var glResourcesReleased = false

    /**
     * Best-effort GL resource cleanup, queued onto the GL thread.
     *
     * Audit notes (Core #1 Stability, item 1.3 follow-up, GLM-5.2 review):
     * - The `isAttachedToWindow` guard covers the common path (view already fully detached) but not
     *   the narrower edge case the original fix targeted: the view can still report itself attached
     *   while its underlying `GLThread`/EGL context has already been torn down by the system between
     *   this check and the queued lambda actually running. [queueEvent] and the GL calls inside it are
     *   now wrapped in `try/catch` so that case degrades to a logged warning instead of a crash.
     * - [glResourcesReleased] deduplicates the two call sites (`surfaceDestroyed` and
     *   `onDetachedFromWindow`, which can both fire in sequence) so `queueEvent` is only enqueued
     *   once per surface lifecycle instead of redundantly twice.
     * - If this is skipped entirely (view detached before this ever ran, e.g. `dispose()` called
     *   post-detach), the eventual EGL context teardown by the system will still reclaim the
     *   underlying GPU memory; this method is a best-effort *early* cleanup, not the only cleanup.
     */
    fun releaseGlResources() {
        if (glResourcesReleased) return
        if (!isAttachedToWindow) return
        glResourcesReleased = true

        try {
            queueEvent {
                try {
                    spatialRenderer.releaseGlResources()
                } catch (e: IllegalStateException) {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, "releaseGlResources: GL context already gone, skipping cleanup", e)
                    }
                }
            }
        } catch (e: IllegalStateException) {
            // queueEvent itself can throw if the GLThread has already exited.
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "releaseGlResources: GLThread already gone, could not enqueue cleanup", e)
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        releaseGlResources()
        super.surfaceDestroyed(holder)
    }

    override fun onDetachedFromWindow() {
        releaseGlResources()
        super.onDetachedFromWindow()
    }

    /**
     * Registers [callback] to run once the GL surface is ready, in addition to (not instead of) any
     * previously-registered callback - including the default `post { requestRender() }` set in
     * [init]. Chaining instead of overwriting closes a "dead code" gap flagged during the item 1.2
     * review: previously, a caller that never invoked this method would silently keep the [init]
     * default, but a caller that did invoke it would silently discard that default instead of adding
     * to it, which was surprising and asymmetric.
     */
    fun setOnSurfaceReady(callback: () -> Unit) {
        val previousCallback = spatialRenderer.onSurfaceReadyCallback
        spatialRenderer.onSurfaceReadyCallback = {
            previousCallback?.invoke()
            callback()
        }
    }

    /** Notifies [callback] every time the viewport aspect ratio changes (see `onSurfaceChanged`). */
    fun setOnViewportChanged(callback: (aspectRatio: Float) -> Unit) {
        spatialRenderer.onViewportChangedCallback = callback
    }
}

class SpatialGlRenderTarget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : RenderBackend {
    private val surfaceView = SpatialGlSurfaceView(context, attrs)

    val view: View get() = surfaceView

    override fun render(frame: RenderFrame) {
        surfaceView.render(frame)
    }

    fun releaseGlResources() {
        surfaceView.releaseGlResources()
    }

    fun setOnSurfaceReady(callback: () -> Unit) {
        surfaceView.setOnSurfaceReady(callback)
    }

    fun setOnViewportChanged(callback: (aspectRatio: Float) -> Unit) {
        surfaceView.setOnViewportChanged(callback)
    }
}

private const val TAG = "SpatialGlSurfaceView"