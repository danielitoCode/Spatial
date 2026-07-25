package com.elitec.spatial_renderer.gl

import android.app.Activity
import android.os.Bundle

/**
 * Minimal host Activity for [CubeRendersOnFirstFrameTest].
 *
 * GLSurfaceView only creates an EGL surface when attached to a window; launching this
 * Activity from androidTest gives a real window without depending on :app.
 */
class FirstFrameTestActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
