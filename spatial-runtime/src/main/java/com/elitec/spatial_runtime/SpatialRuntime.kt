package com.elitec.spatial_runtime

import com.elitec.spatial_camera.camera.CameraDelta
import com.elitec.spatial_camera.camera.CameraRuntimeContract
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.camera.CameraUpdateSource
import com.elitec.spatial_core.render.FrameSnapshot
import com.elitec.spatial_core.render.SpatialRenderLoopContract
import com.elitec.spatial_core.render.buildOrbitFrameSnapshot
import com.elitec.spatial_core.scene.RenderableNode
import com.elitec.spatial_gesture.OrbitGestureDelta
import com.elitec.spatial_gesture.PinchZoomDelta
import com.elitec.spatial_renderer.render.FrameScheduler
import com.elitec.spatial_renderer.render.RenderBackend
import com.elitec.spatial_renderer.render.RenderFrame

class SpatialRuntime(
    private val renderBackend: RenderBackend,
    private val frameScheduler: FrameScheduler,
    private val cameraRuntime: CameraRuntimeContract,
) : SpatialRenderLoopContract {

    private var initialized = false

    /**
     * Viewport aspect ratio (width / height), synced from `onSurfaceChanged` via the render host.
     * Defaults to 1:1 until the surface reports its real size, matching the renderer's own default.
     */
    @Volatile private var aspectRatio: Float = 1f

    override fun onInitialize() {
        initialized = true
    }

    /** Synchronizes the viewport aspect ratio used to populate [FrameSnapshot.viewProjection]. */
    fun updateViewport(aspectRatio: Float) {
        this.aspectRatio = if (aspectRatio.isFinite() && aspectRatio > 0f) aspectRatio else 1f
    }

    override fun onFrame(snapshot: FrameSnapshot) {
        onFrame(
            snapshot = snapshot,
            nodes = emptyList(),
            cameraSnapshot = cameraRuntime.snapshot(),
        )
    }

    fun onFrame(
        snapshot: FrameSnapshot,
        nodes: List<RenderableNode>,
        cameraSnapshot: CameraSnapshot,
    ) {
        if (!initialized) return

        syncCameraSnapshot(cameraSnapshot)
        renderBackend.render(
            RenderFrame(
                frameTimeNanos = snapshot.frameTimeNanos,
                nodes = nodes,
                cameraState = cameraRuntime.snapshot(),
                clearColor = snapshot.clearColor,
            )
        )
    }

    fun renderFrame(
        frameTimeNanos: Long,
        nodes: List<RenderableNode>,
        cameraSnapshot: CameraSnapshot,
    ) {
        onFrame(
            snapshot = buildOrbitFrameSnapshot(
                frameTimeNanos = frameTimeNanos,
                cameraSnapshot = cameraSnapshot,
                aspectRatio = aspectRatio,
            ),
            nodes = nodes,
            cameraSnapshot = cameraSnapshot,
        )
    }

    fun requestFrame(
        nodes: List<RenderableNode>,
        cameraSnapshot: CameraSnapshot,
    ) {
        frameScheduler.requestFrame { scheduledFrame ->
            renderFrame(
                frameTimeNanos = scheduledFrame.frameTimeNanos,
                nodes = nodes,
                cameraSnapshot = cameraSnapshot,
            )
        }
    }

    override fun onShutdown() {
        initialized = false
        // Item 1.1 follow-up: cancel any in-flight postFrameCallback so it can't fire against a
        // torn-down runtime/renderer after shutdown.
        frameScheduler.cancel()
    }

    fun onOrbitGesture(delta: OrbitGestureDelta) {
        applyCameraDelta(
            delta = CameraDelta(deltaYaw = delta.deltaYaw, deltaPitch = delta.deltaPitch),
            source = CameraUpdateSource.Gesture,
        )
    }

    fun onPinchGesture(delta: PinchZoomDelta) {
        applyCameraDelta(
            delta = CameraDelta(zoomScaleDelta = delta.scaleDelta),
            source = CameraUpdateSource.Gesture,
        )
    }

    /** Synchronizes the runtime camera from the official Core #1 snapshot contract. */
    fun syncCameraSnapshot(snapshot: CameraSnapshot) {
        cameraRuntime.syncSnapshot(snapshot)
    }

    /** Applies a Compose- or runtime-produced camera delta through the shared camera contract. */
    fun applyCameraDelta(
        delta: CameraDelta,
        source: CameraUpdateSource = CameraUpdateSource.Gesture,
    ) {
        cameraRuntime.applyDelta(delta = delta, source = source)
    }
}