package com.elitec.spatial_compose.scene

import androidx.compose.runtime.Stable
import com.elitec.spatial_compose.modifier.Modifier3D
import com.elitec.spatial_compose.shapes.PrimitiveShape

@Stable
internal class SceneContentScope(
    private val sceneBuilder: SceneBuilder,
) {
    internal fun reset() {
        sceneBuilder.clear()
    }
    internal fun build(): List<SceneNode> = sceneBuilder.nodes.toList()
    internal fun add(shape: PrimitiveShape, modifier: Modifier3D = Modifier3D.Default) = sceneBuilder.add(shape, modifier)
}

