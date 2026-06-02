package com.elitec.spatial_compose.modifier

import com.elitec.spatial_compose.core.Vec3Distance
import com.elitec.spatial_units.Angle
import kotlin.math.cos
import kotlin.math.sin

internal fun Modifier3D.toModelMatrix(): FloatArray {
    val resolvedSize = size ?: scale
    // Core #1 model composition order: T * Rz * Ry * Rx * S (OpenGL column-major).
    return translationMatrix(position)
        .multiply(rotationZMatrix(rotation.z))
        .multiply(rotationYMatrix(rotation.y))
        .multiply(rotationXMatrix(rotation.x))
        .multiply(scaleMatrix(resolvedSize))
}

private fun translationMatrix(position: Vec3Distance): FloatArray = identityMatrix().apply {
    this[12] = position.x.meters
    this[13] = position.y.meters
    this[14] = position.z.meters
}

private fun scaleMatrix(scale: Vec3Distance): FloatArray = identityMatrix().apply {
    this[0] = scale.x.meters
    this[5] = scale.y.meters
    this[10] = scale.z.meters
}

private fun rotationXMatrix(angle: Angle?): FloatArray {
    val radians = angle?.radians ?: 0f
    val cosValue = cos(radians)
    val sinValue = sin(radians)
    return identityMatrix().apply {
        this[5] = cosValue
        this[6] = sinValue
        this[9] = -sinValue
        this[10] = cosValue
    }
}

private fun rotationYMatrix(angle: Angle?): FloatArray {
    val radians = angle?.radians ?: 0f
    val cosValue = cos(radians)
    val sinValue = sin(radians)
    return identityMatrix().apply {
        this[0] = cosValue
        this[2] = -sinValue
        this[8] = sinValue
        this[10] = cosValue
    }
}

private fun rotationZMatrix(angle: Angle?): FloatArray {
    val radians = angle?.radians ?: 0f
    val cosValue = cos(radians)
    val sinValue = sin(radians)
    return identityMatrix().apply {
        this[0] = cosValue
        this[1] = sinValue
        this[4] = -sinValue
        this[5] = cosValue
    }
}

private fun identityMatrix(): FloatArray = FloatArray(16) { index -> if (index % 5 == 0) 1f else 0f }

private fun FloatArray.multiply(other: FloatArray): FloatArray {
    val result = FloatArray(16)
    for (column in 0 until 4) {
        for (row in 0 until 4) {
            var value = 0f
            for (index in 0 until 4) {
                value += this[index * 4 + row] * other[column * 4 + index]
            }
            result[column * 4 + row] = value
        }
    }
    return result
}