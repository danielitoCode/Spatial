package com.elitec.spatial_compose.core

import androidx.compose.ui.unit.IntSize
import com.elitec.spatial_compose.scene.GestureSensitivity
import com.elitec.spatial_compose.scene.SceneNode

/**
 * Maps a one-finger drag in screen pixels to yaw/pitch degrees.
 *
 * Task 2.1: pitch uses **inverted** [dy] so dragging the finger up increases pitch
 * (camera rises / looks from higher), matching common orbit-camera UX.
 */
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
        // Screen Y grows downward; invert so finger-up → positive pitch delta.
        pitchDegrees = ((-dy) * degreesPerPixel).coerceIn(-MaxOrbitDegreesPerStep, MaxOrbitDegreesPerStep),
    )
}

private const val DefaultOrbitDegreesPerPixel = 0.25f
private const val MaxOrbitDegreesPerStep = 32f
