package com.elitec.spatial_compose.scene

import com.elitec.spatial_compose.modifier.Modifier3D
import com.elitec.spatial_compose.shapes.PrimitiveShape

internal class SceneBuilder {
    private val internalNodes = mutableListOf<SceneNode>()
    val nodes: List<SceneNode> get() = internalNodes

    internal fun add(shape: PrimitiveShape, modifier: Modifier3D) {
        internalNodes += SceneNode(shape, modifier)
    }

    internal fun clear() {
        internalNodes.clear()
    }
}