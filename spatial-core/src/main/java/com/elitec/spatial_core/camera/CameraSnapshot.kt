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
    /**
     * Visual magnification factor for the orbit camera.
     *
     * Values greater than `1f` mean the scene appears closer/larger, and values below `1f` mean
     * the scene appears farther/smaller. Renderers that implement an orbital camera should convert
     * this magnification to camera distance inversely (for example, `baseDistance / zoom`) rather
     * than treating [zoom] itself as the orbital distance.
     */
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