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
    private var pendingClearColor: com.elitec.spatial_core.render.Color4 =
        com.elitec.spatial_core.render.Color4(0f, 0f, 0f, 0f)

    // Audit note (Core #1 Stability, item 1.2): `glReady` and `queuedFrame` are funneled through
    // `readyLock` so check-then-act is atomic between UI and GL ready callback threads.
    private val readyLock = Any()
    private var glReady = false
    private var queuedFrame: (() -> Unit)? = null

    override val view: View get() = renderTarget.view

    init {
        runtime.onInitialize()
        renderTarget.setOnSurfaceReady {
            // Device-closure task 1.2 / 2.3:
            // 1) Replay the last queued requestFrame if any.
            // 2) If nothing was queued but Compose already pushed nodes into pending*, still paint.
            val frameToReplay: (() -> Unit)?
            val fallbackPending: Boolean
            synchronized(readyLock) {
                glReady = true
                frameToReplay = queuedFrame.also { queuedFrame = null }
                fallbackPending = frameToReplay == null && pendingNodes.isNotEmpty()
            }
            when {
                frameToReplay != null -> frameToReplay.invoke()
                fallbackPending -> requestFrameInternal()
            }
        }
        renderTarget.setOnViewportChanged { aspectRatio ->
            runtime.updateViewport(aspectRatio)
        }
    }

    /**
     * Called by the Compose host when the hosting Activity resumes.
     *
     * Task 2.3: re-arm glReady and **pre-queue** the last known scene so the surface-ready
     * callback always has a frame to replay even if Compose does not recompose on resume.
     */
    override fun onResume() {
        synchronized(readyLock) {
            glReady = false
            if (pendingNodes.isNotEmpty()) {
                val nodes = pendingNodes
                val camera = pendingCameraSnapshot
                val clear = pendingClearColor
                queuedFrame = {
                    runtime.requestFrame(
                        nodes = nodes,
                        cameraSnapshot = camera,
                        clearColor = clear,
                    )
                }
            }
        }
        renderTarget.onResume()
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onResume: glReady re-armed, pending scene queued for surface-ready replay")
        }
    }

    /** Called by the Compose host when the hosting Activity pauses. */
    override fun onPause() {
        renderTarget.onPause()
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onPause: GL thread pause dispatched")
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
        // Snapshot pending data at enqueue time so surface-ready replay does not observe a later
        // empty list from an intermediate recomposition.
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
