package com.elitec.spatial_compose

import androidx.compose.ui.unit.IntSize
import com.elitec.spatial_compose.core.resolveOrbitGestureDelta
import com.elitec.spatial_compose.modifier.Modifier3D
import com.elitec.spatial_compose.scene.GestureSensitivity
import com.elitec.spatial_compose.scene.SceneNode
import com.elitec.spatial_compose.shapes.PrimitiveShape
import com.elitec.spatial_units.meters
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlin.math.abs
import org.junit.Test

class SceneGestureSensitivityTest {
    @Test
    fun adaptiveSensitivity_decreasesAngularDeltaWhenZoomIncreases() {
        val nodes = listOf(SceneNode.Primitive(PrimitiveShape.Cube, Modifier3D.Default.size(2f.meters)))

        val normalZoomDelta = resolveOrbitGestureDelta(
            dx = 120f,
            dy = 0f,
            cameraZoom = 1f,
            sceneNodes = nodes,
            viewportSize = IntSize(1080, 1080),
        )
        val zoomedInDelta = resolveOrbitGestureDelta(
            dx = 120f,
            dy = 0f,
            cameraZoom = 3f,
            sceneNodes = nodes,
            viewportSize = IntSize(1080, 1080),
        )

        // Yaw sign is inverted for turntable UX; compare magnitudes so damping still holds.
        assertTrue(
            "Expected zooming in to reduce orbit yaw |delta|, but |${zoomedInDelta.yawDegrees}| was not less than |${normalZoomDelta.yawDegrees}|",
            abs(zoomedInDelta.yawDegrees) < abs(normalZoomDelta.yawDegrees),
        )
    }

    @Test
    fun adaptiveSensitivity_keepsSmallScenesBelowReasonableStepThreshold() {
        val tinyScene = listOf(
            SceneNode.Primitive(
                PrimitiveShape.Cube,
                Modifier3D.Default.size(0.1f.meters),
            ),
        )

        val delta = resolveOrbitGestureDelta(
            dx = 1_000f,
            dy = 1_000f,
            cameraZoom = 1f,
            sceneNodes = tinyScene,
            viewportSize = IntSize(1080, 1080),
        )

        assertTrue("Yaw delta should be capped for small scenes", abs(delta.yawDegrees) <= 32f)
        assertTrue("Pitch delta should be capped for small scenes", abs(delta.pitchDegrees) <= 32f)
        assertTrue("Tiny scene sensitivity should dampen large drags", abs(delta.yawDegrees) < 80f)
    }

    @Test
    fun fixedSensitivity_preservesExplicitLegacyDegreesPerPixelUntilStepCap() {
        val tinyScene = listOf(
            SceneNode.Primitive(
                PrimitiveShape.Cube,
                Modifier3D.Default.size(0.1f.meters),
            ),
        )

        val fixedDelta = resolveOrbitGestureDelta(
            dx = 40f,
            dy = -20f,
            cameraZoom = 4f,
            sceneNodes = tinyScene,
            viewportSize = IntSize(1080, 1080),
            sensitivity = GestureSensitivity.Fixed(0.25f),
        )
        val adaptiveDelta = resolveOrbitGestureDelta(
            dx = 40f,
            dy = -20f,
            cameraZoom = 4f,
            sceneNodes = tinyScene,
            viewportSize = IntSize(1080, 1080),
            sensitivity = GestureSensitivity.Adaptive,
        )

        // Turntable convention: -dx * 0.25 = -10; finger-up (-dy) → +5 pitch.
        assertEquals(-10f, fixedDelta.yawDegrees, 0.0001f)
        assertEquals(5f, fixedDelta.pitchDegrees, 0.0001f)
        assertTrue(
            "Fixed sensitivity should stay sharper than adaptive for this tiny zoomed scene",
            abs(fixedDelta.yawDegrees) > abs(adaptiveDelta.yawDegrees),
        )
    }
}
