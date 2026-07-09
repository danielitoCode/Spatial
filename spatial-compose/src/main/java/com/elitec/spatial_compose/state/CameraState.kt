package com.elitec.spatial_compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.elitec.spatial_camera.camera.CameraDelta
import com.elitec.spatial_camera.camera.CameraRuntimeContract
import com.elitec.spatial_camera.camera.SpatialCamera
import com.elitec.spatial_camera.gesture.GestureMotionPolicy
import com.elitec.spatial_compose.camera.ComposeFrameCameraAnimationScheduler
import com.elitec.spatial_camera.animation.CameraAnimationSpec as RuntimeMotionSpec
import com.elitec.spatial_compose.motion.MotionSpec
import com.elitec.spatial_compose.motion.toCameraMotionProfile
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.camera.CameraUpdateSource
import com.elitec.spatial_motion.resolveCameraMotionPlan
import com.elitec.spatial_units.Angle
import com.elitec.spatial_units.deg
import kotlin.math.PI

@Stable
class CameraState internal constructor(
    yaw: Angle,
    pitch: Angle,
    zoom: Float,
    cameraRuntime: CameraRuntimeContract? = null,
) {
    private val cameraRuntime: CameraRuntimeContract = cameraRuntime ?: SpatialCamera(
        initialState = CameraSnapshot(
            yaw = yaw.toDegrees(),
            pitch = pitch.toDegrees(),
            zoom = zoom,
        ),
        animationScheduler = ComposeFrameCameraAnimationScheduler(onFrameApplied = { syncFromRuntime() }),
    )
    var yaw: Angle by mutableStateOf(yaw)
        private set
    var pitch: Angle by mutableStateOf(pitch)
        private set
    var zoom: Float by mutableFloatStateOf(zoom.coerceIn(MIN_ZOOM, MAX_ZOOM))
        private set
    var version: Long by mutableLongStateOf(0L)
        private set
    var source: CameraUpdateSource by mutableStateOf(CameraUpdateSource.Gesture)
        private set

    init {
        syncFromRuntime()
    }

    fun orbitTo(
        yaw: Angle = this.yaw,
        pitch: Angle = this.pitch,
        source: CameraUpdateSource = CameraUpdateSource.Remote,
    ) {
        cameraRuntime.orbitTo(
            yaw = yaw.toDegrees(),
            pitch = pitch.toDegrees(),
            source = source,
        )
        syncFromRuntime()
    }

    fun orbitBy(
        deltaYawDegrees: Float,
        deltaPitchDegrees: Float,
        source: CameraUpdateSource = CameraUpdateSource.Gesture,
    ) {
        cameraRuntime.applyDelta(
            delta = CameraDelta(
                deltaYaw = deltaYawDegrees,
                deltaPitch = deltaPitchDegrees,
                motionPolicy = GestureMotionPolicy.Raw,
            ),
            source = source,
        )
        syncFromRuntime()
    }

    fun zoomTo(zoom: Float, source: CameraUpdateSource = CameraUpdateSource.Remote) {
        cameraRuntime.zoomTo(zoom = zoom, source = source)
        syncFromRuntime()
    }

    /**
     * Applies a multiplicative visual magnification delta. Values above `1f` zoom in visually
     * (objects appear closer/larger); values below `1f` zoom out visually.
     */
    fun zoomBy(scaleDelta: Float, source: CameraUpdateSource = CameraUpdateSource.Gesture) {
        cameraRuntime.applyDelta(
            delta = CameraDelta(
                zoomScaleDelta = scaleDelta,
                motionPolicy = GestureMotionPolicy.Raw,
            ),
            source = source,
        )
        syncFromRuntime()
    }

    fun jumpTo(
        yaw: Angle = this.yaw,
        pitch: Angle = this.pitch,
        zoom: Float = this.zoom,
        source: CameraUpdateSource = CameraUpdateSource.Remote,
    ) {
        cameraRuntime.jumpTo(
            yaw = yaw.toDegrees(),
            pitch = pitch.toDegrees(),
            zoom = zoom,
            source = source,
        )
        syncFromRuntime()
    }

    fun syncSnapshot(snapshot: CameraSnapshot) {
        cameraRuntime.syncSnapshot(snapshot)
        syncFromRuntime()
    }

    suspend fun animateTo(
        yaw: Angle = this.yaw,
        pitch: Angle = this.pitch,
        zoom: Float = this.zoom,
        durationMillis: Long? = null,
        motion: MotionSpec = MotionSpec.Adaptive,
    ) {
        val plan = resolveCameraMotionPlan(
            startYawDegrees = this.yaw.toDegrees(),
            startPitchDegrees = this.pitch.toDegrees(),
            startZoom = this.zoom,
            targetYawDegrees = yaw.toDegrees(),
            targetPitchDegrees = pitch.coercePitch().toDegrees(),
            targetZoom = zoom.coerceIn(MIN_ZOOM, MAX_ZOOM),
            profile = motion.toCameraMotionProfile(),
            explicitDurationMillis = durationMillis,
            instant = motion.instant,
            minPitchDegrees = MIN_PITCH_DEGREES,
            maxPitchDegrees = MAX_PITCH_DEGREES,
            minZoom = MIN_ZOOM,
            maxZoom = MAX_ZOOM,
        )
        val runtimeMotion: RuntimeMotionSpec = if (plan.durationMillis <= 0L || motion.instant) {
            RuntimeMotionSpec.Instant
        } else {
            RuntimeMotionSpec.Tween(
                durationMs = plan.durationMillis,
                easing = plan.easing,
            )
        }

        cameraRuntime.animateTo(
            yaw = plan.targetYawDegrees,
            pitch = plan.targetPitchDegrees,
            zoom = plan.targetZoom,
            motion = runtimeMotion,
        )
        syncFromRuntime()
    }

    fun snapshot(): CameraSnapshot {
        // Read Compose state properties to register state observation in the calling scope.
        // Without reading these, calls to snapshot() read directly from the non-state cameraRuntime,
        // meaning Compose never detects camera changes and the scene remains frozen.
        val currentVersion = version
        val currentYaw = yaw
        val currentPitch = pitch
        val currentZoom = zoom
        return cameraRuntime.snapshot()
    }

    private fun syncFromRuntime() {
        val snapshot = cameraRuntime.snapshot()
        yaw = snapshot.yaw.deg
        pitch = snapshot.pitch.deg
        zoom = snapshot.zoom
        version = snapshot.version
        source = snapshot.source
    }

    private fun Angle.coercePitch(): Angle = toDegrees().coerceIn(MIN_PITCH_DEGREES, MAX_PITCH_DEGREES).deg

    private companion object {
        const val MIN_PITCH_DEGREES = CameraSnapshot.MIN_PITCH_DEGREES
        const val MAX_PITCH_DEGREES = CameraSnapshot.MAX_PITCH_DEGREES
        const val MIN_ZOOM = CameraSnapshot.MIN_ZOOM
        const val MAX_ZOOM = CameraSnapshot.MAX_ZOOM
    }
}

private fun Angle.toDegrees(): Float = (radians * 180f / PI.toFloat())
