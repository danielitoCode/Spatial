package com.elitec.spatial_runtime

import com.elitec.spatial_camera.CameraRuntimeContract
import com.elitec.spatial_camera.CameraDelta
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.camera.CameraUpdateSource
import com.elitec.spatial_core.render.FrameSnapshot
import com.elitec.spatial_core.render.SpatialRenderLoopContract
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

    override fun onInitialize() {
        initialized = true
    }

    override fun onFrame(snapshot: FrameSnapshot) {
        if (!initialized) return

        renderBackend.render(
            RenderFrame(
                frameTimeNanos = snapshot.frameTimeNanos,
                cameraState = cameraRuntime.snapshot(),
            )
        )
    }

    override fun onShutdown() {
        initialized = false
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