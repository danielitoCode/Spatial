package com.elitec.spatial_compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.elitec.spatial_units.Angle
import com.elitec.spatial_units.deg

@Composable
fun rememberCameraState(
    yaw: Angle = 0f.deg,
    pitch: Angle = 0f.deg,
    zoom: Float = 1f,
): CameraState = remember { CameraState(yaw, pitch, zoom) }
