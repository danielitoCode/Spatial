package com.elitec.spatial_renderer

import com.elitec.spatial_renderer.render.RenderFrame
import org.junit.Assert.assertFalse
import org.junit.Test

class RenderFrameLightingScopeTest {

    @Test
    fun `render frame does not transport active lights for Core 1`() {
        val renderFrameProperties = RenderFrame::class.java.declaredFields.map { it.name }

        assertFalse(
            "Core #1 render frames must not expose active light transport",
            renderFrameProperties.contains("lights"),
        )
        assertFalse(
            "Core #1 render frames must not expose active light state",
            renderFrameProperties.contains("lightState"),
        )
    }
}