package com.elitec.spatial_compose.state.extention

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import com.elitec.spatial_compose.CameraState
import com.elitec.spatial_core.camera.CameraUpdateSource

/**
 * Continuously advances yaw (and optional pitch) while [isActive].
 *
 * Task 2.1: skips frames while the user is orbiting/pinching the scene so auto-rotate
 * does not fight [CameraState.orbitBy] / [CameraState.zoomBy] from gestures.
 * Ticks use [CameraUpdateSource.Animation] so gesture updates keep precedence in the camera runtime.
 */
@Composable
fun CameraState.autoRotate(
    isActive: Boolean,
    deltaYawDegrees: Float = 0.2f,
    deltaPitchDegrees: Float = 0f,
): CameraState {
    LaunchedEffect(isActive, deltaYawDegrees, deltaPitchDegrees) {
        while (isActive) {
            withFrameNanos { frameTimeNanos ->
                if (!this@autoRotate.isAutoRotateSuppressed(frameTimeNanos)) {
                    this@autoRotate.orbitBy(
                        deltaYawDegrees = deltaYawDegrees,
                        deltaPitchDegrees = deltaPitchDegrees,
                        source = CameraUpdateSource.Animation,
                    )
                }
            }
        }
    }
    return this
}
