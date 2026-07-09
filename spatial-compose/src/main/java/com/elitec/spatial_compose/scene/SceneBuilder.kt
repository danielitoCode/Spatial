package com.elitec.spatial_compose.scene

import androidx.compose.runtime.mutableStateListOf

internal class SceneBuilder {
    private val internalNodes = mutableStateListOf<SceneNode>()
    val nodes: List<SceneNode> get() = internalNodes

    internal fun add(node: SceneNode) {
        if (node !in internalNodes) {
            internalNodes.add(node)
        }
    }

    internal fun remove(node: SceneNode) {
        internalNodes.remove(node)
    }
}