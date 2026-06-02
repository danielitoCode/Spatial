package com.elitec.spatial_gesture

import com.elitec.spatial_camera.camera.CameraDelta
import com.elitec.spatial_camera.camera.CameraRuntimeContract
import com.elitec.spatial_camera.gesture.GestureMotionPolicy
import com.elitec.spatial_core.camera.CameraUpdateSource

class SpatialGestureDispatcher(
    private val cameraRuntime: CameraRuntimeContract,
    private val motionPolicy: GestureMotionPolicy = GestureMotionPolicy.Adaptive,
) : SpatialGesturePublisher {

    override fun publishOrbit(delta: OrbitGestureDelta) {
        val normalized = delta.normalizedBy(motionPolicy)
        cameraRuntime.applyDelta(
            delta = CameraDelta(
                deltaYaw = normalized.deltaYaw,
                deltaPitch = normalized.deltaPitch,
                motionPolicy = motionPolicy,
            ),
            source = CameraUpdateSource.Gesture
        )
    }

    override fun publishPinch(delta: PinchZoomDelta) {
        val normalized = delta.normalizedBy(motionPolicy)
        cameraRuntime.applyDelta(
            delta = CameraDelta(
                zoomScaleDelta = normalized.scaleDelta,
                motionPolicy = motionPolicy,
            ),
            source = CameraUpdateSource.Gesture
        )
    }
}

fun OrbitGestureDelta.normalizedBy(policy: GestureMotionPolicy = GestureMotionPolicy.Adaptive): OrbitGestureDelta {
    val yawLimit = policy.normalizedYawLimit()
    val pitchLimit = policy.normalizedPitchLimit()
    return copy(
        deltaYaw = deltaYaw.sanitizeFinite().coerceIn(-yawLimit, yawLimit),
        deltaPitch = deltaPitch.sanitizeFinite().coerceIn(-pitchLimit, pitchLimit),
    )
}

fun PinchZoomDelta.normalizedBy(policy: GestureMotionPolicy = GestureMotionPolicy.Adaptive): PinchZoomDelta {
    val zoomLimit = policy.normalizedZoomLimit()
    val sanitizedScale = if (scaleDelta.isFinite() && scaleDelta > 0f) scaleDelta else 1f
    return copy(
        scaleDelta = sanitizedScale.coerceIn(
            minimumValue = 1f - zoomLimit,
            maximumValue = 1f + zoomLimit,
        )
    )
}

private fun GestureMotionPolicy.normalizedYawLimit(): Float = normalizedLimit(
    requestedLimit = maxYawDeltaPerStep,
    adaptiveLimit = GestureMotionPolicy.DEFAULT_MAX_YAW_DELTA_PER_STEP,
    hardLimit = GestureMotionPolicy.HARD_MAX_YAW_DELTA_PER_STEP,
)

private fun GestureMotionPolicy.normalizedPitchLimit(): Float = normalizedLimit(
    requestedLimit = maxPitchDeltaPerStep,
    adaptiveLimit = GestureMotionPolicy.DEFAULT_MAX_PITCH_DELTA_PER_STEP,
    hardLimit = GestureMotionPolicy.HARD_MAX_PITCH_DELTA_PER_STEP,
)

private fun GestureMotionPolicy.normalizedZoomLimit(): Float = normalizedLimit(
    requestedLimit = maxZoomScaleDeltaPerStep,
    adaptiveLimit = GestureMotionPolicy.DEFAULT_MAX_ZOOM_SCALE_DELTA_PER_STEP,
    hardLimit = GestureMotionPolicy.HARD_MAX_ZOOM_SCALE_DELTA_PER_STEP,
)

private fun GestureMotionPolicy.normalizedLimit(
    requestedLimit: Float,
    adaptiveLimit: Float,
    hardLimit: Float,
): Float {
    val sanitized = if (requestedLimit.isFinite() && requestedLimit > 0f) requestedLimit else adaptiveLimit
    val requested = if (mode == GestureMotionPolicy.Mode.Raw) hardLimit else sanitized
    return requested.coerceIn(0f, hardLimit)
}

private fun Float.sanitizeFinite(): Float = if (isFinite()) this else 0f