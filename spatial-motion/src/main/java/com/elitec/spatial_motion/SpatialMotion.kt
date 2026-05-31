package com.elitec.spatial_motion

import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.sqrt

/** Maps linear animation progress to visual animation progress. */
fun interface MotionEasing {
    fun transform(fraction: Float): Float

    companion object {
        val Linear = MotionEasing { fraction -> fraction }
        val SmoothStep = MotionEasing { fraction -> smoothStep(fraction) }
    }
}

/** Shared camera-motion profile used by runtime and UI adapters. */
data class CameraMotionProfile(
    val minDurationMillis: Long = DefaultAdaptiveAnimationMinDurationMillis,
    val maxDurationMillis: Long = DefaultAdaptiveAnimationMaxDurationMillis,
    val targetAngularVelocityDegreesPerSecond: Float = DefaultTargetAngularVelocityDegreesPerSecond,
    val targetZoomVelocityPerSecond: Float = DefaultTargetZoomVelocityPerSecond,
    val easing: MotionEasing = MotionEasing.SmoothStep,
)

/** Resolved immutable plan for a basic Core #1 camera/transform animation. */
data class CameraMotionPlan(
    val durationMillis: Long,
    val targetYawDegrees: Float,
    val targetPitchDegrees: Float,
    val targetZoom: Float,
    val easing: MotionEasing,
)

/**
 * Resolves camera animation duration, easing, shortest yaw path, and final camera safety clamps.
 *
 * This shared planner intentionally covers only Core #1 basic camera/transform motion. Skeletal
 * animation, clip blending, keyframe timelines, and advanced sequencing belong to future motion
 * systems layered above this primitive.
 */
fun resolveCameraMotionPlan(
    startYawDegrees: Float,
    startPitchDegrees: Float,
    startZoom: Float,
    targetYawDegrees: Float,
    targetPitchDegrees: Float,
    targetZoom: Float,
    profile: CameraMotionProfile = CameraMotionProfile(),
    explicitDurationMillis: Long? = null,
    instant: Boolean = false,
    minPitchDegrees: Float,
    maxPitchDegrees: Float,
    minZoom: Float,
    maxZoom: Float,
): CameraMotionPlan {
    val safeStartYaw = startYawDegrees.finiteOrZero()
    val safeStartPitch = startPitchDegrees.finiteOrZero().coerceIn(minPitchDegrees, maxPitchDegrees)
    val safeStartZoom = startZoom.finiteOr(DefaultCameraZoom).coerceIn(minZoom, maxZoom).coerceAtLeast(MinCameraZoomForMotion)
    val safeTargetPitch = targetPitchDegrees.finiteOr(safeStartPitch).coerceIn(minPitchDegrees, maxPitchDegrees)
    val safeTargetZoom = targetZoom.finiteOr(safeStartZoom).coerceIn(minZoom, maxZoom).coerceAtLeast(MinCameraZoomForMotion)
    val yawDelta = shortestAngleDeltaDegrees(safeStartYaw, targetYawDegrees.finiteOr(safeStartYaw))
    val resolvedTargetYaw = safeStartYaw + yawDelta
    val pitchDelta = safeTargetPitch - safeStartPitch
    val zoomRelativeDelta = abs(ln(safeTargetZoom / safeStartZoom))

    val durationMillis = when {
        instant -> 0L
        explicitDurationMillis != null -> explicitDurationMillis.coerceAtLeast(0L)
        else -> adaptiveCameraMotionDurationMillis(
            yawDeltaDegrees = yawDelta,
            pitchDeltaDegrees = pitchDelta,
            zoomRelativeDelta = zoomRelativeDelta,
            profile = profile,
        )
    }

    return CameraMotionPlan(
        durationMillis = durationMillis,
        targetYawDegrees = resolvedTargetYaw,
        targetPitchDegrees = safeTargetPitch,
        targetZoom = safeTargetZoom,
        easing = profile.easing,
    )
}

private fun adaptiveCameraMotionDurationMillis(
    yawDeltaDegrees: Float,
    pitchDeltaDegrees: Float,
    zoomRelativeDelta: Float,
    profile: CameraMotionProfile,
): Long {
    val minDurationMillis = profile.minDurationMillis.coerceAtLeast(0L)
    val maxDurationMillis = profile.maxDurationMillis.coerceAtLeast(minDurationMillis)
    val angularVelocity = profile.targetAngularVelocityDegreesPerSecond
        .takeIf { it.isFinite() && it > 0f }
        ?: return DefaultAnimationDurationMillis.coerceIn(minDurationMillis, maxDurationMillis)
    val zoomVelocity = profile.targetZoomVelocityPerSecond
        .takeIf { it.isFinite() && it > 0f }
        ?: return DefaultAnimationDurationMillis.coerceIn(minDurationMillis, maxDurationMillis)

    val angularDistanceDegrees = sqrt(yawDeltaDegrees * yawDeltaDegrees + pitchDeltaDegrees * pitchDeltaDegrees)
    val angularDurationMillis = angularDistanceDegrees / angularVelocity * 1_000f
    val zoomDurationMillis = zoomRelativeDelta / zoomVelocity * 1_000f
    val resolvedDuration = max(angularDurationMillis, zoomDurationMillis)
        .takeIf { it.isFinite() }
        ?.toLong()
        ?: DefaultAnimationDurationMillis

    return resolvedDuration.coerceIn(minDurationMillis, maxDurationMillis)
}

fun shortestAngleDeltaDegrees(startDegrees: Float, targetDegrees: Float): Float {
    val rawDelta = targetDegrees - startDegrees
    return (((rawDelta % 360f) + 540f) % 360f) - 180f
}

fun smoothStep(value: Float): Float {
    val t = value.coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}

private fun Float.finiteOr(defaultValue: Float): Float = if (isFinite()) this else defaultValue

private fun Float.finiteOrZero(): Float = finiteOr(0f)

const val DefaultAnimationDurationMillis = 450L
const val DefaultAdaptiveAnimationMinDurationMillis = 120L
const val DefaultAdaptiveAnimationMaxDurationMillis = 1_200L
const val DefaultTargetAngularVelocityDegreesPerSecond = 180f
const val DefaultTargetZoomVelocityPerSecond = 1.75f
private const val DefaultCameraZoom = 1f
private const val MinCameraZoomForMotion = 0.0001f