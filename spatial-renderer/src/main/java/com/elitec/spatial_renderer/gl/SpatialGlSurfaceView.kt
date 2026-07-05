package com.elitec.spatial_renderer.gl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.render.Color4
import com.elitec.spatial_core.scene.RenderableNode
import com.elitec.spatial_renderer.BuildConfig
import com.elitec.spatial_renderer.render.RenderBackend
import com.elitec.spatial_renderer.render.RenderFrame

class SpatialGlSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    private val spatialRenderer = SpatialGlRenderer()

    init {
        setEGLContextClientVersion(3)
        spatialRenderer.onSurfaceReadyCallback = { post { requestRender() } }
        setRenderer(spatialRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun updateScene(nodes: List<RenderableNode>) {
        queueEvent {
            spatialRenderer.updateNodes(nodes)
            requestRender()
        }
    }

    fun updateCamera(cameraSnapshot: CameraSnapshot) {
        queueEvent {
            spatialRenderer.updateCamera(cameraSnapshot)
            requestRender()
        }
    }

    fun render(frame: RenderFrame) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "render(frame): enqueueing ${frame.nodes.size} nodes")
        }
        queueEvent {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "render(frame): GL queue received ${frame.nodes.size} nodes")
            }
            spatialRenderer.updateClearColor(frame.clearColor)
            spatialRenderer.updateNodes(frame.nodes)
            spatialRenderer.updateCamera(frame.cameraState)
            requestRender()
        }
    }

    fun releaseGlResources() {
        if (!isAttachedToWindow) return

        queueEvent {
            spatialRenderer.releaseGlResources()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        releaseGlResources()
        super.surfaceDestroyed(holder)
    }

    override fun onDetachedFromWindow() {
        releaseGlResources()
        super.onDetachedFromWindow()
    }

    fun setOnSurfaceReady(callback: () -> Unit) {
        spatialRenderer.onSurfaceReadyCallback = {
            post { requestRender() }
            callback()
        }
    }

    /** Notifies [callback] every time the viewport aspect ratio changes (see `onSurfaceChanged`). */
    fun setOnViewportChanged(callback: (aspectRatio: Float) -> Unit) {
        spatialRenderer.onViewportChangedCallback = callback
    }
}

class SpatialGlRenderTarget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : RenderBackend {
    private val surfaceView = SpatialGlSurfaceView(context, attrs)

    val view: View get() = surfaceView

    override fun render(frame: RenderFrame) {
        surfaceView.render(frame)
    }

    fun releaseGlResources() {
        surfaceView.releaseGlResources()
    }

    fun setOnSurfaceReady(callback: () -> Unit) {
        surfaceView.setOnSurfaceReady(callback)
    }

    fun setOnViewportChanged(callback: (aspectRatio: Float) -> Unit) {
        surfaceView.setOnViewportChanged(callback)
    }
}

private const val TAG = "SpatialGlSurfaceView"