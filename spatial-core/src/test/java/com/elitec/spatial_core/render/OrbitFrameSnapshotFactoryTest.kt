package com.elitec.spatial_core.render

import com.elitec.spatial_core.camera.CameraSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Coverage gap closed during the 2.0/2.1/2.2/2.3 re-verification pass (2026-07-08): `OrbitCamera`
 * and `buildOrbitFrameSnapshot` are pure Kotlin/JVM and had zero test coverage even though nothing
 * about them requires a device. This mirrors `spatial-renderer`'s `OrbitCameraZoomTest`, but for the
 * spatial-core copy of the same zoom-clamping formula (see the "known duplication" note in
 * `OrbitFrameSnapshotFactory.kt`'s KDoc: the two copies are kept in sync by convention, not by the
 * compiler, so both need their own regression coverage).
 */
class OrbitFrameSnapshotFactoryTest {

    @Test
    fun buildOrbitFrameSnapshotPopulatesRealDataInsteadOfDefaults() {
        val snapshot = buildOrbitFrameSnapshot(
            frameTimeNanos = 123L,
            cameraSnapshot = CameraSnapshot(yaw = 45f, pitch = 10f, zoom = 1.5f),
            aspectRatio = 16f / 9f,
        )
        assertNotEquals(Mat4.identity(), snapshot.viewProjection)
        assertNotEquals(Vec3.ZERO, snapshot.cameraPosition)
        assertEquals(123L, snapshot.frameTimeNanos)
    }

    @Test
    fun defaultCameraSnapshotProducesEyeOnPositiveZAxis() {
        // yaw=0, pitch=0 -> renderer convention places the eye straight down +Z looking at origin.
        val eye = OrbitCamera.eyePosition(CameraSnapshot())
        assertEquals(0f, eye.x, 0.001f)
        assertEquals(0f, eye.y, 0.001f)
        assertEquals(OrbitCamera.DEFAULT_BASE_DISTANCE, eye.z, 0.001f)
    }

    @Test
    fun zoomIsClampedToCameraSnapshotBounds() {
        val overZoomed = OrbitCamera.orbitDistanceForVisualZoom(zoom = 999f)
        val clampedEquivalent = OrbitCamera.orbitDistanceForVisualZoom(zoom = CameraSnapshot.MAX_ZOOM)
        assertEquals(clampedEquivalent, overZoomed, 0.0001f)

        val underZoomed = OrbitCamera.orbitDistanceForVisualZoom(zoom = -5f)
        val minClampedEquivalent = OrbitCamera.orbitDistanceForVisualZoom(zoom = CameraSnapshot.MIN_ZOOM)
        assertEquals(minClampedEquivalent, underZoomed, 0.0001f)
    }

    @Test
    fun nonFiniteZoomFallsBackToOneInsteadOfDividingByZeroOrNaN() {
        val distance = OrbitCamera.orbitDistanceForVisualZoom(zoom = Float.NaN)
        assertEquals(OrbitCamera.DEFAULT_BASE_DISTANCE, distance, 0.0001f)

        val infiniteZoomDistance = OrbitCamera.orbitDistanceForVisualZoom(zoom = Float.POSITIVE_INFINITY)
        assertTrue(infiniteZoomDistance.isFinite())
    }

    @Test
    fun invalidAspectRatioFallsBackToSquareInsteadOfProducingNonFiniteMatrix() {
        val snapshot = buildOrbitFrameSnapshot(
            frameTimeNanos = 0L,
            cameraSnapshot = CameraSnapshot(),
            aspectRatio = 0f,
        )
        assertTrue(snapshot.viewProjection.toFloatArray().all { it.isFinite() })
    }
}
