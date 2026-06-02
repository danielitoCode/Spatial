package com.elitec.spatial_compose.motion

import android.view.MotionEvent
import com.elitec.spatial_compose.core.PointerPosition

internal fun MotionEvent.pointerPositions(): List<PointerPosition> = List(pointerCount) { index ->
    PointerPosition(getX(index), getY(index))
}