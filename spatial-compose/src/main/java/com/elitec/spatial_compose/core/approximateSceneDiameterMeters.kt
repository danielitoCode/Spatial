package com.elitec.spatial_compose.core

import com.elitec.spatial_compose.scene.SceneNode
import com.elitec.spatial_units.meters
import kotlin.math.abs
import kotlin.math.max

internal fun approximateSceneDiameterMeters(sceneNodes: List<SceneNode>): Float {
    if (sceneNodes.isEmpty()) return ReferenceSceneDiameterMeters

    var maxExtent = 0f
    sceneNodes.forEach { node ->
        val size = node.modifier.size ?: node.modifier.scale
        val halfX = abs(size.x.meters).coerceAtLeast(MinNodeDimensionMeters) / 2f
        val halfY = abs(size.y.meters).coerceAtLeast(MinNodeDimensionMeters) / 2f
        val halfZ = abs(size.z.meters).coerceAtLeast(MinNodeDimensionMeters) / 2f
        maxExtent = max(maxExtent, abs(node.modifier.position.x.meters) + halfX)
        maxExtent = max(maxExtent, abs(node.modifier.position.y.meters) + halfY)
        maxExtent = max(maxExtent, abs(node.modifier.position.z.meters) + halfZ)
    }
    return (maxExtent * 2f).coerceAtLeast(MinSceneDiameterMeters)
}

private const val ReferenceSceneDiameterMeters = 2f

private const val MinSceneDiameterMeters = 0.05f
private const val MinNodeDimensionMeters = 0.01f