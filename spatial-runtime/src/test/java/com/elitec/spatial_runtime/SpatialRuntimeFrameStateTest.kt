package com.elitec.spatial_runtime

import com.elitec.spatial_camera.SpatialCamera
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.scene.RenderableNode
import com.elitec.spatial_renderer.render.FrameScheduler
import com.elitec.spatial_renderer.render.RenderBackend
import com.elitec.spatial_renderer.render.RenderFrame
import org.junit.Assert.assertEquals
import org.junit.Test

class SpatialRuntimeFrameStateTest {

    @Test
    fun `render frame forwards nodes and camera snapshot to backend`() {
        val backend = RecordingRenderBackend()
        val runtime = SpatialRuntime(
            renderBackend = backend,
            frameScheduler = object : FrameScheduler {
                override fun requestFrame(onFrame: (RenderFrame) -> Unit) {
                    onFrame(RenderFrame(frameTimeNanos = 0L))
                }
            },
            cameraRuntime = SpatialCamera(),
        )
        val nodes = listOf(RenderableNode(meshId = "Sphere"))
        val camera = CameraSnapshot(yaw = 42f, pitch = -12f, zoom = 2f)

        runtime.onInitialize()
        runtime.renderFrame(
            frameTimeNanos = 123L,
            nodes = nodes,
            cameraSnapshot = camera,
        )

        val frame = backend.frames.single()
        assertEquals(123L, frame.frameTimeNanos)
        assertEquals(nodes, frame.nodes)
        assertEquals(camera.yaw, frame.cameraState.yaw)
        assertEquals(camera.pitch, frame.cameraState.pitch)
        assertEquals(camera.zoom, frame.cameraState.zoom)
    }

    private class RecordingRenderBackend : RenderBackend {
        val frames = mutableListOf<RenderFrame>()

        override fun render(frame: RenderFrame) {
            frames += frame
        }
    }
}