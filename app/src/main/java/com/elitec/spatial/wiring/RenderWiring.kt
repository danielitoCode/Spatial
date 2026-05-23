package com.elitec.spatial.wiring

import com.elitec.spatial_renderer.adapter.ChoreographerFrameScheduler
import com.elitec.spatial_renderer.adapter.DefaultRenderBackend
import com.elitec.spatial_renderer.render.FrameScheduler
import com.elitec.spatial_renderer.render.RenderBackend

/** Composición concreta app-level para respetar inversión de dependencias. */
object RenderWiring {
    val renderBackend: RenderBackend = DefaultRenderBackend()
    val frameScheduler: FrameScheduler = ChoreographerFrameScheduler()
}