package com.elitec.spatial_compose.scene

import android.view.View
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.scene.RenderableNode

internal interface SceneRenderHost {
    val view: View
    fun updateScene(nodes: List<RenderableNode>)
    fun updateCamera(cameraSnapshot: CameraSnapshot)
    fun requestFrame()
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
}