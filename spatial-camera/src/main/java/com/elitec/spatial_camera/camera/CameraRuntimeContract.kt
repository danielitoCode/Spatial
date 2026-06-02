package com.elitec.spatial_camera.camera

import com.elitec.spatial_camera.animation.CameraAnimationSpec
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.camera.CameraUpdateSource

/**
 * Runtime contract with atomic camera operations only.
 */
interface CameraRuntimeContract {
    fun syncSnapshot(snapshot: CameraSnapshot)
    fun orbitTo(yaw: Float, pitch: Float, source: CameraUpdateSource = CameraUpdateSource.Gesture)
    fun applyDelta(delta: CameraDelta, source: CameraUpdateSource = CameraUpdateSource.Gesture)
    fun zoomTo(zoom: Float, source: CameraUpdateSource = CameraUpdateSource.Gesture)
    fun jumpTo(
        yaw: Float,
        pitch: Float,
        zoom: Float,
        source: CameraUpdateSource = CameraUpdateSource.Remote,
    )

    suspend fun animateTo(
        yaw: Float,
        pitch: Float,
        zoom: Float,
        durationMs: Long = CameraAnimationSpec.DEFAULT_DURATION_MS,
    ) {
        animateTo(yaw = yaw, pitch = pitch, zoom = zoom, motion = CameraAnimationSpec.Tween(durationMs = durationMs))
    }

    suspend fun animateTo(yaw: Float, pitch: Float, zoom: Float, motion: CameraAnimationSpec)
    fun snapshot(): CameraSnapshot
}