package com.elitec.spatial_compose.scene

import androidx.compose.runtime.Immutable

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