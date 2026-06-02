package com.elitec.spatial_gesture

data class OrbitGestureDelta(
    val deltaYaw: Float,
    val deltaPitch: Float,
)

data class PinchZoomDelta(
    val scaleDelta: Float,
)

interface SpatialGesturePublisher {
    fun publishOrbit(delta: OrbitGestureDelta)
    fun publishPinch(delta: PinchZoomDelta)
}