package com.elitec.spatial_compose.modifier

import android.view.MotionEvent
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.IntSize
import com.elitec.spatial_compose.core.resolveOrbitGestureDelta
import com.elitec.spatial_compose.motion.pointerPositions
import com.elitec.spatial_compose.scene.SceneGestureInputState
import com.elitec.spatial_compose.scene.SceneGestures
import com.elitec.spatial_compose.scene.SceneNode
import com.elitec.spatial_compose.state.CameraState
import com.elitec.spatial_compose.components.Scene

/**
 * Internal host-injection entry point for compose module tests.
 * Runtime callers use the public [Scene] overload; renderer infrastructure stays outside the
 * source-level public API.
 */
internal fun Modifier.sceneGestureInput(
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