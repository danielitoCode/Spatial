package com.elitec.spatial_compose.core

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

private const val MinPinchScaleDeltaPerEvent = 0.92f
private const val MaxPinchScaleDeltaPerEvent = 1.08f