package com.elitec.spatial_compose

import com.elitec.spatial_camera.SpatialCamera
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.camera.CameraUpdateSource
import com.elitec.spatial_units.deg
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import junit.framework.TestCase.assertEquals
import org.junit.Test

class CameraContractParityTest {
    @Test
    fun orbitProducesSameSnapshotFromComposeAndRuntimeRoutes() {
        val composeCamera = composeCamera()
        val runtimeCamera = runtimeCamera()

        composeCamera.orbitBy(deltaYawDegrees = 7f, deltaPitchDegrees = -5f)
        runtimeCamera.applyDelta(com.elitec.spatial_camera.CameraDelta(deltaYaw = 7f, deltaPitch = -5f))

        assertEquals(runtimeCamera.snapshot(), composeCamera.snapshot())
    }

    @Test
    fun zoomProducesSameSnapshotFromComposeAndRuntimeRoutes() {
        val composeCamera = composeCamera()
        val runtimeCamera = runtimeCamera()

        composeCamera.zoomBy(scaleDelta = 1.08f)
        runtimeCamera.applyDelta(com.elitec.spatial_camera.CameraDelta(zoomScaleDelta = 1.08f))

        assertEquals(runtimeCamera.snapshot(), composeCamera.snapshot())
    }

    @Test
    fun jumpProducesSameSnapshotFromComposeAndRuntimeRoutes() {
        val composeCamera = composeCamera()
        val runtimeCamera = runtimeCamera()

        composeCamera.jumpTo(yaw = 33f.deg, pitch = 95f.deg, zoom = 9f, source = CameraUpdateSource.Remote)
        runtimeCamera.jumpTo(yaw = 33f, pitch = 95f, zoom = 9f, source = CameraUpdateSource.Remote)

        assertEquals(runtimeCamera.snapshot(), composeCamera.snapshot())
    }

    @Test
    fun animateProducesSameSnapshotFromComposeAndRuntimeRoutes() {
        val composeCamera = composeCamera()
        val runtimeCamera = runtimeCamera()

        runImmediateSuspend {
            composeCamera.animateTo(
                yaw = 120f.deg,
                pitch = 42f.deg,
                zoom = 2.5f,
                durationMillis = 0L,
            )
        }
        runtimeCamera.animateTo(
            yaw = 120f,
            pitch = 42f,
            zoom = 2.5f,
            motion = com.elitec.spatial_camera.MotionSpec.Instant,
        )

        assertEquals(runtimeCamera.snapshot(), composeCamera.snapshot())
    }

    private fun composeCamera(): CameraState = CameraState(
        yaw = INITIAL_SNAPSHOT.yaw.deg,
        pitch = INITIAL_SNAPSHOT.pitch.deg,
        zoom = INITIAL_SNAPSHOT.zoom,
    )

    private fun runtimeCamera(): SpatialCamera = SpatialCamera(INITIAL_SNAPSHOT)

    private fun runImmediateSuspend(block: suspend () -> Unit) {
        var completed = false
        var failure: Throwable? = null
        block.startCoroutine(
            Continuation(EmptyCoroutineContext) { result ->
                completed = true
                failure = result.exceptionOrNull()
            }
        )
        check(completed) { "Expected the zero-duration camera animation to complete without frame suspension." }
        failure?.let { throw it }
    }

    private companion object {
        val INITIAL_SNAPSHOT = CameraSnapshot(
            yaw = 10f,
            pitch = 15f,
            zoom = 1.25f,
        )
    }
}