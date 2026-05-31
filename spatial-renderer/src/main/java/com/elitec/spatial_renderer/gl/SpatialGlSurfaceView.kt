package com.elitec.spatial_renderer.gl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.elitec.spatial_core.camera.CameraSnapshot

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

    fun updateScene(nodes: List<com.elitec.spatial_core.scene.RenderableNode>) {
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
}