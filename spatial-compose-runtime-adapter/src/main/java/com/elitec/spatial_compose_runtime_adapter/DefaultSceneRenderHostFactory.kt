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
    private var pendingClearColor: com.elitec.spatial_core.render.Color4 = com.elitec.spatial_core.render.Color4.BLACK

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

    override fun updateClearColor(color: com.elitec.spatial_core.render.Color4) {
        pendingClearColor = color
    }

    override fun requestFrame() {
        if (BuildConfig.DEBUG) {
            Log.d(
                TAG,
                "requestFrame: pendingNodes.size=${pendingNodes.size}, cameraSnapshot=$pendingCameraSnapshot",
            )
        }
        // Capture a snapshot of the current pending data NOW, before any recomposition can overwrite
        // pendingNodes/pendingCameraSnapshot between this call and onSurfaceReady's replay.
        // Previously the closure read pendingNodes lazily at execution time: if onSurfaceCreated
        // triggered a Compose recomposition that produced an empty node list before the queued frame
        // fired, the replay would render 0 nodes even though 17 were pending at enqueue time.
        val capturedNodes = pendingNodes
        val capturedCamera = pendingCameraSnapshot
        val capturedClearColor = pendingClearColor
        val shouldRunNow = synchronized(readyLock) {
            if (glReady) {
                true
            } else {
                queuedFrame = {
                    runtime.requestFrame(
                        nodes = capturedNodes,
                        cameraSnapshot = capturedCamera,
                        clearColor = capturedClearColor,
                    )
                }
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
            clearColor = pendingClearColor,
        )
    }

    override fun dispose() {
        runtime.onShutdown()
        renderTarget.releaseGlResources()
    }
}

private const val TAG = "SpatialRuntimeHost"