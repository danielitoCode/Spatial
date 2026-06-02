package com.elitec.spatial_compose.core

import androidx.compose.ui.unit.IntSize
import com.elitec.spatial_compose.scene.SceneNode
import kotlin.math.max
import kotlin.math.sqrt

internal fun adaptiveOrbitDegreesPerPixel(
    cameraZoom: Float,
    sceneNodes: List<SceneNode>,
    viewportSize: IntSize = IntSize.Zero,
): Float {
    val safeZoom = cameraZoom.takeIf { it.isFinite() && it > 0f } ?: 1f
    val sceneDiameter = approximateSceneDiameterMeters(sceneNodes)
    val sceneFactor = sqrt(sceneDiameter / ReferenceSceneDiameterMeters)
        .coerceIn(MinAdaptiveSceneFactor, MaxAdaptiveSceneFactor)
    val viewportFactor = viewportSize.maxDimension
        .takeIf { it > 0 }
        ?.let { sqrt(ReferenceViewportPixels / it.toFloat()).coerceIn(MinViewportFactor, MaxViewportFactor) }
        ?: 1f

    return (DefaultOrbitDegreesPerPixel * sceneFactor * viewportFactor / safeZoom)
        .coerceIn(MinAdaptiveDegreesPerPixel, DefaultOrbitDegreesPerPixel)

}

private val IntSize.maxDimension: Int get() = max(width, height)

private const val DefaultOrbitDegreesPerPixel = 0.25f
private const val ReferenceSceneDiameterMeters = 2f
private const val MinAdaptiveSceneFactor = 0.2f
private const val MaxAdaptiveSceneFactor = 1.5f
private const val ReferenceViewportPixels = 1080f
private const val MinViewportFactor = 0.75f
private const val MaxViewportFactor = 1.25f
private const val MinAdaptiveDegreesPerPixel = 0.015f