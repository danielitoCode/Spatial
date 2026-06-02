package com.elitec.spatial_camera.camera

import com.elitec.spatial_camera.animation.CameraAnimationScheduler
import com.elitec.spatial_camera.animation.CameraAnimationSpec
import com.elitec.spatial_camera.animation.FixedStepCameraAnimationScheduler
import com.elitec.spatial_camera.gesture.GestureMotionPolicy
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.camera.CameraUpdateSource
import com.elitec.spatial_motion.CameraMotionProfile
import com.elitec.spatial_motion.MotionEasing
import com.elitec.spatial_motion.resolveCameraMotionPlan

/**
 * Core #1 camera engine.
 *
 * The single source of truth exchanged between Compose, runtime, and renderer is [CameraSnapshot].
 * [SpatialCamera] owns normalization and atomic mutations for the runtime route; UI state holders
 * such as Compose `CameraState` are adapters that synchronize snapshots to and from this contract.
 */
class SpatialCamera(
    initialState: CameraSnapshot = CameraSnapshot(),
    private val animationScheduler: CameraAnimationScheduler = FixedStepCameraAnimationScheduler(),
    private val defaultGestureMotionPolicy: GestureMotionPolicy = GestureMotionPolicy.Adaptive,
) : CameraRuntimeContract {
    private var state: CameraSnapshot = normalize(initialState)

    override fun syncSnapshot(snapshot: CameraSnapshot) {
        state = normalize(snapshot)
    }

    override fun orbitTo(yaw: Float, pitch: Float, source: CameraUpdateSource) {
        writeAtomic(source) {
            copy(
                yaw = yaw,
                pitch = pitch.coerceIn(CameraSnapshot.MIN_PITCH_DEGREES, CameraSnapshot.MAX_PITCH_DEGREES),
            )
        }
    }

    override fun applyDelta(delta: CameraDelta, source: CameraUpdateSource) {
        val safeDelta = normalizeDelta(delta, source)
        writeAtomic(source) {
            copy(
                yaw = yaw + safeDelta.deltaYaw,
                pitch = (pitch + safeDelta.deltaPitch).coerceIn(CameraSnapshot.MIN_PITCH_DEGREES, CameraSnapshot.MAX_PITCH_DEGREES),
                zoom = (zoom * safeDelta.zoomScaleDelta).coerceIn(CameraSnapshot.MIN_ZOOM, CameraSnapshot.MAX_ZOOM),
            )
        }
    }

    override fun zoomTo(zoom: Float, source: CameraUpdateSource) {
        writeAtomic(source) {
            copy(zoom = zoom.coerceIn(CameraSnapshot.MIN_ZOOM, CameraSnapshot.MAX_ZOOM))
        }
    }

    override fun jumpTo(
        yaw: Float,
        pitch: Float,
        zoom: Float,
        source: CameraUpdateSource,
    ) {
        writeAtomic(source) {
            copy(
                yaw = yaw,
                pitch = pitch.coerceIn(CameraSnapshot.MIN_PITCH_DEGREES, CameraSnapshot.MAX_PITCH_DEGREES),
                zoom = zoom.coerceIn(CameraSnapshot.MIN_ZOOM, CameraSnapshot.MAX_ZOOM),
            )
        }
    }

    override suspend fun animateTo(yaw: Float, pitch: Float, zoom: Float, motion: CameraAnimationSpec) {
        when (motion) {
            CameraAnimationSpec.Instant -> jumpTo(
                yaw = yaw,
                pitch = pitch,
                zoom = zoom,
                source = CameraUpdateSource.Animation,
            )

            is CameraAnimationSpec.Tween -> animateTween(
                yaw = yaw,
                pitch = pitch,
                zoom = zoom,
                durationMs = motion.durationMs,
                easing = motion.easing,
            )
        }
    }

    private suspend fun animateTween(
        yaw: Float,
        pitch: Float,
        zoom: Float,
        durationMs: Long,
        easing: CameraEasing,
    ) {
        val start = snapshot()
        val plan = resolveCameraMotionPlan(
            startYawDegrees = start.yaw,
            startPitchDegrees = start.pitch,
            startZoom = start.zoom,
            targetYawDegrees = yaw,
            targetPitchDegrees = pitch,
            targetZoom = zoom,
            profile = CameraMotionProfile(easing = easing),
            explicitDurationMillis = durationMs,
            minPitchDegrees = CameraSnapshot.MIN_PITCH_DEGREES,
            maxPitchDegrees = CameraSnapshot.MAX_PITCH_DEGREES,
            minZoom = CameraSnapshot.MIN_ZOOM,
            maxZoom = CameraSnapshot.MAX_ZOOM,
        )
        val safeDuration = plan.durationMillis

        animationScheduler.schedule(safeDuration) { elapsedMs ->
            val linearProgress = if (safeDuration == 0L) {
                1f
            } else {
                (elapsedMs.toFloat() / safeDuration.toFloat()).coerceIn(0f, 1f)
            }
            val easedProgress = plan.easing.transform(linearProgress).coerceIn(0f, 1f)

            writeAtomic(CameraUpdateSource.Animation) {
                copy(
                    yaw = lerp(start.yaw, plan.targetYawDegrees, easedProgress),
                    pitch = lerp(start.pitch, plan.targetPitchDegrees, easedProgress),
                    zoom = lerp(start.zoom, plan.targetZoom, easedProgress),
                )
            }
        }

        val current = snapshot()
        if (current.yaw != plan.targetYawDegrees || current.pitch != plan.targetPitchDegrees || current.zoom != plan.targetZoom) {
            jumpTo(
                yaw = plan.targetYawDegrees,
                pitch = plan.targetPitchDegrees,
                zoom = plan.targetZoom,
                source = CameraUpdateSource.Animation,
            )
        }
    }

    private fun lerp(start: Float, end: Float, progress: Float): Float =
        start + (end - start) * progress

    override fun snapshot(): CameraSnapshot = state

    private fun writeAtomic(
        source: CameraUpdateSource,
        transform: CameraSnapshot.() -> CameraSnapshot,
    ) {
        val current = state
        val candidate = normalize(current.transform())
        val selected = resolveConflict(current = current, incoming = candidate, source = source)
        state = selected.copy(version = current.version + 1, source = selected.source)
    }

    private fun resolveConflict(
        current: CameraSnapshot,
        incoming: CameraSnapshot,
        source: CameraUpdateSource,
    ): CameraSnapshot {
        return if (incoming.version == current.version) {
            incoming.copy(source = source)
        } else {
            val currentPrecedence = precedence(current.source)
            val incomingPrecedence = precedence(source)
            if (incomingPrecedence >= currentPrecedence) incoming.copy(source = source) else current
        }
    }

    private fun precedence(source: CameraUpdateSource): Int = when (source) {
        CameraUpdateSource.Gesture -> 3
        CameraUpdateSource.Remote -> 2
        CameraUpdateSource.Animation -> 1
    }

    private fun normalizeDelta(delta: CameraDelta, source: CameraUpdateSource): CameraDelta {
        val policy = if (source == CameraUpdateSource.Gesture) delta.motionPolicy else defaultGestureMotionPolicy
        val yawLimit = deltaLimit(
            requestedLimit = policy.maxYawDeltaPerStep,
            adaptiveLimit = GestureMotionPolicy.DEFAULT_MAX_YAW_DELTA_PER_STEP,
            hardLimit = GestureMotionPolicy.HARD_MAX_YAW_DELTA_PER_STEP,
            raw = policy.mode == GestureMotionPolicy.Mode.Raw,
        )
        val pitchLimit = deltaLimit(
            requestedLimit = policy.maxPitchDeltaPerStep,
            adaptiveLimit = GestureMotionPolicy.DEFAULT_MAX_PITCH_DELTA_PER_STEP,
            hardLimit = GestureMotionPolicy.HARD_MAX_PITCH_DELTA_PER_STEP,
            raw = policy.mode == GestureMotionPolicy.Mode.Raw,
        )
        val zoomLimit = deltaLimit(
            requestedLimit = policy.maxZoomScaleDeltaPerStep,
            adaptiveLimit = GestureMotionPolicy.DEFAULT_MAX_ZOOM_SCALE_DELTA_PER_STEP,
            hardLimit = GestureMotionPolicy.HARD_MAX_ZOOM_SCALE_DELTA_PER_STEP,
            raw = policy.mode == GestureMotionPolicy.Mode.Raw,
        )

        return delta.copy(
            deltaYaw = sanitizeFinite(delta.deltaYaw).coerceIn(-yawLimit, yawLimit),
            deltaPitch = sanitizeFinite(delta.deltaPitch).coerceIn(-pitchLimit, pitchLimit),
            zoomScaleDelta = normalizeScaleDelta(delta.zoomScaleDelta, zoomLimit),
        )
    }

    private fun deltaLimit(
        requestedLimit: Float,
        adaptiveLimit: Float,
        hardLimit: Float,
        raw: Boolean,
    ): Float {
        val sanitized = if (requestedLimit.isFinite() && requestedLimit > 0f) requestedLimit else adaptiveLimit
        val requested = if (raw) hardLimit else sanitized
        return requested.coerceIn(0f, hardLimit)
    }

    private fun normalizeScaleDelta(scale: Float, maxZoomScaleDeltaPerStep: Float): Float {
        val sanitized = if (scale.isFinite() && scale > 0f) scale else 1f
        return sanitized.coerceIn(
            minimumValue = 1f - maxZoomScaleDeltaPerStep,
            maximumValue = 1f + maxZoomScaleDeltaPerStep,
        )
    }

    private fun sanitizeFinite(value: Float): Float = if (value.isFinite()) value else 0f

    private fun normalize(snapshot: CameraSnapshot): CameraSnapshot = snapshot.copy(
        pitch = snapshot.pitch.coerceIn(CameraSnapshot.MIN_PITCH_DEGREES, CameraSnapshot.MAX_PITCH_DEGREES),
        zoom = snapshot.zoom.coerceIn(CameraSnapshot.MIN_ZOOM, CameraSnapshot.MAX_ZOOM),
    )
}

typealias CameraEasing = MotionEasing