package com.elitec.spatial_camera.animation

fun interface CameraAnimationScheduler {
    suspend fun schedule(durationMs: Long, onFrame: (elapsedMs: Long) -> Unit)
}