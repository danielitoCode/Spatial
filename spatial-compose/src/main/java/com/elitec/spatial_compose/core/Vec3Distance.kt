package com.elitec.spatial_compose.core

import androidx.compose.runtime.Immutable
import com.elitec.spatial_units.Distance
import com.elitec.spatial_units.meters

@Immutable
internal data class Vec3Distance(
    val x: Distance = 0f.meters,
    val y: Distance = 0f.meters,
    val z: Distance = 0f.meters,
)