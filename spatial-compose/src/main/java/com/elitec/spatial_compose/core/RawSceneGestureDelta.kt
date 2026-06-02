package com.elitec.spatial_compose.core

internal data class RawSceneGestureDelta(
    val orbitDeltaPixels: OrbitGestureDeltaPixels? = null,
    val scaleDelta: Float? = null,
)