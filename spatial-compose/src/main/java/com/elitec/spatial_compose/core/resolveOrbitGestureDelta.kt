package com.elitec.spatial_compose.core

import androidx.compose.ui.unit.IntSize
import com.elitec.spatial_compose.scene.GestureSensitivity
import com.elitec.spatial_compose.scene.SceneNode

internal fun resolveOrbitGestureDelta(
    dx: Float,
    dy: Float,
    cameraZoom: Float,
    sceneNodes: List<SceneNode>,
    viewportSize: IntSize = IntSize.Zero,
    sensitivity: GestureSensitivity = GestureSensitivity.Adaptive,
): OrbitGestureDeltaDegrees {
    val degreesPerPixel = when (sensitivity) {
        GestureSensitivity.Adaptive -> adaptiveOrbitDegreesPerPixel(cameraZoom, sceneNodes, viewportSize)
        is GestureSensitivity.Fixed -> sensitivity.degreesPerPixel.takeIf { it.isFinite() && it > 0f }
            ?: DefaultOrbitDegreesPerPixel
    }
    return OrbitGestureDeltaDegrees(
        yawDegrees = (dx * degreesPerPixel).coerceIn(-MaxOrbitDegreesPerStep, MaxOrbitDegreesPerStep),
        pitchDegrees = (dy * degreesPerPixel).coerceIn(-MaxOrbitDegreesPerStep, MaxOrbitDegreesPerStep),
    )
}

private const val DefaultOrbitDegreesPerPixel = 0.25f
private const val MaxOrbitDegreesPerStep = 32f