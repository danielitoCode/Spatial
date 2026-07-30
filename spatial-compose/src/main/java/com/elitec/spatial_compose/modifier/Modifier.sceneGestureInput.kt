package com.elitec.spatial_compose.modifier

import android.view.MotionEvent
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntSize
import com.elitec.spatial_compose.core.resolveOrbitGestureDelta
import com.elitec.spatial_compose.motion.pointerPositions
import com.elitec.spatial_compose.scene.SceneGestureInputState
import com.elitec.spatial_compose.scene.SceneGestures
import com.elitec.spatial_compose.scene.SceneNode
import com.elitec.spatial_compose.state.CameraState

/**
 * Internal host-injection entry point for scene orbit / pinch gestures.
 *
 * Task 2.1: pairs pointer DOWN/UP with [CameraState.beginGestureInteraction] /
 * [CameraState.endGestureInteraction] so auto-rotate yields during user orbit.
 *
 * When [Scene] sits inside a scrollable parent (e.g. LazyColumn), the parent would
 * otherwise intercept vertical MOVE events. We call [android.view.ViewParent.requestDisallowInterceptTouchEvent]
 * while the gesture is active so orbit/pinch reach this filter.
 */
internal fun Modifier.sceneGestureInput(
    cameraState: CameraState,
    gestures: SceneGestures,
    sceneNodes: List<SceneNode>,
    viewportSize: IntSize,
): Modifier {
    if (gestures.mode == SceneGestures.Mode.None) return this

    return composed {
        val gestureState = remember { SceneGestureInputState() }
        val hostView = LocalView.current
        pointerInteropFilter { event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    hostView.parent?.requestDisallowInterceptTouchEvent(true)
                    cameraState.beginGestureInteraction()
                    gestureState.onDown(event.x, event.y)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    hostView.parent?.requestDisallowInterceptTouchEvent(true)
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
                    hostView.parent?.requestDisallowInterceptTouchEvent(true)
                    cameraState.beginGestureInteraction()
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
                    cameraState.endGestureInteraction()
                    hostView.parent?.requestDisallowInterceptTouchEvent(false)
                    true
                }
                else -> true
            }
        }
    }
}
