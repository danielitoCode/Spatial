package com.elitec.spatial_light

import com.elitec.spatial_core.scene.LightData

/**
 * Helpers for creating light contracts without opting Core #1 into active lighting.
 *
 * Core #1 transports flat-color materials to the renderer. [LightData] remains a
 * pure scene contract so scene authors can model lighting intent ahead of a later
 * lighting milestone, but current Core #1 renderers are allowed to ignore it.
 */
object Light {
    /** Creates directional light metadata using the shared core contract. */
    fun directional(
        dirX: Float = 0f,
        dirY: Float = -1f,
        dirZ: Float = 0f,
        intensity: Float = 1f,
        r: Float = 1f,
        g: Float = 1f,
        b: Float = 1f,
    ): LightData = LightData(
        dirX = dirX,
        dirY = dirY,
        dirZ = dirZ,
        intensity = intensity,
        r = r,
        g = g,
        b = b,
    )
}