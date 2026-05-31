package com.elitec.spatial_core.camera

/**
 * Identifies who requested a camera update.
 *
 * Source precedence on conflicts (same frame/version):
 * Gesture > Remote > Animation.
 */
enum class CameraUpdateSource {
    Gesture,
    Remote,
    Animation,
}

/**
 * Snapshot inmutable del estado de la cámara.
 * Tipo compartido entre todos los módulos via spatial-core.
 * No depende de Compose, Android, ni OpenGL.
 */
data class CameraSnapshot(
    val yaw: Float = 0f,
    val pitch: Float = 0f,
    val zoom: Float = 1f,
    val version: Long = 0L,
    val source: CameraUpdateSource = CameraUpdateSource.Gesture,
) {
    companion object {
        const val MIN_PITCH_DEGREES = -89f
        const val MAX_PITCH_DEGREES = 89f
        const val MIN_ZOOM = 0.3f
        const val MAX_ZOOM = 4f
    }
}