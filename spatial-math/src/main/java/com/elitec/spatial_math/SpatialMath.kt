package com.elitec.spatial_math

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class SpatialMath {
}

/**
 * Pure-Kotlin 4x4 column-major matrix math shared by every module that needs to compute a
 * view/projection pair without depending on `android.opengl.Matrix` (spatial-math is a plain JVM
 * module so it can be unit tested off-device and reused by spatial-core/spatial-runtime).
 *
 * All matrices are stored as a 16-element [FloatArray] in OpenGL's column-major layout, i.e.
 * `m[column * 4 + row]`, matching `android.opengl.Matrix` conventions so results are drop-in
 * compatible with the renderer.
 */
object Mat4Math {

    fun identity(): FloatArray = FloatArray(16).also {
        it[0] = 1f; it[5] = 1f; it[10] = 1f; it[15] = 1f
    }

    /** Column-major `a * b`, matching `android.opengl.Matrix.multiplyMM` semantics. */
    fun multiply(a: FloatArray, b: FloatArray): FloatArray {
        val result = FloatArray(16)
        for (column in 0 until 4) {
            for (row in 0 until 4) {
                var sum = 0f
                for (k in 0 until 4) {
                    sum += a[k * 4 + row] * b[column * 4 + k]
                }
                result[column * 4 + row] = sum
            }
        }
        return result
    }

    /** Equivalent to `android.opengl.Matrix.perspectiveM`. */
    fun perspective(fovYDegrees: Float, aspect: Float, near: Float, far: Float): FloatArray {
        val safeAspect = if (aspect.isFinite() && aspect > 0f) aspect else 1f
        val f = 1f / tan(Math.toRadians(fovYDegrees.toDouble() / 2.0)).toFloat()
        val rangeInv = 1f / (near - far)
        return floatArrayOf(
            f / safeAspect, 0f, 0f, 0f,
            0f, f, 0f, 0f,
            0f, 0f, (far + near) * rangeInv, -1f,
            0f, 0f, 2f * far * near * rangeInv, 0f,
        )
    }

    /** Equivalent to `android.opengl.Matrix.setLookAtM`. */
    fun lookAt(
        eyeX: Float, eyeY: Float, eyeZ: Float,
        centerX: Float, centerY: Float, centerZ: Float,
        upX: Float, upY: Float, upZ: Float,
    ): FloatArray {
        var fx = centerX - eyeX
        var fy = centerY - eyeY
        var fz = centerZ - eyeZ
        val fLength = sqrt(fx * fx + fy * fy + fz * fz).let { if (it == 0f) 1f else it }
        fx /= fLength; fy /= fLength; fz /= fLength

        var sx = fy * upZ - fz * upY
        var sy = fz * upX - fx * upZ
        var sz = fx * upY - fy * upX
        val sLength = sqrt(sx * sx + sy * sy + sz * sz).let { if (it == 0f) 1f else it }
        sx /= sLength; sy /= sLength; sz /= sLength

        val ux = sy * fz - sz * fy
        val uy = sz * fx - sx * fz
        val uz = sx * fy - sy * fx

        return floatArrayOf(
            sx, ux, -fx, 0f,
            sy, uy, -fy, 0f,
            sz, uz, -fz, 0f,
            -(sx * eyeX + sy * eyeY + sz * eyeZ),
            -(ux * eyeX + uy * eyeY + uz * eyeZ),
            (fx * eyeX + fy * eyeY + fz * eyeZ),
            1f,
        )
    }

    /**
     * Orbit-camera eye position around the origin, matching the convention used by the GL
     * renderer: yaw/pitch are degrees, distance is the radial distance from the origin.
     */
    fun orbitEyePosition(yawDegrees: Float, pitchDegrees: Float, distance: Float): FloatArray {
        val yawRad = Math.toRadians(yawDegrees.toDouble())
        val pitchRad = Math.toRadians(pitchDegrees.toDouble())
        val x = (distance * sin(yawRad) * cos(pitchRad)).toFloat()
        val y = (distance * sin(pitchRad)).toFloat()
        val z = (distance * cos(yawRad) * cos(pitchRad)).toFloat()
        return floatArrayOf(x, y, z)
    }
}