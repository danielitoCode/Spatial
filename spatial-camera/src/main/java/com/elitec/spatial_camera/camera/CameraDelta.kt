package com.elitec.spatial_camera.camera

import com.elitec.spatial_camera.gesture.GestureMotionPolicy

data class CameraDelta(
    val deltaYaw: Float = 0f,
    val deltaPitch: Float = 0f,
    val zoomScaleDelta: Float = 1f,
    val motionPolicy: GestureMotionPolicy = GestureMotionPolicy.Adaptive,
)