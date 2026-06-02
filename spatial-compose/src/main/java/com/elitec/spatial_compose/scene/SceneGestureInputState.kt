package com.elitec.spatial_compose.scene

import com.elitec.spatial_compose.core.OrbitGestureDeltaPixels
import com.elitec.spatial_compose.core.PointerPosition
import com.elitec.spatial_compose.core.RawSceneGestureDelta
import com.elitec.spatial_compose.core.resolvePinchZoomScaleDelta

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