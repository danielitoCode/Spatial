package com.elitec.spatial_compose.shapes

import com.elitec.spatial_core.scene.MaterialData

internal fun PrimitiveShape.defaultMaterial(): MaterialData = when (this) {
    PrimitiveShape.Cube -> MaterialData(0.95f, 0.35f, 0.20f)
    PrimitiveShape.Sphere -> MaterialData(0.25f, 0.65f, 1.0f)
    PrimitiveShape.Plane -> MaterialData(0.35f, 0.42f, 0.48f)
}