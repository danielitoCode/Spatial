package com.elitec.spatial_compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
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

object Shapes3D {
    val Cube: PrimitiveShape = PrimitiveShape.Cube
    val Sphere: PrimitiveShape = PrimitiveShape.Sphere
    val Plane: PrimitiveShape = PrimitiveShape.Plane
}

@Immutable
data class Modifier3D(
    val position: Vec3Distance = Vec3Distance(),
    val rotation: Rotation3D = Rotation3D(),
    val scale: Vec3Distance = Vec3Distance(1f.meters, 1f.meters, 1f.meters),
    val size: Vec3Distance? = null,
) {
    fun position(x: Float, y: Float, z: Float): Modifier3D = position(x.meters, y.meters, z.meters)
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
data class CameraState(
    val yaw: Angle? = null,
    val pitch: Angle? = null,
    val zoom: Float = 1f,
)

@Composable
fun rememberCameraState(): CameraState = remember { CameraState() }

@Immutable
data class SceneGestures internal constructor(
    val mode: Mode,
) {
    enum class Mode {
        Orbit,
    }
}

object Gestures {
    fun orbit(): SceneGestures = SceneGestures(SceneGestures.Mode.Orbit)
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

    internal fun add(shape: PrimitiveShape, modifier: Modifier3D) {
        internalNodes += SceneNode(shape, modifier)
    }

    // Legacy DSL compatibility.
    fun cube(
        modifier: Modifier3D = Modifier3D.Default
    ) = add(PrimitiveShape.Cube, modifier)

    fun sphere(
        modifier: Modifier3D = Modifier3D.Default
    ) = add(PrimitiveShape.Sphere, modifier)

    fun plane(
        modifier: Modifier3D = Modifier3D.Default
    ) = add(PrimitiveShape.Plane, modifier)

    fun element(
        shape: PrimitiveShape,
        modifier: Modifier3D = Modifier3D.Default,
    ) {
        internalNodes += SceneNode(shape, modifier)
    }

    internal fun clear() {
        internalNodes.clear()
    }
}

@Immutable
object Element {
    @Composable
    fun Cube(modifier: Modifier3D = Modifier3D.Default) {
        Element(shape = PrimitiveShape.Cube, modifier = modifier)
    }

    @Composable
    fun Sphere(modifier: Modifier3D = Modifier3D.Default) {
        Element(shape = PrimitiveShape.Sphere, modifier = modifier)
    }

    @Composable
    fun Plane(modifier: Modifier3D = Modifier3D.Default) {
        Element(shape = PrimitiveShape.Plane, modifier = modifier)
    }
}

@Stable
class SceneContentScope internal constructor(
    private val sceneBuilder: SceneBuilder,
) {
    internal fun reset() {
        sceneBuilder.clear()
    }
    internal fun build(): List<SceneNode> = sceneBuilder.nodes.toList()
    fun add(shape: PrimitiveShape, modifier: Modifier3D = Modifier3D.Default) = sceneBuilder.element(shape, modifier)
}

private val LocalSceneContentScope = compositionLocalOf<SceneContentScope?> { null }

@Composable
fun Element(
    shape: PrimitiveShape,
    modifier: Modifier3D = Modifier3D.Default,
) {
    val sceneScope = LocalSceneContentScope.current
        ?: error("Element(...) must be called inside Scene { ... } content.")
    sceneScope.add(shape, modifier)
}

@Composable
fun rememberSceneGraph(content: @Composable SceneContentScope.() -> Unit): List<SceneNode> {
    val scope = remember { SceneContentScope(SceneBuilder()) }
    scope.reset()
    CompositionLocalProvider(LocalSceneContentScope provides scope) {
        scope.content()
    }
    return scope.build()
}

@Composable
fun Scene(
    content: @Composable SceneContentScope.() -> Unit,
): List<SceneNode> {
    return rememberSceneGraph(content)
}