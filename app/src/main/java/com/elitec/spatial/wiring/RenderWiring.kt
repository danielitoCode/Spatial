package com.elitec.spatial.wiring

import com.elitec.spatial_camera.SpatialCamera
import com.elitec.spatial_renderer.adapter.ChoreographerFrameScheduler
import com.elitec.spatial_renderer.adapter.DefaultRenderBackend
import com.elitec.spatial_renderer.render.FrameScheduler
import com.elitec.spatial_renderer.render.RenderBackend

/** Composición concreta app-level para respetar inversión de dependencias. */
object RenderWiring {
    private val camera = SpatialCamera()
    val runtime = SpatialRuntime(
        renderBackend = DefaultRenderBackend(),
        frameScheduler = ChoreographerFrameScheduler(),
        cameraRuntime = camera,
    )
    val gestureDispatcher = SpatialGestureDispatcher(camera)
    fun cameraSnapshot() = camera.snapshot()
}