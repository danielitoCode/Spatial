package com.elitec.spatial_renderer.gl

import android.content.Context
import android.content.ContextWrapper
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

import android.app.Activity
import android.graphics.PixelFormat

/**
 * Unwraps Compose/Activity `ContextWrapper` layers (e.g. `ContextThemeWrapper`) to find the
 * underlying [Activity], if any. Returns null for non-Activity hosts (e.g. a Service context, or
 * a preview/test harness), in which case the window-translucency fix below is simply skipped.
 */
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

class SpatialGlSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    private val spatialRenderer = SpatialGlRenderer()

    // Guards ensureWindowTranslucentIfNeeded() so we only touch the hosting Window once, instead
    // of on every frame that requests a transparent clear color.
    private var windowTranslucencyEnsured = false

    init {
        setEGLContextClientVersion(3)
        // Configure EGL for translucent config (RGBA_8888) and 16-bit depth buffer
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        
        // Request translucency format from the surface holder
        holder.setFormat(PixelFormat.TRANSLUCENT)
        
        // Use Z-order media overlay so the GL surface respects its position in the View hierarchy.
        // Previously we used `setZOrderOnTop(true)`, which forced the GL surface above ALL
        // Compose content (including TopBars, Scaffolds, dialogs), breaking UI occlusion.
        // `setZOrderMediaOverlay(true)` places the surface above the window's main surface but
        // below its view hierarchy, allowing standard views (like a TopBar) to draw over it.
        //
        // Trade-off this introduced (fixed below, see ensureWindowTranslucentIfNeeded): a
        // GLSurfaceView's holder format only controls its OWN compositor layer. With
        // setZOrderOnTop(true) that layer sat above literally everything, including the window's
        // own opaque backing, so a transparent clear color showed through to whatever was behind
        // the app. With setZOrderMediaOverlay(true), the surface's layer sits *below* the
        // window's own main surface - and that window is opaque by default in Android, so a
        // transparent clear color now shows the window's opaque (black) backing instead of
        // seeing through. Fixing this requires the hosting Activity's Window itself to be marked
        // translucent, not just this SurfaceView's own holder format.
        setZOrderMediaOverlay(true)
        
        spatialRenderer.onSurfaceReadyCallback = { post { requestRender() } }
        setRenderer(spatialRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    /**
     * Marks the hosting Activity's [android.view.Window] as translucent so this view's
     * transparent pixels actually show through, instead of the window's opaque default backing.
     *
     * Only called when a frame actually requests a non-opaque clear color (`alpha < 1f`), so
     * scenes with an opaque background never pay the cost of a translucent window (which disables
     * some SurfaceFlinger compositing optimizations). Idempotent per view instance.
     *
     * No-op if [Context.findActivity] can't resolve an [Activity] (e.g. non-Activity host).
     */
    private fun ensureWindowTranslucentIfNeeded(clearColor: Color4) {
        if (windowTranslucencyEnsured || clearColor.a >= 1f) return
        context.findActivity()?.window?.setFormat(PixelFormat.TRANSLUCENT)
        windowTranslucencyEnsured = true
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
        ensureWindowTranslucentIfNeeded(frame.clearColor)
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
        if (!isAttachedToWindow) return
        // Track 1 (Fix background-then-foreground bug, Core #1): 
        // We only guard the redundant entry from multiple lifecycle hooks (detach + destroy) 
        // during a SINGLE teardown event. If we're already tearing down, skip. 
        if (glResourcesReleased) return
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
        // When the screen turns off and back on, the EGL context is torn down and recreated.
        // We release GL resources here so the GLThread doesn't try to use stale buffers, but we
        // MUST reset the `glResourcesReleased` flag so that when `onSurfaceCreated` fires again
        // (after the surface is recreated), the renderer is allowed to rebuild buffers from scratch.
        // Without this reset, the flag stays `true` forever and `onSurfaceCreated` silently
        // skips buffer creation, leaving the scene empty until the app is fully restarted.
        releaseGlResources()
        glResourcesReleased = false
        super.surfaceDestroyed(holder)
    }

    override fun onDetachedFromWindow() {
        releaseGlResources()
        // Track 1 (Fix background-then-foreground bug, Core #1): the SurfaceHolder path
        // (`surfaceDestroyed`) already resets `glResourcesReleased` so a subsequent
        // `onSurfaceCreated` can re-enqueue cleanup. The detach path historically did NOT,
        // so once a detach fired (e.g. the AndroidView's host left composition during a
        // navigation pop, or the system tore down the view between background and foreground),
        // the flag stayed `true` forever and the next surface lifetime leaked the renderer's
        // GPU resources *and* short-circuited the new buffer creation path because
        // `releaseGlResources()` would early-return. Reset here too so both teardown entry
        // points converge on the same flushed state.
        glResourcesReleased = false
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

    // -- Background/foreground lifecycle hooks -------------------------------------------------
    //
    // Track 1 (Fix background-then-foreground bug, Core #1):
    //
    // `GLSurfaceView` exposes public `onPause()`/`onResume()` that the hosting Activity MUST call
    // from its own lifecycle so the GL thread is paused/resumed in lockstep with the app. Without
    // these calls:
    //   - The GL thread keeps spinning on a dead surface on some devices (battery drain + possible
    //     crash when the EGL context is torn down underneath it).
    //   - On return from background, the EGL context may or may not be preserved (device-dependent
    //     via `setPreserveEGLContextOnPause`, defaulting to `true` only on API 11+). When it is not
    //     preserved, `onSurfaceCreated` must fire again - but if `onResume()` was never called, the
    //     GL thread is not correctly restarted and the recreate sequence gets desynchronised,
    //     leaving the renderer with the freshly-recreated `programId == 0` / empty `meshBuffers`
    //     state described in the bug report ("figures 3D disappear after closing the screen").
    //
    // These overrides forward to `super` (which does the real GLThread work) AND ask the renderer
    // to forget any stale "ready" state so the next `onSurfaceChanged` re-fires
    // `onSurfaceReadyCallback`. That callback re-runs the host's queued replay path, which
    // re-pushes the cached `pendingNodes` to the renderer so the scene is repainted instead of
    // staying empty until the user touches a slider.
    //
    // Note: these are NOT the same as `Activity.onPause/onResume`. They must be called BY the
    // Activity's overrides (or via a `LifecycleObserver` on `ON_PAUSE`/`ON_RESUME`). Compose's
    // `AndroidView` does not wire this automatically. See `SpatialGlRenderTarget.onPause/onResume`
    // and the host's `DisposableEffect` that observes the Activity lifecycle.
    override fun onPause() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onPause: pausing GL thread")
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        // Re-arm the "ready" gate so `onSurfaceChanged` re-fires the host's replay callback once
        // the surface finishes reloading. Without this, the host stays in `glReady == true` from
        // the previous surface lifetime and `requestFrame()` would short-circuit straight into
        // `requestFrameInternal()` against a renderer that no longer has valid GL resources yet.
        spatialRenderer.resetSurfaceReadyGate()
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onResume: resuming GL thread and re-arming surface-ready gate")
        }
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

    /** Forwards to [SpatialGlSurfaceView.onPause] so the host can drive the GL thread from the
     *  Activity's `onPause` (or via a `LifecycleObserver` on `ON_PAUSE`). See the KDoc on
     *  [SpatialGlSurfaceView.onPause] for why this is required for correct background/foreground
     *  behavior. */
    fun onPause() {
        surfaceView.onPause()
    }

    /** Forwards to [SpatialGlSurfaceView.onResume]. See [onPause] and
     *  [SpatialGlSurfaceView.onResume]. */
    fun onResume() {
        surfaceView.onResume()
    }
}

private const val TAG = "SpatialGlSurfaceView"