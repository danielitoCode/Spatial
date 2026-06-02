package com.elitec.spatial_compose.motion

import androidx.compose.runtime.Immutable
import com.elitec.spatial_motion.CameraMotionProfile
import com.elitec.spatial_motion.DefaultAdaptiveAnimationMaxDurationMillis
import com.elitec.spatial_motion.DefaultAdaptiveAnimationMinDurationMillis
import com.elitec.spatial_motion.DefaultTargetAngularVelocityDegreesPerSecond
import com.elitec.spatial_motion.DefaultTargetZoomVelocityPerSecond
import com.elitec.spatial_motion.MotionEasing

/**
 * Describes how camera animations choose their duration and interpolation curve.
 *
 * Compose keeps this [MotionSpec] as an adapter over the shared spatial-motion planner so the UI
 * API can expose adaptive policies without duplicating easing or duration math from runtime.
 * [Adaptive] derives duration from yaw, pitch, and relative zoom distance. [Instant] is the
 * explicit opt-in path for visual jumps. Use [custom] to tune the adaptive velocity and bounds.
 */
@Immutable
sealed class MotionSpec(
    val minDurationMillis: Long,
    val maxDurationMillis: Long,
    val targetAngularVelocityDegreesPerSecond: Float,
    val targetZoomVelocityPerSecond: Float,
    val easing: MotionEasing,
    val instant: Boolean,
) {
    data object Adaptive : MotionSpec(
        minDurationMillis = DefaultAdaptiveAnimationMinDurationMillis,
        maxDurationMillis = DefaultAdaptiveAnimationMaxDurationMillis,
        targetAngularVelocityDegreesPerSecond = DefaultTargetAngularVelocityDegreesPerSecond,
        targetZoomVelocityPerSecond = DefaultTargetZoomVelocityPerSecond,
        easing = MotionEasing.SmoothStep,
        instant = false,
    )

    data object Instant : MotionSpec(
        minDurationMillis = 0L,
        maxDurationMillis = 0L,
        targetAngularVelocityDegreesPerSecond = Float.POSITIVE_INFINITY,
        targetZoomVelocityPerSecond = Float.POSITIVE_INFINITY,
        easing = MotionEasing.Linear,
        instant = true,
    )

    class Custom internal constructor(
        minDurationMillis: Long,
        maxDurationMillis: Long,
        targetAngularVelocityDegreesPerSecond: Float,
        targetZoomVelocityPerSecond: Float,
        easing: MotionEasing,
        instant: Boolean,
    ) : MotionSpec(
        minDurationMillis = minDurationMillis,
        maxDurationMillis = maxDurationMillis,
        targetAngularVelocityDegreesPerSecond = targetAngularVelocityDegreesPerSecond,
        targetZoomVelocityPerSecond = targetZoomVelocityPerSecond,
        easing = easing,
        instant = instant,
    )

    companion object {
        fun custom(
            minDurationMillis: Long = DefaultAdaptiveAnimationMinDurationMillis,
            maxDurationMillis: Long = DefaultAdaptiveAnimationMaxDurationMillis,
            targetAngularVelocityDegreesPerSecond: Float = DefaultTargetAngularVelocityDegreesPerSecond,
            targetZoomVelocityPerSecond: Float = DefaultTargetZoomVelocityPerSecond,
            easing: MotionEasing = MotionEasing.SmoothStep,
            instant: Boolean = false,
        ): MotionSpec = Custom(
            minDurationMillis = minDurationMillis,
            maxDurationMillis = maxDurationMillis,
            targetAngularVelocityDegreesPerSecond = targetAngularVelocityDegreesPerSecond,
            targetZoomVelocityPerSecond = targetZoomVelocityPerSecond,
            easing = easing,
            instant = instant,
        )
    }
}

fun MotionSpec.toCameraMotionProfile(): CameraMotionProfile = CameraMotionProfile(
    minDurationMillis = minDurationMillis,
    maxDurationMillis = maxDurationMillis,
    targetAngularVelocityDegreesPerSecond = targetAngularVelocityDegreesPerSecond,
    targetZoomVelocityPerSecond = targetZoomVelocityPerSecond,
    easing = easing,
)