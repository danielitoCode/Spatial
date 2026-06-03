package com.elitec.spatial_compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.elitec.spatial_units.Angle
import com.elitec.spatial_units.deg
import com.elitec.spatial_compose.components.Scene as ComponentScene
import com.elitec.spatial_compose.core.Element as CoreElement
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

@Composable
public fun Scene(
    modifier: Modifier = Modifier,
    renderHostFactory: SceneRenderHostFactory,
    cameraState: CameraState = rememberCameraState(),
    gestures: SceneGestures = Gestures.orbit(),
    content: @Composable () -> Unit,
) {
    ComponentScene(
        modifier = modifier,
        renderHostFactory = renderHostFactory,
        cameraState = cameraState,
        gestures = gestures,
        content = content,
    )
}

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