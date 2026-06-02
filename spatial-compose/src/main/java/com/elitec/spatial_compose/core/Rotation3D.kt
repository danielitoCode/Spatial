package com.elitec.spatial_compose.core

import androidx.compose.runtime.Immutable
import com.elitec.spatial_units.Angle

@Immutable
internal data class Rotation3D(
    val x: Angle? = null,
    val y: Angle? = null,
    val z: Angle? = null,
)