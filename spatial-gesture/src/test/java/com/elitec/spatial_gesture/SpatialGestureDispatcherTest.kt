package com.elitec.spatial_gesture

import com.elitec.spatial_camera.CameraDelta
import com.elitec.spatial_camera.CameraRuntimeContract
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.camera.CameraUpdateSource
import com.elitec.spatial_camera.GestureMotionPolicy
import com.elitec.spatial_camera.MotionSpec
import org.junit.Assert.assertEquals
import org.junit.Test

class SpatialGestureDispatcherTest {
    @Test
    fun publishOrbit_normalizesExaggeratedDelta() {
        val cameraRuntime = RecordingCameraRuntime()
        val dispatcher = SpatialGestureDispatcher(cameraRuntime)

        dispatcher.publishOrbit(OrbitGestureDelta(deltaYaw = 500f, deltaPitch = -500f))

        val delta = cameraRuntime.lastDelta
        assertEquals(12f, delta.deltaYaw, FLOAT_TOLERANCE)
        assertEquals(-8f, delta.deltaPitch, FLOAT_TOLERANCE)
    }

    @Test
    fun publishPinch_sanitizesInvalidScale() {
        val cameraRuntime = RecordingCameraRuntime()
        val dispatcher = SpatialGestureDispatcher(cameraRuntime)

        dispatcher.publishPinch(PinchZoomDelta(scaleDelta = 0f))
        assertEquals(1f, cameraRuntime.lastDelta.zoomScaleDelta, FLOAT_TOLERANCE)

        dispatcher.publishPinch(PinchZoomDelta(scaleDelta = -1f))
        assertEquals(1f, cameraRuntime.lastDelta.zoomScaleDelta, FLOAT_TOLERANCE)
    }

    @Test
    fun publishPinch_normalizesZoomScaleDelta() {
        val cameraRuntime = RecordingCameraRuntime()
        val dispatcher = SpatialGestureDispatcher(cameraRuntime)

        dispatcher.publishPinch(PinchZoomDelta(scaleDelta = 3f))

        assertEquals(1.25f, cameraRuntime.lastDelta.zoomScaleDelta, FLOAT_TOLERANCE)
    }

    @Test
    fun rawPolicy_keepsAbruptDeltasUpToHardSafetyLimit() {
        val cameraRuntime = RecordingCameraRuntime()
        val dispatcher = SpatialGestureDispatcher(cameraRuntime, motionPolicy = GestureMotionPolicy.Raw)

        dispatcher.publishOrbit(OrbitGestureDelta(deltaYaw = 500f, deltaPitch = -500f))
        assertEquals(90f, cameraRuntime.lastDelta.deltaYaw, FLOAT_TOLERANCE)
        assertEquals(-45f, cameraRuntime.lastDelta.deltaPitch, FLOAT_TOLERANCE)

        dispatcher.publishPinch(PinchZoomDelta(scaleDelta = 3f))
        assertEquals(2f, cameraRuntime.lastDelta.zoomScaleDelta, FLOAT_TOLERANCE)
    }

    private class RecordingCameraRuntime : CameraRuntimeContract {
        lateinit var lastDelta: CameraDelta

        override fun syncSnapshot(snapshot: CameraSnapshot) = Unit

        override fun orbitTo(yaw: Float, pitch: Float, source: CameraUpdateSource) = Unit

        override fun applyDelta(delta: CameraDelta, source: CameraUpdateSource) {
            lastDelta = delta
        }

        override fun zoomTo(zoom: Float, source: CameraUpdateSource) = Unit

        override fun jumpTo(yaw: Float, pitch: Float, zoom: Float, source: CameraUpdateSource) = Unit

        override fun animateTo(yaw: Float, pitch: Float, zoom: Float, motion: MotionSpec) = Unit

        override fun snapshot(): CameraSnapshot = CameraSnapshot()
    }

    private companion object {
        const val FLOAT_TOLERANCE = 0.0001f
    }
}