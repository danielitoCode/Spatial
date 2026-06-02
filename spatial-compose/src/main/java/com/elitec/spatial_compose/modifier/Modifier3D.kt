package com.elitec.spatial_compose.modifier

import androidx.compose.runtime.Immutable
import com.elitec.spatial_compose.core.Rotation3D
import com.elitec.spatial_compose.core.Vec3Distance
import com.elitec.spatial_units.Angle
import com.elitec.spatial_units.Distance
import com.elitec.spatial_units.meters

@Immutable
class Modifier3D internal constructor(
    internal val position: Vec3Distance = Vec3Distance(),
    internal val rotation: Rotation3D = Rotation3D(),
    internal val scale: Vec3Distance = Vec3Distance(1f.meters, 1f.meters, 1f.meters),
    internal val size: Vec3Distance? = null,
) {
    fun position(x: Float, y: Float, z: Float): Modifier3D = position(x.meters, y.meters, z.meters)
    fun position(x: Distance, y: Distance, z: Distance): Modifier3D = copy(position = Vec3Distance(x, y, z))
    fun rotateX(angle: Angle): Modifier3D = copy(rotation = rotation.copy(x = angle))
    fun rotateY(angle: Angle): Modifier3D = copy(rotation = rotation.copy(y = angle))
    fun rotateZ(angle: Angle): Modifier3D = copy(rotation = rotation.copy(z = angle))
    fun scale(x: Distance, y: Distance, z: Distance): Modifier3D = copy(scale = Vec3Distance(x, y, z))
    fun size(all: Distance): Modifier3D = copy(size = Vec3Distance(all, all, all))
    fun size(width: Distance, height: Distance, depth: Distance): Modifier3D = copy(size = Vec3Distance(width, height, depth))

    private fun copy(
        position: Vec3Distance = this.position,
        rotation: Rotation3D = this.rotation,
        scale: Vec3Distance = this.scale,
        size: Vec3Distance? = this.size,
    ): Modifier3D = Modifier3D(
        position = position,
        rotation = rotation,
        scale = scale,
        size = size,
    )

    companion object {
        val Default = Modifier3D()
    }
}