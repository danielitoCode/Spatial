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

    // Audit note (Core #1 Stability, item 1.2 follow-up): `glReady` and `queuedFrame` used to be
    // read/written as two separate, non-atomic steps (a @Volatile flag plus a nullable field). That
    // allowed a genuine lost-frame race: the GL thread could flip `glReady` to true and drain a null
    // `queuedFrame` *between* this thread's `if (!glReady)` check and its `queuedFrame = { ... }`
    // assignment, so the just-queued frame would never be replayed. All access to both fields is now
    // funneled through `readyLock` so the check-then-act sequence is atomic on both sides.
    private val readyLock = Any()
    private var glReady = false
    private var queuedFrame: (() -> Unit)? = null

    override val view: View get() = renderTarget.view

    init {
        runtime.onInitialize()
        renderTarget.setOnSurfaceReady {
            val frameToReplay = synchronized(readyLock) {
                glReady = true
                queuedFrame.also { queuedFrame = null }
            }
            frameToReplay?.invoke()
        }
        renderTarget.setOnViewportChanged { aspectRatio ->
            runtime.updateViewport(aspectRatio)
        }
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
        val shouldRunNow = synchronized(readyLock) {
            if (glReady) {
                true
            } else {
                queuedFrame = { requestFrameInternal() }
                false
            }
        }
        if (shouldRunNow) {
            requestFrameInternal()
        }
    }

    private fun requestFrameInternal() {
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