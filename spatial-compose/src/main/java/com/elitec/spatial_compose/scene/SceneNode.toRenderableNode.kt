package com.elitec.spatial_compose.scene

import com.elitec.spatial_compose.modifier.toModelMatrix
import com.elitec.spatial_compose.shapes.defaultMaterial
import com.elitec.spatial_core.scene.MaterialData
import com.elitec.spatial_core.scene.RenderableNode

internal fun SceneNode.toRenderableNode(): RenderableNode = when (this) {
    is SceneNode.Primitive -> RenderableNode(
        meshId = shape.name,
        modelMatrix = modifier.toModelMatrix(),
        material = shape.defaultMaterial(),
    )
    is SceneNode.Model -> RenderableNode(
        meshId = meshId,
        modelMatrix = modifier.toModelMatrix(),
        // Models use a default white material so the GLB's own vertex colors or future textures show correctly.
        material = MaterialData(r = 1f, g = 1f, b = 1f, a = 1f),
    )
}
