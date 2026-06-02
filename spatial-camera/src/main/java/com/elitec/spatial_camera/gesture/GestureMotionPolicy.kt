package com.elitec.spatial_camera.gesture

/**
 * Public motion policy used to normalize gesture camera deltas.
 *
 * The default [Adaptive] mode clamps per-event yaw, pitch and zoom-scale changes
 * to smooth noisy gesture streams. [Raw] is intended for advanced callers that
 * intentionally request abrupt steps; [com.elitec.spatial_camera.camera.SpatialCamera] still applies hard safety
 * limits so invalid or extreme deltas cannot destabilize the camera.
 */
data class GestureMotionPolicy(
    val mode: Mode = Mode.Adaptive,
    val maxYawDeltaPerStep: Float = DEFAULT_MAX_YAW_DELTA_PER_STEP,
    val maxPitchDeltaPerStep: Float = DEFAULT_MAX_PITCH_DELTA_PER_STEP,
    val maxZoomScaleDeltaPerStep: Float = DEFAULT_MAX_ZOOM_SCALE_DELTA_PER_STEP,
) {
    enum class Mode {
        Adaptive,
        Raw,
    }

    companion object {
        const val DEFAULT_MAX_YAW_DELTA_PER_STEP = 12f
        const val DEFAULT_MAX_PITCH_DELTA_PER_STEP = 8f
        const val DEFAULT_MAX_ZOOM_SCALE_DELTA_PER_STEP = 0.25f

        const val HARD_MAX_YAW_DELTA_PER_STEP = 90f
        const val HARD_MAX_PITCH_DELTA_PER_STEP = 45f
        const val HARD_MAX_ZOOM_SCALE_DELTA_PER_STEP = 1f

        val Adaptive = GestureMotionPolicy()
        val Raw = GestureMotionPolicy(mode = Mode.Raw)
    }
}