package com.elitec.spatial_runtime

import com.elitec.spatial_camera.CameraRuntimeContract
import com.elitec.spatial_core.render.SpatialRenderLoopContract
import com.elitec.spatial_gesture.OrbitGestureDelta
import com.elitec.spatial_gesture.PinchZoomDelta
import com.elitec.spatial_renderer.render.FrameScheduler
import com.elitec.spatial_renderer.render.RenderBackend
import com.elitec.spatial_renderer.render.RenderFram

class SpatialRuntime(
    private val renderBackend: RenderBackend,
    private val frameScheduler: FrameScheduler,
    private val cameraRuntime: CameraRuntimeContract,
) : SpatialRenderLoopContract {

    override fun onInitialize() = Unit

    override fun onFrame(frameTimeNanos: Long) {
        renderBackend.render(
            RenderFrame(
                frameTimeNanos = frameTimeNanos,
                cameraState = cameraRuntime.snapshot(),
            )
        )
    }

    fun onOrbitGesture(delta: OrbitGestureDelta) {
        cameraRuntime.updateOrbit(delta.deltaYaw, delta.deltaPitch)
    }

    fun onPinchGesture(delta: PinchZoomDelta) {
        cameraRuntime.updateZoom(delta.scaleDelta)
    }

    fun requestRenderFrame() {
        frameScheduler.requestFrame { onFrame(it.frameTimeNanos) }
    }

    override fun onShutdown() = Unit
}