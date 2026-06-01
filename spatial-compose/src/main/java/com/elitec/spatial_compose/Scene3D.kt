package com.elitec.spatial_compose

import android.content.Context
import android.view.MotionEvent
import android.view.View
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
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import com.elitec.spatial_camera.CameraAnimationScheduler
import com.elitec.spatial_camera.CameraDelta
import com.elitec.spatial_camera.CameraRuntimeContract
import com.elitec.spatial_camera.GestureMotionPolicy
import com.elitec.spatial_camera.SpatialCamera
import com.elitec.spatial_camera.MotionSpec as RuntimeMotionSpec
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.camera.CameraUpdateSource
import com.elitec.spatial_motion.CameraMotionProfile
import com.elitec.spatial_motion.DefaultAdaptiveAnimationMaxDurationMillis
import com.elitec.spatial_motion.DefaultAdaptiveAnimationMinDurationMillis
import com.elitec.spatial_motion.DefaultTargetAngularVelocityDegreesPerSecond
import com.elitec.spatial_motion.DefaultTargetZoomVelocityPerSecond
import com.elitec.spatial_motion.MotionEasing
import com.elitec.spatial_motion.resolveCameraMotionPlan
import com.elitec.spatial_core.scene.MaterialData
import com.elitec.spatial_core.scene.RenderableNode
import com.elitec.spatial_renderer.adapter.ChoreographerFrameScheduler
import com.elitec.spatial_renderer.gl.SpatialGlRenderTarget
import com.elitec.spatial_runtime.SpatialRuntime
import com.elitec.spatial_units.Angle
import com.elitec.spatial_units.Distance
import com.elitec.spatial_units.deg
import com.elitec.spatial_units.meters
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

@Immutable
internal data class Vec3Distance(
    val x: Distance = 0f.meters,
    val y: Distance = 0f.meters,
    val z: Distance = 0f.meters,
)

@Immutable
internal data class Rotation3D(
    val x: Angle? = null,
    val y: Angle? = null,
    val z: Angle? = null,
)

internal object Shapes3D {
    val Cube: PrimitiveShape = PrimitiveShape.Cube
    val Sphere: PrimitiveShape = PrimitiveShape.Sphere
    val Plane: PrimitiveShape = PrimitiveShape.Plane
}

@Immutable
class Modifier3D internal constructor(
    internal val position: Vec3Distance = Vec3Distance(),
    internal val rotation: Rotation3D = Rotation3D(),
    internal val scale: Vec3Distance = Vec3Distance(1f.meters, 1f.meters, 1f.meters),
    internal val size: Vec3Distance? = null,
) {
    fun position(x: Float, y: Float, z: Float): Modifier3D = position(x.meters, y.meters, z.meters)
    fun position(x: Distance, y: Distance, z: Distance): Modifier3D = copy(position = Vec3Distance(x, y, z))
    fun rotateX(angle: Angle): Modifier3D = copy(rotation = rotation.copy(x = angle))
    fun rotateY(angle: Angle): Modifier3D = copy(rotation = rotation.copy(y = angle))
    fun rotateZ(angle: Angle): Modifier3D = copy(rotation = rotation.copy(z = angle))
    fun scale(x: Distance, y: Distance, z: Distance): Modifier3D = copy(scale = Vec3Distance(x, y, z))
    fun size(all: Distance): Modifier3D = copy(size = Vec3Distance(all, all, all))
    fun size(width: Distance, height: Distance, depth: Distance): Modifier3D = copy(size = Vec3Distance(width, height, depth))

    private fun copy(
        position: Vec3Distance = this.position,
        rotation: Rotation3D = this.rotation,
        scale: Vec3Distance = this.scale,
        size: Vec3Distance? = this.size,
    ): Modifier3D = Modifier3D(
        position = position,
        rotation = rotation,
        scale = scale,
        size = size,
    )

    companion object {
        val Default = Modifier3D()
    }
}

internal interface SceneRenderHost {
    val view: View
    fun updateScene(nodes: List<RenderableNode>)
    fun updateCamera(cameraSnapshot: CameraSnapshot)
    fun requestFrame()
}

internal fun interface SceneRenderHostFactory {
    fun create(context: Context): SceneRenderHost
}

private object DefaultSceneRenderHostFactory : SceneRenderHostFactory {
    override fun create(context: Context): SceneRenderHost = SpatialRuntimeSceneRenderHost(context)
}

private class SpatialRuntimeSceneRenderHost(context: Context) : SceneRenderHost {
    private val renderTarget = SpatialGlRenderTarget(context)
    private val runtimeCamera = SpatialCamera()
    private val runtime = SpatialRuntime(
        renderBackend = renderTarget,
        frameScheduler = ChoreographerFrameScheduler(),
        cameraRuntime = runtimeCamera,
    )
    private var pendingNodes: List<RenderableNode> = emptyList()
    private var pendingCameraSnapshot: CameraSnapshot = runtimeCamera.snapshot()

    override val view: View get() = renderTarget.view

    init {
        runtime.onInitialize()
    }

    override fun updateScene(nodes: List<RenderableNode>) {
        pendingNodes = nodes
    }

    override fun updateCamera(cameraSnapshot: CameraSnapshot) {
        pendingCameraSnapshot = cameraSnapshot
    }

    override fun requestFrame() {
        runtime.requestFrame(
            nodes = pendingNodes,
            cameraSnapshot = pendingCameraSnapshot,
        )
    }
}

internal fun SceneRenderHost.renderSceneFrame(
    nodes: List<RenderableNode>,
    cameraSnapshot: CameraSnapshot,
) {
    updateScene(nodes)
    updateCamera(cameraSnapshot)
    requestFrame()
}

private class SceneRenderHostHolder {
    var host: SceneRenderHost? = null
}

/**
 * Compose-owned camera adapter for Scene.
 *
 * [CameraSnapshot] is the single Core #1 camera contract. This state holder exposes Compose-friendly
 * observable fields while delegating normalization and atomic mutations to [CameraRuntimeContract]
 * (the default engine is [SpatialCamera]). The renderer only receives immutable snapshots.
 *
 * Public API guidance: Compose callers should use [CameraState] and [CameraState.animateTo]; runtime
 * callers should use [SpatialCamera]. Both APIs synchronize through [CameraSnapshot].
 */
@Stable
class CameraState internal constructor(
    yaw: Angle,
    pitch: Angle,
    zoom: Float,
    cameraRuntime: CameraRuntimeContract? = null,
) {
    private val cameraRuntime: CameraRuntimeContract = cameraRuntime ?: SpatialCamera(
        initialState = CameraSnapshot(
            yaw = yaw.toDegrees(),
            pitch = pitch.toDegrees(),
            zoom = zoom,),
        animationScheduler = ComposeFrameCameraAnimationScheduler(onFrameApplied = { syncFromRuntime() }),
    )
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

    init {
        syncFromRuntime()
    }

    fun orbitTo(
        yaw: Angle = this.yaw,
        pitch: Angle = this.pitch,
        source: CameraUpdateSource = CameraUpdateSource.Remote,
    ) {
        cameraRuntime.orbitTo(
            yaw = yaw.toDegrees(),
            pitch = pitch.toDegrees(),
            source = source,
        )
        syncFromRuntime()
    }

    fun orbitBy(
        deltaYawDegrees: Float,
        deltaPitchDegrees: Float,
        source: CameraUpdateSource = CameraUpdateSource.Gesture,
    ) {
        cameraRuntime.applyDelta(
            delta = CameraDelta(
                deltaYaw = deltaYawDegrees,
                deltaPitch = deltaPitchDegrees,
                motionPolicy = GestureMotionPolicy.Raw,
            ),
            source = source,
        )
        syncFromRuntime()
    }

    fun zoomTo(zoom: Float, source: CameraUpdateSource = CameraUpdateSource.Remote) {
        cameraRuntime.zoomTo(zoom = zoom, source = source)
        syncFromRuntime()
    }

    /**
     * Applies a multiplicative visual magnification delta. Values above `1f` zoom in visually
     * (objects appear closer/larger); values below `1f` zoom out visually.
     */
    fun zoomBy(scaleDelta: Float, source: CameraUpdateSource = CameraUpdateSource.Gesture) {
        cameraRuntime.applyDelta(
            delta = CameraDelta(
                zoomScaleDelta = scaleDelta,
                motionPolicy = GestureMotionPolicy.Raw,
            ),
            source = source,
        )
        syncFromRuntime()
    }

    fun jumpTo(
        yaw: Angle = this.yaw,
        pitch: Angle = this.pitch,
        zoom: Float = this.zoom,
        source: CameraUpdateSource = CameraUpdateSource.Remote,
    ) {
        cameraRuntime.jumpTo(
            yaw = yaw.toDegrees(),
            pitch = pitch.toDegrees(),
            zoom = zoom,
            source = source,
        )
        syncFromRuntime()
    }

    fun syncSnapshot(snapshot: CameraSnapshot) {
        cameraRuntime.syncSnapshot(snapshot)
        syncFromRuntime()
    }

    suspend fun animateTo(
        yaw: Angle = this.yaw,
        pitch: Angle = this.pitch,
        zoom: Float = this.zoom,
        durationMillis: Long? = null,
        motion: MotionSpec = MotionSpec.Adaptive,
    ) {
        val plan = resolveCameraMotionPlan(
            startYawDegrees = this.yaw.toDegrees(),
            startPitchDegrees = this.pitch.toDegrees(),
            startZoom = this.zoom,
            targetYawDegrees = yaw.toDegrees(),
            targetPitchDegrees = pitch.coercePitch().toDegrees(),
            targetZoom = zoom.coerceIn(MinZoom, MaxZoom),
            profile = motion.toCameraMotionProfile(),
            explicitDurationMillis = durationMillis,
            instant = motion.instant,
            minPitchDegrees = MinPitchDegrees,
            maxPitchDegrees = MaxPitchDegrees,
            minZoom = MinZoom,
            maxZoom = MaxZoom,
        )
        val runtimeMotion: RuntimeMotionSpec = if (plan.durationMillis <= 0L || motion.instant) {
            RuntimeMotionSpec.Instant
        } else {
            RuntimeMotionSpec.Tween(
                durationMs = plan.durationMillis,
                easing = plan.easing,
            )
        }

        cameraRuntime.animateTo(
            yaw = plan.targetYawDegrees,
            pitch = plan.targetPitchDegrees,
            zoom = plan.targetZoom,
            motion = runtimeMotion,
        )
        syncFromRuntime()
    }

    fun snapshot(): CameraSnapshot = cameraRuntime.snapshot()

    private fun syncFromRuntime() {
        val snapshot = cameraRuntime.snapshot()
        yaw = snapshot.yaw.deg
        pitch = snapshot.pitch.deg
        zoom = snapshot.zoom
        version = snapshot.version
        source = snapshot.source
    }

    private fun write(updateSource: CameraUpdateSource, block: CameraState.() -> Unit) {
        block()
        source = updateSource
        version += 1L
    }

    private fun Angle.coercePitch(): Angle = toDegrees().coerceIn(MinPitchDegrees, MaxPitchDegrees).deg

    private companion object {
        const val MinPitchDegrees = CameraSnapshot.MIN_PITCH_DEGREES
        const val MaxPitchDegrees = CameraSnapshot.MAX_PITCH_DEGREES
        const val MinZoom = CameraSnapshot.MIN_ZOOM
        const val MaxZoom = CameraSnapshot.MAX_ZOOM
    }
}

private class ComposeFrameCameraAnimationScheduler(
    private val onFrameApplied: () -> Unit,
) : CameraAnimationScheduler {
    override suspend fun schedule(durationMs: Long, onFrame: (elapsedMs: Long) -> Unit) {
        val safeDurationMillis = durationMs.coerceAtLeast(0L)
        if (safeDurationMillis == 0L) {
            onFrame(0L)
            onFrameApplied()
            return
        }

        val durationNanos = safeDurationMillis * NanosPerMillisecond
        val startTimeNanos = withFrameNanos { it }
        onFrame(0L)
        onFrameApplied()

        while (true) {
            val frameTimeNanos = withFrameNanos { it }
            val elapsedNanos = (frameTimeNanos - startTimeNanos).coerceAtLeast(0L)
            val elapsedMillis = (elapsedNanos / NanosPerMillisecond).coerceAtMost(safeDurationMillis)
            onFrame(elapsedMillis)
            onFrameApplied()
            if (elapsedNanos >= durationNanos) break
        }
    }

    private companion object {
        const val NanosPerMillisecond = 1_000_000L
    }
}

/**
 * Describes how camera animations choose their duration and interpolation curve.
 *
 * Compose keeps this [MotionSpec] as an adapter over the shared spatial-motion planner so the UI
 * API can expose adaptive policies without duplicating easing or duration math from runtime.
 * [Adaptive] derives duration from yaw, pitch, and relative zoom distance. [Instant] is the
 * explicit opt-in path for visual jumps. Use [custom] to tune the adaptive velocity and bounds.
 */
@Immutable
sealed class MotionSpec(
    val minDurationMillis: Long,
    val maxDurationMillis: Long,
    val targetAngularVelocityDegreesPerSecond: Float,
    val targetZoomVelocityPerSecond: Float,
    val easing: MotionEasing,
    val instant: Boolean,
) {
    data object Adaptive : MotionSpec(
        minDurationMillis = DefaultAdaptiveAnimationMinDurationMillis,
        maxDurationMillis = DefaultAdaptiveAnimationMaxDurationMillis,
        targetAngularVelocityDegreesPerSecond = DefaultTargetAngularVelocityDegreesPerSecond,
        targetZoomVelocityPerSecond = DefaultTargetZoomVelocityPerSecond,
        easing = MotionEasing.SmoothStep,
        instant = false,
    )

    data object Instant : MotionSpec(
        minDurationMillis = 0L,
        maxDurationMillis = 0L,
        targetAngularVelocityDegreesPerSecond = Float.POSITIVE_INFINITY,
        targetZoomVelocityPerSecond = Float.POSITIVE_INFINITY,
        easing = MotionEasing.Linear,
        instant = true,
    )

    class Custom internal constructor(
        minDurationMillis: Long,
        maxDurationMillis: Long,
        targetAngularVelocityDegreesPerSecond: Float,
        targetZoomVelocityPerSecond: Float,
        easing: MotionEasing,
        instant: Boolean,
    ) : MotionSpec(
        minDurationMillis = minDurationMillis,
        maxDurationMillis = maxDurationMillis,
        targetAngularVelocityDegreesPerSecond = targetAngularVelocityDegreesPerSecond,
        targetZoomVelocityPerSecond = targetZoomVelocityPerSecond,
        easing = easing,
        instant = instant,
    )

    companion object {
        fun custom(
            minDurationMillis: Long = DefaultAdaptiveAnimationMinDurationMillis,
            maxDurationMillis: Long = DefaultAdaptiveAnimationMaxDurationMillis,
            targetAngularVelocityDegreesPerSecond: Float = DefaultTargetAngularVelocityDegreesPerSecond,
            targetZoomVelocityPerSecond: Float = DefaultTargetZoomVelocityPerSecond,
            easing: MotionEasing = MotionEasing.SmoothStep,
            instant: Boolean = false,
        ): MotionSpec = Custom(
            minDurationMillis = minDurationMillis,
            maxDurationMillis = maxDurationMillis,
            targetAngularVelocityDegreesPerSecond = targetAngularVelocityDegreesPerSecond,
            targetZoomVelocityPerSecond = targetZoomVelocityPerSecond,
            easing = easing,
            instant = instant,
        )
    }
}

private fun MotionSpec.toCameraMotionProfile(): CameraMotionProfile = CameraMotionProfile(
    minDurationMillis = minDurationMillis,
    maxDurationMillis = maxDurationMillis,
    targetAngularVelocityDegreesPerSecond = targetAngularVelocityDegreesPerSecond,
    targetZoomVelocityPerSecond = targetZoomVelocityPerSecond,
    easing = easing,
)

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
    val orbitEnabled: Boolean get() = mode.orbitEnabled
    val zoomEnabled: Boolean get() = mode.zoomEnabled

    enum class Mode(
        internal val orbitEnabled: Boolean,
        internal val zoomEnabled: Boolean,
    ) {
        None(orbitEnabled = false, zoomEnabled = false),
        Orbit(orbitEnabled = true, zoomEnabled = false),
        OrbitAndZoom(orbitEnabled = true, zoomEnabled = true),
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

    /**
     * Enables one-finger orbit and two-finger pinch zoom gestures at the same time.
     */
    fun orbitAndZoom(sensitivity: GestureSensitivity = GestureSensitivity.Adaptive): SceneGestures =
        SceneGestures(SceneGestures.Mode.OrbitAndZoom, sensitivity)
}

@Immutable
internal data class SceneNode(
    val shape: PrimitiveShape,
    val modifier: Modifier3D = Modifier3D.Default,
)

internal enum class PrimitiveShape {
    Cube,
    Sphere,
    Plane,
}

internal class SceneBuilder {
    private val internalNodes = mutableListOf<SceneNode>()
    val nodes: List<SceneNode> get() = internalNodes

    internal fun add(shape: PrimitiveShape, modifier: Modifier3D) {
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
        SceneElement(shape = PrimitiveShape.Cube, modifier = modifier)
    }

    @Composable
    fun Sphere(modifier: Modifier3D = Modifier3D.Default) {
        SceneElement(shape = PrimitiveShape.Sphere, modifier = modifier)
    }

    @Composable
    fun Plane(modifier: Modifier3D = Modifier3D.Default) {
        SceneElement(shape = PrimitiveShape.Plane, modifier = modifier)
    }
}

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

private val LocalSceneContentScope = compositionLocalOf<SceneContentScope?> { null }

@Composable
internal fun SceneElement(
    shape: PrimitiveShape,
    modifier: Modifier3D = Modifier3D.Default,
) {
    val sceneScope = LocalSceneContentScope.current
        ?: error("Element(...) must be called inside Scene { ... } content.")
    sceneScope.add(shape, modifier)
}

@Composable
internal fun rememberSceneGraph(content: @Composable () -> Unit): List<SceneNode> {
    val scope = remember { SceneContentScope(SceneBuilder()) }
    scope.reset()
    CompositionLocalProvider(LocalSceneContentScope provides scope) {
        content()
    }
    return scope.build()
}

@Composable
fun Scene(
    modifier: Modifier = Modifier,
    cameraState: CameraState = rememberCameraState(),
    gestures: SceneGestures = Gestures.orbit(),
    content: @Composable () -> Unit,
) {
    Scene(
        modifier = modifier,
        cameraState = cameraState,
        gestures = gestures,
        renderHostFactory = DefaultSceneRenderHostFactory,
        content = content,
    )
}

/**
 * Internal host-injection entry point for compose module tests.
 * Runtime callers use the public [Scene] overload; renderer infrastructure stays outside the
 * source-level public API.
 */
@Composable
internal fun Scene(
    modifier: Modifier = Modifier,
    cameraState: CameraState = rememberCameraState(),
    gestures: SceneGestures = Gestures.orbit(),
    renderHostFactory: SceneRenderHostFactory = DefaultSceneRenderHostFactory,
    content: @Composable () -> Unit,
) {
    val sceneNodes = rememberSceneGraph(content)
    val renderableNodes = sceneNodes.map(SceneNode::toRenderableNode)
    val cameraSnapshot = cameraState.snapshot()
    val renderHostHolder = remember { SceneRenderHostHolder() }

    var viewportSize by remember { mutableStateOf(IntSize.Zero) }

    AndroidView(
        modifier = modifier
            .onSizeChanged { viewportSize = it }
            .sceneGestureInput(cameraState, gestures, sceneNodes, viewportSize),
        factory = { context ->
            renderHostFactory.create(context).also { host ->
                renderHostHolder.host = host
                host.renderSceneFrame(renderableNodes, cameraSnapshot)
            }.view
        },
        update = {
            renderHostHolder.host?.renderSceneFrame(renderableNodes, cameraSnapshot)
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

    val gestureState = SceneGestureInputState()
    return pointerInteropFilter { event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                gestureState.onDown(event.x, event.y)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                val rawDelta = gestureState.onMove(
                    pointers = event.pointerPositions(),
                    orbitEnabled = gestures.orbitEnabled,
                    zoomEnabled = gestures.zoomEnabled,
                )
                rawDelta.scaleDelta?.let(cameraState::zoomBy)
                rawDelta.orbitDeltaPixels?.let { orbitDelta ->
                    val delta = resolveOrbitGestureDelta(
                        dx = orbitDelta.dx,
                        dy = orbitDelta.dy,
                        cameraZoom = cameraState.zoom,
                        sceneNodes = sceneNodes,
                        viewportSize = viewportSize,
                        sensitivity = gestures.orbitSensitivity,
                    )
                    cameraState.orbitBy(delta.yawDegrees, delta.pitchDegrees)
                }
                true
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                gestureState.onPointerDown(
                    pointers = event.pointerPositions(),
                    zoomEnabled = gestures.zoomEnabled,
                )
                true
            }
            MotionEvent.ACTION_POINTER_UP -> {
                gestureState.onPointerUp(
                    pointers = event.pointerPositions(),
                    actionIndex = event.actionIndex,
                    zoomEnabled = gestures.zoomEnabled,
                )
                true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                gestureState.reset()
                true
            }
            else -> true
        }
    }
}

internal data class PointerPosition(
    val x: Float,
    val y: Float,
)

internal data class OrbitGestureDeltaPixels(
    val dx: Float,
    val dy: Float,
)

internal data class RawSceneGestureDelta(
    val orbitDeltaPixels: OrbitGestureDeltaPixels? = null,
    val scaleDelta: Float? = null,
)

internal class SceneGestureInputState {
    private var lastX = 0f
    private var lastY = 0f
    private var lastPointerCount = 0
    private var lastPinchDistance = 0f

    fun onDown(x: Float, y: Float) {
        resetSinglePointer(x, y)
    }

    fun onMove(
        pointers: List<PointerPosition>,
        orbitEnabled: Boolean,
        zoomEnabled: Boolean,
    ): RawSceneGestureDelta {
        return when {
            pointers.size >= 2 && zoomEnabled -> {
                val distance = pointers.pinchDistance()
                val scaleDelta = if (lastPointerCount >= 2 && lastPinchDistance > 0f) {
                    resolvePinchZoomScaleDelta(
                        currentDistance = distance,
                        previousDistance = lastPinchDistance,
                    )
                } else {
                    null
                }
                lastPinchDistance = distance
                lastPointerCount = pointers.size
                RawSceneGestureDelta(scaleDelta = scaleDelta)
            }
            pointers.size == 1 && orbitEnabled -> {
                val pointer = pointers.first()
                val orbitDelta = if (lastPointerCount == 1) {
                    OrbitGestureDeltaPixels(
                        dx = pointer.x - lastX,
                        dy = pointer.y - lastY,
                    )
                } else {
                    null
                }
                resetSinglePointer(pointer.x, pointer.y)
                RawSceneGestureDelta(orbitDeltaPixels = orbitDelta)
            }
            else -> RawSceneGestureDelta()
        }
    }

    fun onPointerDown(
        pointers: List<PointerPosition>,
        zoomEnabled: Boolean,
    ) {
        lastPointerCount = pointers.size
        lastPinchDistance = if (pointers.size >= 2 && zoomEnabled) pointers.pinchDistance() else 0f
    }

    fun onPointerUp(
        pointers: List<PointerPosition>,
        actionIndex: Int,
        zoomEnabled: Boolean,
    ) {
        val remainingPointers = pointers.filterIndexed { index, _ -> index != actionIndex }
        when {
            remainingPointers.size == 1 -> {
                val remainingPointer = remainingPointers.first()
                resetSinglePointer(remainingPointer.x, remainingPointer.y)
            }
            remainingPointers.size >= 2 && zoomEnabled -> {
                lastPointerCount = remainingPointers.size
                lastPinchDistance = remainingPointers.pinchDistance()
            }
            else -> reset()
        }
    }

    fun reset() {
        lastPointerCount = 0
        lastPinchDistance = 0f
    }

    private fun resetSinglePointer(x: Float, y: Float) {
        lastX = x
        lastY = y
        lastPointerCount = 1
        lastPinchDistance = 0f
    }
}

private fun MotionEvent.pointerPositions(): List<PointerPosition> = List(pointerCount) { index ->
    PointerPosition(getX(index), getY(index))
}


private fun List<PointerPosition>.pinchDistance(): Float {
    if (size < 2) return 0f
    return pointerDistance(
        firstX = this[0].x,
        firstY = this[0].y,
        secondX = this[1].x,
        secondY = this[1].y,
    )
}

internal fun pointerDistance(
    firstX: Float,
    firstY: Float,
    secondX: Float,
    secondY: Float,
): Float {
    val dx = secondX - firstX
    val dy = secondY - firstY
    return sqrt(dx * dx + dy * dy)
}

/**
 * Converts pinch distance changes to visual magnification deltas. Increasing finger distance
 * (pinch out) returns a value greater than `1f`; decreasing distance (pinch in) returns less than
 * `1f`.
 */
internal fun resolvePinchZoomScaleDelta(
    currentDistance: Float,
    previousDistance: Float,
): Float {
    if (!currentDistance.isFinite() || !previousDistance.isFinite()) return 1f
    if (currentDistance <= 0f || previousDistance <= 0f) return 1f

    return (currentDistance / previousDistance).coerceIn(MinPinchScaleDeltaPerEvent, MaxPinchScaleDeltaPerEvent)
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

internal fun Modifier3D.toModelMatrix(): FloatArray {
    val resolvedSize = size ?: scale
    // Core #1 model composition order: T * Rz * Ry * Rx * S (OpenGL column-major).
    return translationMatrix(position)
        .multiply(rotationZMatrix(rotation.z))
        .multiply(rotationYMatrix(rotation.y))
        .multiply(rotationXMatrix(rotation.x))
        .multiply(scaleMatrix(resolvedSize))
}

private fun PrimitiveShape.defaultMaterial(): MaterialData = when (this) {
    PrimitiveShape.Cube -> MaterialData(0.95f, 0.35f, 0.20f)
    PrimitiveShape.Sphere -> MaterialData(0.25f, 0.65f, 1.0f)
    PrimitiveShape.Plane -> MaterialData(0.35f, 0.42f, 0.48f)
}

private fun identityMatrix(): FloatArray = FloatArray(16) { index -> if (index % 5 == 0) 1f else 0f }

private fun translationMatrix(position: Vec3Distance): FloatArray = identityMatrix().apply {
    this[12] = position.x.meters
    this[13] = position.y.meters
    this[14] = position.z.meters
}

private fun scaleMatrix(scale: Vec3Distance): FloatArray = identityMatrix().apply {
    this[0] = scale.x.meters
    this[5] = scale.y.meters
    this[10] = scale.z.meters
}

private fun rotationXMatrix(angle: Angle?): FloatArray {
    val radians = angle?.radians ?: 0f
    val cosValue = cos(radians)
    val sinValue = sin(radians)
    return identityMatrix().apply {
        this[5] = cosValue
        this[6] = sinValue
        this[9] = -sinValue
        this[10] = cosValue
    }
}

private fun rotationYMatrix(angle: Angle?): FloatArray {
    val radians = angle?.radians ?: 0f
    val cosValue = cos(radians)
    val sinValue = sin(radians)
    return identityMatrix().apply {
        this[0] = cosValue
        this[2] = -sinValue
        this[8] = sinValue
        this[10] = cosValue
    }
}

private fun rotationZMatrix(angle: Angle?): FloatArray {
    val radians = angle?.radians ?: 0f
    val cosValue = cos(radians)
    val sinValue = sin(radians)
    return identityMatrix().apply {
        this[0] = cosValue
        this[1] = sinValue
        this[4] = -sinValue
        this[5] = cosValue
    }
}

private fun FloatArray.multiply(other: FloatArray): FloatArray {
    val result = FloatArray(16)
    for (column in 0 until 4) {
        for (row in 0 until 4) {
            var value = 0f
            for (index in 0 until 4) {
                value += this[index * 4 + row] * other[column * 4 + index]
            }
            result[column * 4 + row] = value
        }
    }
    return result
}

private fun Angle.toDegrees(): Float = (radians * 180f / PI.toFloat())
private fun lerp(start: Float, stop: Float, fraction: Float): Float = start + (stop - start) * fraction

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
private const val MinPinchScaleDeltaPerEvent = 0.92f
private const val MaxPinchScaleDeltaPerEvent = 1.08f