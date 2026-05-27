package com.elitec.spatial_core.render

data class FrameSnapshot(
    val frameTimeNanos: Long,
    val viewProjection: Mat4 = Mat4.identity(),
    val cameraPosition: Vec3 = Vec3.ZERO,
    val clearColor: Color4 = Color4.BLACK,
    val resources: StableFrameResources = StableFrameResources.empty(),
)