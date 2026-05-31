package com.elitec.spatial_compose

import com.elitec.spatial_core.camera.CameraUpdateSource
import com.elitec.spatial_gesture.OrbitGestureDelta
import com.elitec.spatial_gesture.PinchZoomDelta

interface SpatialComposeRuntimeBridge {
    /**
     * Event ingress for camera updates.
     *
     * Thread policy: producers may enqueue from any thread, but runtime must serialize processing
     * on Main/UI or one dedicated event loop before mutating camera state.
     */
    fun submitOrbit(delta: OrbitGestureDelta, source: CameraUpdateSource = CameraUpdateSource.Gesture)
    fun submitPinch(delta: PinchZoomDelta, source: CameraUpdateSource = CameraUpdateSource.Gesture)
    fun requestFrame()
}