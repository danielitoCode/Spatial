package com.elitec.spatial_runtime

import com.elitec.spatial_camera.CameraRuntimeContract
import com.elitec.spatial_camera.CameraDelta
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
        cameraRuntime.applyDelta(
            delta = CameraDelta(deltaYaw = delta.deltaYaw, deltaPitch = delta.deltaPitch),
            source = CameraUpdateSource.Gesture
        )
    }

    fun onPinchGesture(delta: PinchZoomDelta) {
        cameraRuntime.applyDelta(
            delta = CameraDelta(zoomScaleDelta = delta.scaleDelta),
            source = CameraUpdateSource.Gesture
        )
    }
}