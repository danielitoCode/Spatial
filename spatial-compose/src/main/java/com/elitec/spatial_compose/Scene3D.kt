package com.elitec.spatial_compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import com.elitec.spatial_units.Angle
import com.elitec.spatial_units.Distance
import com.elitec.spatial_units.meters

@Immutable
data class Vec3Distance(
    val x: Distance = 0f.meters,
    val y: Distance = 0f.meters,
    val z: Distance = 0f.meters,
)

@Immutable
data class Rotation3D(
    val x: Angle? = null,
    val y: Angle? = null,
    val z: Angle? = null,
)

@Immutable
data class Modifier3D(
    val position: Vec3Distance = Vec3Distance(),
    val rotation: Rotation3D = Rotation3D(),
    val scale: Vec3Distance = Vec3Distance(1f.meters, 1f.meters, 1f.meters),
    val size: Vec3Distance? = null,
) {
    fun position(x: Distance, y: Distance, z: Distance): Modifier3D = copy(position = Vec3Distance(x, y, z))
    fun rotateX(angle: Angle): Modifier3D = copy(rotation = rotation.copy(x = angle))
    fun rotateY(angle: Angle): Modifier3D = copy(rotation = rotation.copy(y = angle))
    fun rotateZ(angle: Angle): Modifier3D = copy(rotation = rotation.copy(z = angle))
    fun scale(x: Distance, y: Distance, z: Distance): Modifier3D = copy(scale = Vec3Distance(x, y, z))
    fun size(all: Distance): Modifier3D = copy(size = Vec3Distance(all, all, all))
    fun size(width: Distance, height: Distance, depth: Distance): Modifier3D = copy(size = Vec3Distance(width, height, depth))

    companion object {
        val Default = Modifier3D()
    }
}

@Immutable
data class SceneNode(
    val shape: PrimitiveShape,
    val modifier: Modifier3D = Modifier3D.Default,
)

enum class PrimitiveShape {
    Cube,
    Sphere,
    Plane,
}

class SceneBuilder internal constructor() {
    private val internalNodes = mutableListOf<SceneNode>()
    val nodes: List<SceneNode> get() = internalNodes

    fun cube(modifier: Modifier3D = Modifier3D.Default) {
        internalNodes += SceneNode(PrimitiveShape.Cube, modifier)
    }

    fun sphere(modifier: Modifier3D = Modifier3D.Default) {
        internalNodes += SceneNode(PrimitiveShape.Sphere, modifier)
    }

    fun plane(modifier: Modifier3D = Modifier3D.Default) {
        internalNodes += SceneNode(PrimitiveShape.Plane, modifier)
    }
}

@Composable
fun rememberSceneGraph(content: SceneBuilder.() -> Unit): List<SceneNode> = remember(content) {
    SceneBuilder().apply(content).nodes
}