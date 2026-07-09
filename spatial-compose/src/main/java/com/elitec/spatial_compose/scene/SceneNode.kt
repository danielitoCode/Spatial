package com.elitec.spatial_compose.scene

import androidx.compose.runtime.Immutable
import com.elitec.spatial_compose.modifier.Modifier3D
import com.elitec.spatial_compose.shapes.PrimitiveShape

/**
 * Represents an element in the 3D scene graph.
 * It can be either a built-in primitive (Cube, Sphere, Plane) or an externally loaded 3D model.
 */
@Immutable
internal sealed class SceneNode {
    abstract val modifier: Modifier3D

    /**
     * A node representing a built-in primitive shape.
     */
    @Immutable
    internal data class Primitive(
        val shape: PrimitiveShape,
        override val modifier: Modifier3D = Modifier3D.Default,
    ) : SceneNode()

    /**
     * A node representing an externally loaded 3D model.
     * The [meshId] is derived from the [ModelResource] and is used to look up the loaded mesh data.
     */
    @Immutable
    internal data class Model(
        val meshId: String,
        override val modifier: Modifier3D = Modifier3D.Default,
    ) : SceneNode()
}
