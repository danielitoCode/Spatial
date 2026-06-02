package com.elitec.spatial_compose.scene

import androidx.compose.runtime.Immutable

@ConsistentCopyVisibility
@Immutable
data class SceneGestures internal constructor(
    val mode: Mode,
    val orbitSensitivity: GestureSensitivity = GestureSensitivity.Adaptive,
) {
    val orbitEnabled: Boolean get() = mode.orbitEnabled
    val zoomEnabled: Boolean get() = mode.zoomEnabled

    enum class Mode(
        internal val orbitEnabled: Boolean,
        internal val zoomEnabled: Boolean,
    ) {
        None(orbitEnabled = false, zoomEnabled = false),
        Orbit(orbitEnabled = true, zoomEnabled = false),
        OrbitAndZoom(orbitEnabled = true, zoomEnabled = true),
    }
}