package com.elitec.spatial.wiring

import com.elitec.spatial_camera.SpatialCamera
import com.elitec.spatial_gesture.SpatialGestureDispatcher
import com.elitec.spatial_renderer.adapter.ChoreographerFrameScheduler
import com.elitec.spatial_renderer.adapter.DefaultRenderBackend
import com.elitec.spatial_runtime.SpatialRuntime

/** Composición concreta app-level para respetar inversión de dependencias. */
object RenderWiring {
    private val camera = SpatialCamera()
    val gestureDispatcher = SpatialGestureDispatcher(camera)
    fun cameraSnapshot() = camera.snapshot()
}