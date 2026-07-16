package com.elitec.spatial_compose.scene

import android.view.View
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.scene.RenderableNode

public interface SceneRenderHost {
    val view: View
    fun updateScene(nodes: List<RenderableNode>)
    fun updateCamera(cameraSnapshot: CameraSnapshot)
    fun updateClearColor(color: com.elitec.spatial_core.render.Color4) {}
    fun requestFrame()
    fun dispose()

    /**
     * Called by the Compose host when the hosting Activity/Fragment resumes. Lets the host sync the
     * GL thread lifecycle and re-arm any "ready" gate after a background/foreground cycle. Default
     * no-op so test/fake implementations that don't model lifecycle still compile.
     *
     * Track 1 (Fix background-then-foreground bug, Core #1).
     */
    fun onResume() {}

    /**
     * Called by the Compose host when the hosting Activity/Fragment pauses. Default no-op.
     *
     * Track 1 (Fix background-then-foreground bug, Core #1).
     */
    fun onPause() {}
}

internal fun SceneRenderHost.renderSceneFrame(
    nodes: List<RenderableNode>,
    cameraSnapshot: CameraSnapshot,
    clearColor: com.elitec.spatial_core.render.Color4 = com.elitec.spatial_core.render.Color4.BLACK,
) {
    updateScene(nodes)
    updateCamera(cameraSnapshot)
    updateClearColor(clearColor)
    requestFrame()
}

internal class SceneRenderHostHolder {
    var host: SceneRenderHost? = null

    fun dispose() {
        host?.dispose()
        host = null
    }
}