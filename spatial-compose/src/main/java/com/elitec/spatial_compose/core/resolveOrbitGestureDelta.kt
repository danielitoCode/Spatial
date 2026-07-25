package com.elitec.spatial_compose.core

import androidx.compose.ui.unit.IntSize
import com.elitec.spatial_compose.scene.GestureSensitivity
import com.elitec.spatial_compose.scene.SceneNode

/**
 * Maps a one-finger drag in screen pixels to yaw/pitch degrees.
 *
 * Task 2.1 (device feedback — turntable UX):
 * - **Yaw** uses inverted [dx] so dragging right rotates the figure with the finger.
 * - **Pitch** uses raw [dy] so dragging down pitches with the finger (not against it).
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
        // Finger right (dx > 0) → negative yaw so the object appears to follow the drag.
        yawDegrees = ((-dx) * degreesPerPixel).coerceIn(-MaxOrbitDegreesPerStep, MaxOrbitDegreesPerStep),
        // Finger down (dy > 0) → positive pitch so the object follows the vertical drag.
        pitchDegrees = (dy * degreesPerPixel).coerceIn(-MaxOrbitDegreesPerStep, MaxOrbitDegreesPerStep),
    )
}

private const val DefaultOrbitDegreesPerPixel = 0.25f
private const val MaxOrbitDegreesPerStep = 32f
