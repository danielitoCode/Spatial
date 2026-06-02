package com.elitec.spatial_camera.animation

import com.elitec.spatial_camera.camera.CameraEasing
import com.elitec.spatial_motion.MotionEasing

/**
 * Animation specification for camera transitions.
 */
sealed class CameraAnimationSpec {
    data object Instant : CameraAnimationSpec()
    data class Tween(
        val durationMs: Long = DEFAULT_DURATION_MS,
        val easing: CameraEasing = MotionEasing.Companion.SmoothStep,
    ) : CameraAnimationSpec()

    companion object {
        const val DEFAULT_DURATION_MS = 300L
    }
}