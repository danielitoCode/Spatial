package com.elitec.spatial_compose.scene

import com.elitec.spatial_compose.core.PointerPosition
import com.elitec.spatial_compose.core.pointerDistance

internal fun List<PointerPosition>.pinchDistance(): Float {
    if (size < 2) return 0f
    return pointerDistance(
        firstX = this[0].x,
        firstY = this[0].y,
        secondX = this[1].x,
        secondY = this[1].y,
    )
}