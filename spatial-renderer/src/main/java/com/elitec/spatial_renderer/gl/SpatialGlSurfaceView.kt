package com.elitec.spatial_renderer.gl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.View
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.scene.RenderableNode
import com.elitec.spatial_renderer.render.RenderBackend
import com.elitec.spatial_renderer.render.RenderFrame

class SpatialGlSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    private val spatialRenderer = SpatialGlRenderer()

    init {
        setEGLContextClientVersion(3)
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
        queueEvent {
            spatialRenderer.updateNodes(frame.nodes)
            spatialRenderer.updateCamera(frame.cameraState)
            requestRender()
        }
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
}