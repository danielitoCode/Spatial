package com.elitec.spatial_compose.scene

import androidx.compose.runtime.Immutable
import com.elitec.spatial_compose.modifier.Modifier3D
import com.elitec.spatial_compose.shapes.PrimitiveShape

@Immutable
internal data class SceneNode(
    val shape: PrimitiveShape,
    val modifier: Modifier3D = Modifier3D.Default,
)