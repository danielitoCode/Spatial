package com.elitec.spatial_compose.core

import kotlin.math.sqrt

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
