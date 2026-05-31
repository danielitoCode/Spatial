package com.elitec.spatial_compose

import android.view.View
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.scene.MaterialData
import com.elitec.spatial_core.scene.RenderableNode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class SceneRenderHostTest {

    @Test
    fun `scene frame dispatch sends nodes camera then frame request`() {
        val node = RenderableNode(
            meshId = "Cube",
            material = MaterialData(r = 1f, g = 0f, b = 0f),
        )
        val nodes = listOf(node)
        val camera = CameraSnapshot(yaw = 30f, pitch = -15f, zoom = 1.5f)
        val host = RecordingSceneRenderHost()

        host.renderSceneFrame(nodes, camera)

        assertEquals(listOf("nodes", "camera", "requestFrame"), host.events)
        assertSame(nodes, host.nodes)
        assertEquals(camera, host.cameraSnapshot)
        assertEquals(1, host.requestFrameCount)
    }

    private class RecordingSceneRenderHost : SceneRenderHost {
        val events = mutableListOf<String>()
        var nodes: List<RenderableNode>? = null
        var cameraSnapshot: CameraSnapshot? = null
        var requestFrameCount = 0

        override val view: View
            get() = error("The pure host-order test does not need an Android View instance.")

        override fun updateScene(nodes: List<RenderableNode>) {
            events += "nodes"
            this.nodes = nodes
        }

        override fun updateCamera(cameraSnapshot: CameraSnapshot) {
            events += "camera"
            this.cameraSnapshot = cameraSnapshot
        }

        override fun requestFrame() {
            events += "requestFrame"
            requestFrameCount += 1
        }
    }
}