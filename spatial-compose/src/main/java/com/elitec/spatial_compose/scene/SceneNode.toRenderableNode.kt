package com.elitec.spatial_compose.scene

import com.elitec.spatial_compose.modifier.toModelMatrix
import com.elitec.spatial_compose.shapes.defaultMaterial
import com.elitec.spatial_core.scene.RenderableNode

internal fun SceneNode.toRenderableNode(): RenderableNode = RenderableNode(
    meshId = shape.name,
    modelMatrix = modifier.toModelMatrix(),
    material = shape.defaultMaterial(),
)