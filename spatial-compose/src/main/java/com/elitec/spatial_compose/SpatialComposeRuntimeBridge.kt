package com.elitec.spatial_compose

import com.elitec.spatial_gesture.OrbitGestureDelta
import com.elitec.spatial_gesture.PinchZoomDelta

interface SpatialComposeRuntimeBridge {
    fun submitOrbit(delta: OrbitGestureDelta)
    fun submitPinch(delta: PinchZoomDelta)
    fun requestFrame()
}