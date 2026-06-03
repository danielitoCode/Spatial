package com.elitec.spatial_compose.scene

import android.view.View
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.scene.RenderableNode

public interface SceneRenderHost {
    val view: View
    fun updateScene(nodes: List<RenderableNode>)
    fun updateCamera(cameraSnapshot: CameraSnapshot)
    fun requestFrame()
    fun dispose()
}

internal fun SceneRenderHost.renderSceneFrame(
    nodes: List<RenderableNode>,
    cameraSnapshot: CameraSnapshot,
) {
    updateScene(nodes)
    updateCamera(cameraSnapshot)
    requestFrame()
}

internal class SceneRenderHostHolder {
    var host: SceneRenderHost? = null

    fun dispose() {
        host?.dispose()
        host = null
    }
}