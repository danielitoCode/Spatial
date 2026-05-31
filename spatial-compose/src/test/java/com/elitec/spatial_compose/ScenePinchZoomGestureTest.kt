package com.elitec.spatial_compose

import com.elitec.spatial_units.deg
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test

class ScenePinchZoomGestureTest {
    @Test
    fun pinchOutAndInProduceStableScaleDeltas() {
        val inputState = SceneGestureInputState()
        val start = listOf(PointerPosition(0f, 0f), PointerPosition(100f, 0f))

        inputState.onPointerDown(start, zoomEnabled = true)

        val pinchOut = inputState.onMove(
            pointers = listOf(PointerPosition(0f, 0f), PointerPosition(110f, 0f)),
            orbitEnabled = true,
            zoomEnabled = true,
        )
        val pinchIn = inputState.onMove(
            pointers = listOf(PointerPosition(0f, 0f), PointerPosition(99f, 0f)),
            orbitEnabled = true,
            zoomEnabled = true,
        )

        assertEquals(1.08f, pinchOut.scaleDelta!!, 0.0001f)
        assertEquals(0.92f, pinchIn.scaleDelta!!, 0.0001f)
        assertNull(pinchOut.orbitDeltaPixels)
        assertNull(pinchIn.orbitDeltaPixels)
    }

    @Test
    fun oneTwoOneFingerTransitionResetsOrbitAnchor() {
        val inputState = SceneGestureInputState()
        inputState.onDown(10f, 10f)

        val firstDrag = inputState.onMove(
            pointers = listOf(PointerPosition(15f, 12f)),
            orbitEnabled = true,
            zoomEnabled = true,
        )
        assertEquals(5f, firstDrag.orbitDeltaPixels!!.dx, 0.0001f)
        assertEquals(2f, firstDrag.orbitDeltaPixels!!.dy, 0.0001f)

        inputState.onPointerDown(
            pointers = listOf(PointerPosition(15f, 12f), PointerPosition(40f, 12f)),
            zoomEnabled = true,
        )
        inputState.onMove(
            pointers = listOf(PointerPosition(15f, 12f), PointerPosition(50f, 12f)),
            orbitEnabled = true,
            zoomEnabled = true,
        )
        inputState.onPointerUp(
            pointers = listOf(PointerPosition(80f, 80f), PointerPosition(50f, 12f)),
            actionIndex = 1,
            zoomEnabled = true,
        )

        val resumedOrbit = inputState.onMove(
            pointers = listOf(PointerPosition(82f, 83f)),
            orbitEnabled = true,
            zoomEnabled = true,
        )

        assertEquals(2f, resumedOrbit.orbitDeltaPixels!!.dx, 0.0001f)
        assertEquals(3f, resumedOrbit.orbitDeltaPixels!!.dy, 0.0001f)
    }

    @Test
    fun cameraZoomByRespectsMinAndMaxZoomLimits() {
        val cameraState = CameraState(yaw = 0f.deg, pitch = 0f.deg, zoom = 1f)

        repeat(50) { cameraState.zoomBy(0.5f) }
        assertEquals(0.3f, cameraState.zoom, 0.0001f)

        repeat(50) { cameraState.zoomBy(2f) }
        assertEquals(4f, cameraState.zoom, 0.0001f)
    }

    @Test
    fun invalidOrExtremePinchDistancesReturnSafeScaleDelta() {
        assertEquals(1f, resolvePinchZoomScaleDelta(currentDistance = 0f, previousDistance = 100f), 0.0001f)
        assertEquals(1f, resolvePinchZoomScaleDelta(currentDistance = 100f, previousDistance = Float.NaN), 0.0001f)
        assertTrue(resolvePinchZoomScaleDelta(currentDistance = 1_000f, previousDistance = 10f) <= 1.08f)
        assertTrue(resolvePinchZoomScaleDelta(currentDistance = 10f, previousDistance = 1_000f) >= 0.92f)
    }
}