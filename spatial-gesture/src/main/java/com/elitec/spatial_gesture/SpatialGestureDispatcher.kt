package com.elitec.spatial_gesture

import com.elitec.spatial_camera.CameraRuntimeContract
import com.elitec.spatial_camera.CameraDelta
import com.elitec.spatial_camera.CameraUpdateSource

class SpatialGestureDispatcher(
    private val cameraRuntime: CameraRuntimeContract,
) : SpatialGesturePublisher {

    override fun publishOrbit(delta: OrbitGestureDelta) {
        cameraRuntime.applyDelta(
            delta = CameraDelta(deltaYaw = delta.deltaYaw, deltaPitch = delta.deltaPitch),
            source = CameraUpdateSource.Gesture
        )
    }

    override fun publishPinch(delta: PinchZoomDelta) {
        cameraRuntime.applyDelta(
            delta = CameraDelta(zoomScaleDelta = delta.scaleDelta),
            source = CameraUpdateSource.Gesture
        )
    }
}