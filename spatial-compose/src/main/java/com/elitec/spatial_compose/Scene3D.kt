package com.elitec.spatial_compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.elitec.spatial_units.Angle
import com.elitec.spatial_units.deg
import com.elitec.spatial_compose.components.Scene as ComponentScene
import com.elitec.spatial_compose.core.Element as CoreElement
import com.elitec.spatial_compose.scene.ModelSceneElement as CoreModelSceneElement
import com.elitec.spatial_compose.modifier.Modifier3D as CoreModifier3D
import com.elitec.spatial_compose.motion.MotionSpec as CoreMotionSpec
import com.elitec.spatial_compose.scene.GestureSensitivity as CoreGestureSensitivity
import com.elitec.spatial_compose.scene.Gestures as CoreGestures
import com.elitec.spatial_compose.scene.SceneGestures as CoreSceneGestures
import com.elitec.spatial_compose.state.CameraState as CoreCameraState
import com.elitec.spatial_compose.state.rememberCameraState as rememberCoreCameraState
import com.elitec.spatial_compose.scene.SceneRenderHostFactory as CoreSceneRenderHostFactory

/**
 * Core #1 exposes its Compose scene API from the root `com.elitec.spatial_compose` package.
 *
 * Implementation details remain organized in subpackages, while app code imports this root package
 * as the stable public surface.
 */
@Immutable
public object Element {
    @Composable
    public fun Cube(modifier: Modifier3D = Modifier3D.Default) {
        CoreElement.Cube(modifier = modifier)
    }

    @Composable
    public fun Sphere(modifier: Modifier3D = Modifier3D.Default) {
        CoreElement.Sphere(modifier = modifier)
    }

    @Composable
    public fun Plane(modifier: Modifier3D = Modifier3D.Default) {
        CoreElement.Plane(modifier = modifier)
    }

    /**
     * Renders a 3D model loaded from an external resource (e.g., a GLB file from `/res/raw/`).
     *
     * Usage:
     * ```kotlin
     * Element.Model(
     *     model = ModelResource.fromRawResource(R.raw.my_model),
     *     modifier = Modifier3D.Default.size(2f.meters)
     * )
     * ```
     *
     * @param model The [ModelResource] identifying the 3D asset to load.
     * @param modifier The [Modifier3D] to position, rotate, or scale the model in the scene.
     */
    @Composable
    public fun Model(
        model: ModelResource,
        modifier: Modifier3D = Modifier3D.Default,
    ) {
        CoreModelSceneElement(model = model, modifier = modifier)
    }
}

/** Root-package export for 3D element modifiers. */
public typealias Modifier3D = CoreModifier3D

public object Gestures {
    public fun none(): SceneGestures = CoreGestures.none()
    public fun orbit(sensitivity: GestureSensitivity = CoreGestureSensitivity.Adaptive): SceneGestures =
        CoreGestures.orbit(sensitivity = sensitivity)

    public fun orbitAndZoom(sensitivity: GestureSensitivity = CoreGestureSensitivity.Adaptive): SceneGestures =
        CoreGestures.orbitAndZoom(sensitivity = sensitivity)
}

/** Root-package export for scene gesture configuration. */
public typealias SceneGestures = CoreSceneGestures

/** Root-package export for orbit gesture sensitivity policies. */
public typealias GestureSensitivity = CoreGestureSensitivity

/** Root-package export for camera state. */
public typealias CameraState = CoreCameraState

/** Root-package contract for supplying the Android host that renders a Scene. */
public typealias SceneRenderHostFactory = CoreSceneRenderHostFactory

/** Root-package export for camera animation motion policies. */
public typealias MotionSpec = CoreMotionSpec

/**
 * Renders a 3D scene as a Compose element.
 *
 * @param contentScale Initial visual scale of the scene content relative to the default camera
 * distance. `1f` (100%) is the default orbital view; `0.5f` makes content appear at half size
 * (camera zoomed out); `2f` makes content appear at double size (camera zoomed in).
 * This value seeds the camera's initial zoom via [rememberCameraState] and can be adjusted
 * afterwards through [cameraState] or gestures.
 *
 * **Important:** If you supply an explicit [cameraState], [contentScale] is ignored — your
 * camera's own zoom takes precedence. Only provide [contentScale] when relying on the default
 * internal camera.
 */
@Composable
public fun Scene(
    modifier: Modifier = Modifier,
    renderHostFactory: SceneRenderHostFactory,
    contentScale: Float = 1f,
    cameraState: CameraState = rememberCameraState(zoom = contentScale.toInitialZoom()),
    gestures: SceneGestures = Gestures.orbit(),
    backgroundColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Transparent,
    content: @Composable () -> Unit,
) {
    ComponentScene(
        modifier = modifier,
        renderHostFactory = renderHostFactory,
        cameraState = cameraState,
        gestures = gestures,
        backgroundColor = backgroundColor,
        content = content,
    )
}

/**
 * Maps a public [contentScale] fraction (where 1f = default orbital view) to the internal
 * [CameraSnapshot.zoom] value used by the orbit camera.
 *
 * The orbit camera treats zoom as a *visual magnification factor*, so `contentScale` maps
 * directly to zoom — a value of `0.5f` halves the apparent size of objects (camera moves
 * farther out) and `2f` doubles it (camera moves closer in). The result is clamped to the
 * camera's valid zoom range [MIN_ZOOM, MAX_ZOOM].
 */
private fun Float.toInitialZoom(): Float =
    coerceIn(com.elitec.spatial_core.camera.CameraSnapshot.MIN_ZOOM,
              com.elitec.spatial_core.camera.CameraSnapshot.MAX_ZOOM)

@Composable
public fun rememberCameraState(
    yaw: Angle = 0f.deg,
    pitch: Angle = 0f.deg,
    zoom: Float = 1f,
): CameraState = rememberCoreCameraState(
    yaw = yaw,
    pitch = pitch,
    zoom = zoom,
)