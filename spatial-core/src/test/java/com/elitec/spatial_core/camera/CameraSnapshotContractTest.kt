package com.elitec.spatial_core.camera

import org.junit.Assert.assertEquals
import org.junit.Test

class CameraSnapshotContractTest {

    @Test
    fun `defaults describe canonical gesture camera state`() {
        val snapshot = CameraSnapshot()

        assertEquals(0f, snapshot.yaw, FLOAT_TOLERANCE)
        assertEquals(0f, snapshot.pitch, FLOAT_TOLERANCE)
        assertEquals(1f, snapshot.zoom, FLOAT_TOLERANCE)
        assertEquals(0L, snapshot.version)
        assertEquals(CameraUpdateSource.Gesture, snapshot.source)
    }

    @Test
    fun `camera limits are published from core contract`() {
        assertEquals(-89f, CameraSnapshot.MIN_PITCH_DEGREES, FLOAT_TOLERANCE)
        assertEquals(89f, CameraSnapshot.MAX_PITCH_DEGREES, FLOAT_TOLERANCE)
        assertEquals(0.3f, CameraSnapshot.MIN_ZOOM, FLOAT_TOLERANCE)
        assertEquals(4f, CameraSnapshot.MAX_ZOOM, FLOAT_TOLERANCE)
    }

    @Test
    fun `version and source travel with copied snapshots`() {
        val snapshot = CameraSnapshot(
            yaw = 45f,
            pitch = 30f,
            zoom = 2f,
            version = 7L,
            source = CameraUpdateSource.Remote,
        )
        val next = snapshot.copy(version = snapshot.version + 1, source = CameraUpdateSource.Animation)

        assertEquals(45f, next.yaw, FLOAT_TOLERANCE)
        assertEquals(30f, next.pitch, FLOAT_TOLERANCE)
        assertEquals(2f, next.zoom, FLOAT_TOLERANCE)
        assertEquals(8L, next.version)
        assertEquals(CameraUpdateSource.Animation, next.source)
    }

    private companion object {
        const val FLOAT_TOLERANCE = 0.0001f
    }
}