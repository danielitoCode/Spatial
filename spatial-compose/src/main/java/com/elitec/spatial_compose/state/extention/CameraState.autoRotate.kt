package com.elitec.spatial_compose.state.extention

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import com.elitec.spatial_compose.CameraState

@Composable
fun CameraState.autoRotate(
    isActive: Boolean,
    deltaYawDegrees: Float = 0.2f,
    deltaPitchDegrees: Float = 0f,
): CameraState {
    // Auto-rotate effect: advances yaw using frame clock. Only active when toggled.
    LaunchedEffect(isActive) {
        while (isActive) {
            withFrameNanos { _ ->
                this@autoRotate.orbitBy(deltaYawDegrees = deltaYawDegrees, deltaPitchDegrees = deltaPitchDegrees)
            }
        }
    }
    return this
}