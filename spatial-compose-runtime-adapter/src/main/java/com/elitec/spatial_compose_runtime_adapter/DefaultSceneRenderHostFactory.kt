package com.elitec.spatial_compose_runtime_adapter

import android.content.Context
import android.util.Log
import android.view.View
import com.elitec.spatial_camera.camera.SpatialCamera
import com.elitec.spatial_compose.scene.SceneRenderHost
import com.elitec.spatial_compose.scene.SceneRenderHostFactory
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.scene.RenderableNode
import com.elitec.spatial_renderer.adapter.ChoreographerFrameScheduler
import com.elitec.spatial_renderer.gl.SpatialGlRenderTarget
import com.elitec.spatial_runtime.SpatialRuntime

public object DefaultSceneRenderHostFactory : SceneRenderHostFactory {
    override fun create(context: Context): SceneRenderHost = SpatialRuntimeSceneRenderHost(context)
}

public class SpatialRuntimeSceneRenderHost(context: Context) : SceneRenderHost {
    private val renderTarget = SpatialGlRenderTarget(context)
    private val runtimeCamera = SpatialCamera()
    private val runtime = SpatialRuntime(
        renderBackend = renderTarget,
        frameScheduler = ChoreographerFrameScheduler(),
        cameraRuntime = runtimeCamera,
    )
    private var pendingNodes: List<RenderableNode> = emptyList()
    private var pendingCameraSnapshot: CameraSnapshot = runtimeCamera.snapshot()

    override val view: View get() = renderTarget.view

    init {
        runtime.onInitialize()
    }

    override fun updateScene(nodes: List<RenderableNode>) {
        pendingNodes = nodes
    }

    override fun updateCamera(cameraSnapshot: CameraSnapshot) {
        pendingCameraSnapshot = cameraSnapshot
    }

    override fun requestFrame() {
        if (BuildConfig.DEBUG) {
            Log.d(
                TAG,
                "requestFrame: pendingNodes.size=${pendingNodes.size}, cameraSnapshot=$pendingCameraSnapshot",
            )
        }
        runtime.requestFrame(
            nodes = pendingNodes,
            cameraSnapshot = pendingCameraSnapshot,
        )
    }

    override fun dispose() {
        runtime.onShutdown()
        renderTarget.releaseGlResources()
    }
}

private const val TAG = "SpatialRuntimeHost"