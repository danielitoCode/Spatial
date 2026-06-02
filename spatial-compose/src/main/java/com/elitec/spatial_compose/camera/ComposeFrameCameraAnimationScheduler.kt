package com.elitec.spatial_compose.camera

import androidx.compose.runtime.withFrameNanos
import com.elitec.spatial_camera.animation.CameraAnimationScheduler
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_camera.camera.SpatialCamera
import com.elitec.spatial_camera.camera.CameraRuntimeContract
import com.elitec.spatial_compose.state.CameraState

/**
 * Compose-owned camera adapter for Scene.
 *
 * [CameraSnapshot] is the single Core #1 camera contract. This state holder exposes Compose-friendly
 * observable fields while delegating normalization and atomic mutations to [CameraRuntimeContract]
 * (the default engine is [SpatialCamera]). The renderer only receives immutable snapshots.
 *
 * Public API guidance: Compose callers should use [CameraState] and [CameraState.animateTo]; runtime
 * callers should use [SpatialCamera]. Both APIs synchronize through [CameraSnapshot].
 */

internal class ComposeFrameCameraAnimationScheduler(
    private val onFrameApplied: () -> Unit,
) : CameraAnimationScheduler {
    override suspend fun schedule(durationMs: Long, onFrame: (elapsedMs: Long) -> Unit) {
        val safeDurationMillis = durationMs.coerceAtLeast(0L)
        if (safeDurationMillis == 0L) {
            onFrame(0L)
            onFrameApplied()
            return
        }

        val durationNanos = safeDurationMillis * NANOS_PER_MILLISECOND
        val startTimeNanos = withFrameNanos { it }
        onFrame(0L)
        onFrameApplied()

        while (true) {
            val frameTimeNanos = withFrameNanos { it }
            val elapsedNanos = (frameTimeNanos - startTimeNanos).coerceAtLeast(0L)
            val elapsedMillis = (elapsedNanos / NANOS_PER_MILLISECOND).coerceAtMost(safeDurationMillis)
            onFrame(elapsedMillis)
            onFrameApplied()
            if (elapsedNanos >= durationNanos) break
        }
    }

    private companion object {
        const val NANOS_PER_MILLISECOND = 1_000_000L
    }
}