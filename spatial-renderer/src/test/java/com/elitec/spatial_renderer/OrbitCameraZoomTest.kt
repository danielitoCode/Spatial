package com.elitec.spatial_renderer

import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_renderer.gl.orbitDistanceForVisualZoom
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OrbitCameraZoomTest {
    @Test
    fun visualZoomInReducesOrbitalDistance() {
        val defaultDistance = orbitDistanceForVisualZoom(zoom = 1f)
        val zoomedInDistance = orbitDistanceForVisualZoom(zoom = 2f)
        val zoomedOutDistance = orbitDistanceForVisualZoom(zoom = 0.5f)

        assertEquals(10f, defaultDistance, 0.0001f)
        assertEquals(5f, zoomedInDistance, 0.0001f)
        assertEquals(20f, zoomedOutDistance, 0.0001f)
        assertTrue(zoomedInDistance < defaultDistance)
        assertTrue(zoomedOutDistance > defaultDistance)
    }

    @Test
    fun visualZoomDistanceRespectsSnapshotZoomBounds() {
        assertEquals(10f / CameraSnapshot.MAX_ZOOM, orbitDistanceForVisualZoom(zoom = 100f), 0.0001f)
        assertEquals(10f / CameraSnapshot.MIN_ZOOM, orbitDistanceForVisualZoom(zoom = 0.01f), 0.0001f)
    }
}