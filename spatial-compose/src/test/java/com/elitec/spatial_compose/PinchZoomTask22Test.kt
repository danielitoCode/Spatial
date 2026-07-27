package com.elitec.spatial_compose

import com.elitec.spatial_compose.core.PointerPosition
import com.elitec.spatial_compose.core.resolvePinchZoomScaleDelta
import com.elitec.spatial_compose.scene.SceneGestureInputState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Task 2.2 — pinch zoom: scale delta math and two-finger gesture state.
 */
class PinchZoomTask22Test {

    @Test
    fun `pinch out yields scale greater than one`() {
        val scale = resolvePinchZoomScaleDelta(
            currentDistance = 200f,
            previousDistance = 100f,
        )
        assertTrue(scale > 1f)
        assertEquals(1.08f, scale, 0.0001f) // clamped to MaxPinchScaleDeltaPerEvent
    }

    @Test
    fun `pinch in yields scale less than one`() {
        val scale = resolvePinchZoomScaleDelta(
            currentDistance = 100f,
            previousDistance = 200f,
        )
        assertTrue(scale < 1f)
        assertEquals(0.92f, scale, 0.0001f) // clamped to MinPinchScaleDeltaPerEvent
    }

    @Test
    fun `invalid distances yield identity scale`() {
        assertEquals(1f, resolvePinchZoomScaleDelta(0f, 100f), 0.0001f)
        assertEquals(1f, resolvePinchZoomScaleDelta(100f, 0f), 0.0001f)
        assertEquals(1f, resolvePinchZoomScaleDelta(Float.NaN, 100f), 0.0001f)
    }

    @Test
    fun `two finger move produces scale delta after pointer down baseline`() {
        val state = SceneGestureInputState()
        state.onDown(0f, 0f)
        state.onPointerDown(
            pointers = listOf(
                PointerPosition(0f, 0f),
                PointerPosition(100f, 0f),
            ),
            zoomEnabled = true,
        )
        val move = state.onMove(
            pointers = listOf(
                PointerPosition(0f, 0f),
                PointerPosition(150f, 0f),
            ),
            orbitEnabled = true,
            zoomEnabled = true,
        )
        assertNotNull(move.scaleDelta)
        assertTrue(move.scaleDelta!! > 1f)
        assertNull(move.orbitDeltaPixels)
    }

    @Test
    fun `zoom disabled yields no scale delta`() {
        val state = SceneGestureInputState()
        state.onPointerDown(
            pointers = listOf(
                PointerPosition(0f, 0f),
                PointerPosition(100f, 0f),
            ),
            zoomEnabled = false,
        )
        val move = state.onMove(
            pointers = listOf(
                PointerPosition(0f, 0f),
                PointerPosition(150f, 0f),
            ),
            orbitEnabled = true,
            zoomEnabled = false,
        )
        assertNull(move.scaleDelta)
    }
}
