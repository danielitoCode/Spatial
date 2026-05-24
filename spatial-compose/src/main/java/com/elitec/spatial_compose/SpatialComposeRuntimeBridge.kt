package com.elitec.spatial_compose

interface SpatialComposeRuntimeBridge {
    fun submitOrbit(delta: OrbitGestureDelta)
    fun submitPinch(delta: PinchZoomDelta)
    fun requestFrame()
}