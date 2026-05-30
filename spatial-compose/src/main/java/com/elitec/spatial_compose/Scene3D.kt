package com.elitec.spatial_compose

import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import com.elitec.spatial_camera.CameraSnapshot
import com.elitec.spatial_camera.CameraUpdateSource
import com.elitec.spatial_core.scene.MaterialData
import com.elitec.spatial_core.scene.RenderableNode
import com.elitec.spatial_renderer.gl.SpatialGlSurfaceView
import com.elitec.spatial_units.Angle
import com.elitec.spatial_units.Distance
import com.elitec.spatial_units.deg
import com.elitec.spatial_units.meters
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

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

/**
 * Compose-owned camera state for Scene.
 *
 * The renderer only receives immutable [CameraSnapshot] values. Gesture and animation writes go
 * through this state holder so camera control follows the same state-hoisting model as Compose.
 */
@Stable
class CameraState internal constructor(
    yaw: Angle,
    pitch: Angle,
    zoom: Float,
) {
    var yaw: Angle by mutableStateOf(yaw)
        private set
    var pitch: Angle by mutableStateOf(pitch)
        private set
    var zoom: Float by mutableFloatStateOf(zoom.coerceIn(MinZoom, MaxZoom))
        private set
    var version: Long by mutableLongStateOf(0L)
        private set
    var source: CameraUpdateSource by mutableStateOf(CameraUpdateSource.Gesture)
        private set

    fun orbitTo(
        yaw: Angle = this.yaw,
        pitch: Angle = this.pitch,
        source: CameraUpdateSource = CameraUpdateSource.Remote,
    ) {
        write(source) {
            this.yaw = yaw
            this.pitch = pitch.coercePitch()
        }
    }

    fun orbitBy(
        deltaYawDegrees: Float,
        deltaPitchDegrees: Float,
        source: CameraUpdateSource = CameraUpdateSource.Gesture,
    ) {
        orbitTo(
            yaw = (yaw.toDegrees() + deltaYawDegrees).deg,
            pitch = (pitch.toDegrees() + deltaPitchDegrees).deg,
            source = source,
        )
    }

    fun zoomTo(zoom: Float, source: CameraUpdateSource = CameraUpdateSource.Remote) {
        write(source) {
            this.zoom = zoom.coerceIn(MinZoom, MaxZoom)
        }
    }

    fun zoomBy(scaleDelta: Float, source: CameraUpdateSource = CameraUpdateSource.Gesture) {
        val safeScale = if (scaleDelta <= 0f) 1f else scaleDelta
        zoomTo(zoom * safeScale, source)
    }

    suspend fun animateTo(
        yaw: Angle = this.yaw,
        pitch: Angle = this.pitch,
        zoom: Float = this.zoom,
        durationMillis: Long = DefaultAnimationDurationMillis,
    ) {
        val startYaw = this.yaw.toDegrees()
        val startPitch = this.pitch.toDegrees()
        val startZoom = this.zoom
        val targetYaw = yaw.toDegrees()
        val targetPitch = pitch.coercePitch().toDegrees()
        val targetZoom = zoom.coerceIn(MinZoom, MaxZoom)
        val durationNanos = durationMillis.coerceAtLeast(1L) * 1_000_000L
        val startTime = withFrameNanos { it }

        while (true) {
            val frameTime = withFrameNanos { it }
            val linearProgress = ((frameTime - startTime).toFloat() / durationNanos).coerceIn(0f, 1f)
            val easedProgress = smoothStep(linearProgress)
            write(CameraUpdateSource.Animation) {
                this.yaw = lerp(startYaw, targetYaw, easedProgress).deg
                this.pitch = lerp(startPitch, targetPitch, easedProgress).deg.coercePitch()
                this.zoom = lerp(startZoom, targetZoom, easedProgress).coerceIn(MinZoom, MaxZoom)
            }
            if (linearProgress >= 1f) break
        }
    }

    fun snapshot(): CameraSnapshot = CameraSnapshot(
        yaw = yaw.toDegrees(),
        pitch = pitch.toDegrees(),
        zoom = zoom,
        version = version,
        source = source,
    )

    private fun write(updateSource: CameraUpdateSource, block: CameraState.() -> Unit) {
        block()
        source = updateSource
        version += 1L
    }

    private fun Angle.coercePitch(): Angle = toDegrees().coerceIn(MinPitchDegrees, MaxPitchDegrees).deg

    private companion object {
        const val MinPitchDegrees = -89f
        const val MaxPitchDegrees = 89f
        const val MinZoom = 0.3f
        const val MaxZoom = 4f
        const val DefaultAnimationDurationMillis = 450L
    }
}

@Immutable
sealed interface GestureSensitivity {
    /**
     * Keeps orbiting smooth by scaling drag sensitivity with camera zoom, scene bounds, and the
     * available input viewport when Compose can report it.
     */
    @Immutable
    data object Adaptive : GestureSensitivity

    /**
     * Uses the supplied angular delta for each input pixel. This intentionally preserves the old,
     * direct orbit behavior for callers that prefer a sharper/manual response.
     */
    @Immutable
    data class Fixed(val degreesPerPixel: Float) : GestureSensitivity
}

@Composable
fun rememberCameraState(
    yaw: Angle = 0f.deg,
    pitch: Angle = 0f.deg,
    zoom: Float = 1f,
): CameraState = remember { CameraState(yaw, pitch, zoom) }

@Immutable
data class SceneGestures internal constructor(
    val mode: Mode,
    val orbitSensitivity: GestureSensitivity = GestureSensitivity.Adaptive,
) {
    enum class Mode {
        None,
        Orbit,
    }
}

object Gestures {
    fun none(): SceneGestures = SceneGestures(SceneGestures.Mode.None)
    /**
     * Enables one-finger orbit gestures. The default is [GestureSensitivity.Adaptive], matching the
     * current public API while smoothing the effective per-pixel angular delta.
     */
    fun orbit(sensitivity: GestureSensitivity = GestureSensitivity.Adaptive): SceneGestures =
        SceneGestures(SceneGestures.Mode.Orbit, sensitivity)
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

/**
 * Compose-first 3D scene host.
 *
 * Like Canvas, callers describe content in a scoped DSL. Unlike Canvas, Scene owns a real Android
 * rendering surface internally and continuously syncs the resolved scene graph and camera state to
 * the OpenGL renderer.
 */
@Composable
fun Scene(
    modifier: Modifier = Modifier,
    cameraState: CameraState = rememberCameraState(),
    gestures: SceneGestures = Gestures.orbit(),
    content: @Composable SceneContentScope.() -> Unit,
) {
    val sceneNodes = rememberSceneGraph(content)
    val renderableNodes = sceneNodes.map(SceneNode::toRenderableNode)
    val cameraSnapshot = cameraState.snapshot()

    var viewportSize by remember { mutableStateOf(IntSize.Zero) }

    AndroidView(
        modifier = modifier
            .onSizeChanged { viewportSize = it }
            .sceneGestureInput(cameraState, gestures, sceneNodes, viewportSize),
        factory = { context ->
            SpatialGlSurfaceView(context).apply {
                updateScene(renderableNodes)
                updateCamera(cameraSnapshot)
            }
        },
        update = { view ->
            view.updateScene(renderableNodes)
            view.updateCamera(cameraSnapshot)
        },
    )
}

private fun Modifier.sceneGestureInput(
    cameraState: CameraState,
    gestures: SceneGestures,
    sceneNodes: List<SceneNode>,
    viewportSize: IntSize,
): Modifier {
    if (gestures.mode == SceneGestures.Mode.None) return this

    var lastX = 0f
    var lastY = 0f
    return pointerInteropFilter { event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
                true
            }
            MotionEvent.ACTION_MOVE -> {
                if (gestures.mode == SceneGestures.Mode.Orbit) {
                    val dx = event.x - lastX
                    val dy = event.y - lastY
                    val delta = resolveOrbitGestureDelta(
                        dx = dx,
                        dy = dy,
                        cameraZoom = cameraState.zoom,
                        sceneNodes = sceneNodes,
                        viewportSize = viewportSize,
                        sensitivity = gestures.orbitSensitivity,
                    )
                    cameraState.orbitBy(delta.yawDegrees, delta.pitchDegrees)
                    lastX = event.x
                    lastY = event.y
                }
                true
            }
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_POINTER_UP -> true
            else -> true
        }
    }
}

internal data class OrbitGestureDeltaDegrees(
    val yawDegrees: Float,
    val pitchDegrees: Float,
)

internal fun resolveOrbitGestureDelta(
    dx: Float,
    dy: Float,
    cameraZoom: Float,
    sceneNodes: List<SceneNode>,
    viewportSize: IntSize = IntSize.Zero,
    sensitivity: GestureSensitivity = GestureSensitivity.Adaptive,
): OrbitGestureDeltaDegrees {
    val degreesPerPixel = when (sensitivity) {
        GestureSensitivity.Adaptive -> adaptiveOrbitDegreesPerPixel(cameraZoom, sceneNodes, viewportSize)
        is GestureSensitivity.Fixed -> sensitivity.degreesPerPixel.takeIf { it.isFinite() && it > 0f }
            ?: DefaultOrbitDegreesPerPixel
    }
    return OrbitGestureDeltaDegrees(
        yawDegrees = (dx * degreesPerPixel).coerceIn(-MaxOrbitDegreesPerStep, MaxOrbitDegreesPerStep),
        pitchDegrees = (dy * degreesPerPixel).coerceIn(-MaxOrbitDegreesPerStep, MaxOrbitDegreesPerStep),
    )
}

internal fun adaptiveOrbitDegreesPerPixel(
    cameraZoom: Float,
    sceneNodes: List<SceneNode>,
    viewportSize: IntSize = IntSize.Zero,
): Float {
    val safeZoom = cameraZoom.takeIf { it.isFinite() && it > 0f } ?: 1f
    val sceneDiameter = approximateSceneDiameterMeters(sceneNodes)
    val sceneFactor = sqrt(sceneDiameter / ReferenceSceneDiameterMeters)
        .coerceIn(MinAdaptiveSceneFactor, MaxAdaptiveSceneFactor)
    val viewportFactor = viewportSize.maxDimension
        .takeIf { it > 0 }
        ?.let { sqrt(ReferenceViewportPixels / it.toFloat()).coerceIn(MinViewportFactor, MaxViewportFactor) }
        ?: 1f

    return (DefaultOrbitDegreesPerPixel * sceneFactor * viewportFactor / safeZoom)
        .coerceIn(MinAdaptiveDegreesPerPixel, DefaultOrbitDegreesPerPixel)
}

internal fun approximateSceneDiameterMeters(sceneNodes: List<SceneNode>): Float {
    if (sceneNodes.isEmpty()) return ReferenceSceneDiameterMeters

    var maxExtent = 0f
    sceneNodes.forEach { node ->
        val size = node.modifier.size ?: node.modifier.scale
        val halfX = abs(size.x.meters).coerceAtLeast(MinNodeDimensionMeters) / 2f
        val halfY = abs(size.y.meters).coerceAtLeast(MinNodeDimensionMeters) / 2f
        val halfZ = abs(size.z.meters).coerceAtLeast(MinNodeDimensionMeters) / 2f
        maxExtent = max(maxExtent, abs(node.modifier.position.x.meters) + halfX)
        maxExtent = max(maxExtent, abs(node.modifier.position.y.meters) + halfY)
        maxExtent = max(maxExtent, abs(node.modifier.position.z.meters) + halfZ)
    }
    return (maxExtent * 2f).coerceAtLeast(MinSceneDiameterMeters)
}

private val IntSize.maxDimension: Int get() = max(width, height)

private fun SceneNode.toRenderableNode(): RenderableNode = RenderableNode(
    meshId = shape.name,
    modelMatrix = modifier.toModelMatrix(),
    material = shape.defaultMaterial(),
)

private fun Modifier3D.toModelMatrix(): FloatArray {
    val resolvedSize = size ?: scale
    val matrix = identityMatrix()
    matrix[0] = resolvedSize.x.meters
    matrix[5] = resolvedSize.y.meters
    matrix[10] = resolvedSize.z.meters
    matrix[12] = position.x.meters
    matrix[13] = position.y.meters
    matrix[14] = position.z.meters
    return matrix
}

private fun PrimitiveShape.defaultMaterial(): MaterialData = when (this) {
    PrimitiveShape.Cube -> MaterialData(0.95f, 0.35f, 0.20f)
    PrimitiveShape.Sphere -> MaterialData(0.25f, 0.65f, 1.0f)
    PrimitiveShape.Plane -> MaterialData(0.35f, 0.42f, 0.48f)
}

private fun identityMatrix(): FloatArray = FloatArray(16) { index -> if (index % 5 == 0) 1f else 0f }

private fun Angle.toDegrees(): Float = (radians * 180f / PI.toFloat())

private fun lerp(start: Float, stop: Float, fraction: Float): Float = start + (stop - start) * fraction

private fun smoothStep(value: Float): Float = value * value * (3f - 2f * value)

private const val DefaultOrbitDegreesPerPixel = 0.25f
private const val MaxOrbitDegreesPerStep = 32f
private const val ReferenceSceneDiameterMeters = 2f
private const val MinSceneDiameterMeters = 0.05f
private const val MinNodeDimensionMeters = 0.01f
private const val MinAdaptiveSceneFactor = 0.2f
private const val MaxAdaptiveSceneFactor = 1.5f
private const val ReferenceViewportPixels = 1080f
private const val MinViewportFactor = 0.75f
private const val MaxViewportFactor = 1.25f
private const val MinAdaptiveDegreesPerPixel = 0.015f