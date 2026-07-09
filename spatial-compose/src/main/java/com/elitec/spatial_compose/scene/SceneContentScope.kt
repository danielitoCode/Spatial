package com.elitec.spatial_compose.scene

import androidx.compose.runtime.Stable

@Stable
internal class SceneContentScope(
    private val sceneBuilder: SceneBuilder,
) {
    internal fun add(node: SceneNode) {
        sceneBuilder.add(node)
    }

    internal fun remove(node: SceneNode) {
        sceneBuilder.remove(node)
    }
}

