package com.elitec.spatial_compose.modifier

import com.elitec.spatial_compose.core.Vec3Distance
import com.elitec.spatial_units.Angle
import kotlin.math.cos
import kotlin.math.sin

/**
 * Core #1 model matrix composition order (item 3.1): `M = T * Rz * Ry * Rx * S`.
 *
 * Matrices are OpenGL-style column-major and column-vectors are transformed as `M * v`, so this
 * product is applied to a vertex right-to-left: scale first, then rotate around X, then Y, then Z,
 * then translate.
 *
 * **Correction (re-verified 2026-07-08, Claude):** an earlier version of this KDoc claimed this was
 * an *intrinsic* X→Y→Z sequence that only "coincidentally" resembled extrinsic Z→Y→X for special
 * cases. That was wrong, and was corrected after a numeric check (see
 * `CORE1_STABILITY.md`, item 3.1 audit notes, for the derivation). The precise, always-true fact is:
 *
 * `Rz(γ)·Ry(β)·Rx(α)` is **exactly**, for every combination of angles (not just special cases):
 * - an **extrinsic** rotation about the *fixed world axes*, applied in the order X(α), then Y(β),
 *   then Z(γ) - matching the literal left-to-right reading of "rotate by X, then Y, then Z" - and,
 *   equivalently (same matrix, standard Euler-angle identity),
 * - an **intrinsic** (body-frame) rotation applied in the *reverse* order Z(γ), then Y(β), then
 *   X(α), about the body's own progressively-rotated axes.
 *
 * These two descriptions are not approximations of each other; they are the same linear map. What
 * is genuinely NOT equivalent to this matrix is "intrinsic X, then Y, then Z" (body axes, in that
 * order) - that would instead require the reversed product `Rx·Ry·Rz`.
 *
 * Practical consequence for consumers composing `Modifier3D.rotation(x, y, z)`: think of it as
 * "extrinsic X, then Y, then Z about the world's fixed axes" (the simplest mental model, and the one
 * that matches reading the matrix product left-to-right in application order). Gimbal lock occurs
 * when the *middle* (Y) rotation approaches ±90°, at which point the X and Z rotations align onto
 * the same effective axis and one degree of freedom is lost - true for both equivalent descriptions
 * above, since they are the same transform.
 */
internal fun Modifier3D.toModelMatrix(): FloatArray {
    val resolvedSize = size ?: scale
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