package com.elitec.spatial_camera

data class CameraState(
    val yaw: Float = 0f,
    val pitch: Float = 0f,
    val zoom: Float = 1f,
)

interface CameraRuntimeContract {
    fun updateOrbit(deltaYaw: Float, deltaPitch: Float)
    fun updateZoom(scaleDelta: Float)
    fun snapshot(): CameraState
}

class SpatialCamera(
    private var state: CameraState = CameraState(),
) : CameraRuntimeContract {

    override fun updateOrbit(deltaYaw: Float, deltaPitch: Float) {
        state = state.copy(
            yaw = state.yaw + deltaYaw,
            pitch = (state.pitch + deltaPitch).coerceIn(-89f, 89f),
        )
    }

    override fun updateZoom(scaleDelta: Float) {
        val zoomDelta = if (scaleDelta <= 0f) 1f else scaleDelta
        state = state.copy(zoom = (state.zoom * zoomDelta).coerceIn(0.3f, 4f))
    }

    override fun snapshot(): CameraState = state
}