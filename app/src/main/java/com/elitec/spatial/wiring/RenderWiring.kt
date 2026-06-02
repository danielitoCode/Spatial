package com.elitec.spatial.wiring


import com.elitec.spatial_camera.camera.SpatialCamera
import com.elitec.spatial_gesture.SpatialGestureDispatcher

/** Impl composition app-level for respect a dependency invert*/
object RenderWiring {
    private val camera = SpatialCamera()
    val gestureDispatcher = SpatialGestureDispatcher(camera)
    fun cameraSnapshot() = camera.snapshot()
}