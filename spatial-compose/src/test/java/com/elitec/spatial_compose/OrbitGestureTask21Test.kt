package com.elitec.spatial_compose

import androidx.compose.ui.unit.IntSize
import com.elitec.spatial_compose.core.PointerPosition
import com.elitec.spatial_compose.core.resolveOrbitGestureDelta
import com.elitec.spatial_compose.scene.GestureSensitivity
import com.elitec.spatial_compose.scene.SceneGestureInputState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Task 2.1 — orbit gesture: pixel deltas, turntable signs, single-pointer tracking.
 */
class OrbitGestureTask21Test {

    @Test
    fun `single pointer move yields orbit pixel delta`() {
        val state = SceneGestureInputState()
        state.onDown(10f, 20f)
        val first = state.onMove(
            pointers = listOf(PointerPosition(10f, 20f)),
            orbitEnabled = true,
            zoomEnabled = false,
        )
        val second = state.onMove(
            pointers = listOf(PointerPosition(30f, 40f)),
            orbitEnabled = true,
            zoomEnabled = false,
        )
        assertNotNull(second.orbitDeltaPixels)
        assertEquals(20f, second.orbitDeltaPixels!!.dx, 0.01f)
        assertEquals(20f, second.orbitDeltaPixels!!.dy, 0.01f)
        assertNull(second.scaleDelta)
        assertNull(first.scaleDelta)
    }

    @Test
    fun `orbit disabled yields no orbit delta`() {
        val state = SceneGestureInputState()
        state.onDown(0f, 0f)
        val move = state.onMove(
            pointers = listOf(PointerPosition(15f, 0f)),
            orbitEnabled = false,
            zoomEnabled = false,
        )
        assertNull(move.orbitDeltaPixels)
    }

    @Test
    fun `resolveOrbitGestureDelta turntable signs for finger right and finger up`() {
        val delta = resolveOrbitGestureDelta(
            dx = 10f, // finger right
            dy = -20f, // finger up
            cameraZoom = 1f,
            sceneNodes = emptyList(),
            viewportSize = IntSize(400, 400),
            sensitivity = GestureSensitivity.Fixed(0.25f),
        )
        // -dx * 0.25 = -2.5 → figure follows drag to the right
        assertEquals(-2.5f, delta.yawDegrees, 0.01f)
        // dy * 0.25 = -5 → finger up pitches the figure upward with the drag
        assertEquals(-5f, delta.pitchDegrees, 0.01f)
    }

    @Test
    fun `resolveOrbitGestureDelta clamps extreme steps`() {
        val delta = resolveOrbitGestureDelta(
            dx = 10_000f,
            dy = 10_000f,
            cameraZoom = 1f,
            sceneNodes = emptyList(),
            sensitivity = GestureSensitivity.Fixed(1f),
        )
        assertTrue(delta.yawDegrees >= -32f)
        assertTrue(delta.pitchDegrees >= -32f)
        assertTrue(delta.yawDegrees <= 32f)
        assertTrue(delta.pitchDegrees <= 32f)
    }
}
