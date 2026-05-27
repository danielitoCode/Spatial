package com.elitec.spatial_core.camera

/**
 * Snapshot inmutable del estado de la cámara.
 * Tipo compartido entre todos los módulos via spatial-core.
 * No depende de Compose, Android, ni OpenGL.
 */
data class CameraSnapshot(
    val yaw: Float = 0f,
    val pitch: Float = 0f,
    val zoom: Float = 1f,
)
