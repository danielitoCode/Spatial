package com.elitec.spatial_renderer.gl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class SpatialGlSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    init {
        setEGLContextClientVersion(3)
        setRenderer(SpatialGlRenderer())
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}