package com.elitec.spatial_gesture

import com.elitec.spatial_camera.CameraRuntimeContract

class SpatialGestureDispatcher(
    private val cameraRuntime: CameraRuntimeContract,
) : SpatialGesturePublisher {

    override fun publishOrbit(delta: OrbitGestureDelta) {
        cameraRuntime.updateOrbit(delta.deltaYaw, delta.deltaPitch)
    }

    override fun publishPinch(delta: PinchZoomDelta) {
        cameraRuntime.updateZoom(delta.scaleDelta)
    }
}