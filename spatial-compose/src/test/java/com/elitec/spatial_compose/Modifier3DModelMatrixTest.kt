package com.elitec.spatial_compose

import com.elitec.spatial_compose.modifier.Modifier3D
import com.elitec.spatial_compose.modifier.toModelMatrix
import com.elitec.spatial_units.deg
import com.elitec.spatial_units.meters
import org.junit.Assert.assertEquals
import org.junit.Test

class Modifier3DModelMatrixTest {

    @Test
    fun `model matrix keeps translation in final column`() {
        val matrix = Modifier3D.Default
            .position(1.5f.meters, (-2f).meters, 3.25f.meters)
            .toModelMatrix()

        assertMatrixEquals(
            floatArrayOf(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                1.5f, -2f, 3.25f, 1f,
            ),
            matrix,
        )
    }

    @Test
    fun `model matrix applies scale from size when present`() {
        val matrix = Modifier3D.Default
            .scale(9f.meters, 9f.meters, 9f.meters)
            .size(2f.meters, 3f.meters, 4f.meters)
            .toModelMatrix()

        assertMatrixEquals(
            floatArrayOf(
                2f, 0f, 0f, 0f,
                0f, 3f, 0f, 0f,
                0f, 0f, 4f, 0f,
                0f, 0f, 0f, 1f,
            ),
            matrix,
        )
    }

    @Test
    fun `model matrix composes translation rotation z and scale in core order`() {
        val matrix = Modifier3D.Default
            .size(2f.meters, 3f.meters, 4f.meters)
            .rotateZ(90f.deg)
            .position(10f.meters, 20f.meters, 30f.meters)
            .toModelMatrix()

        assertMatrixEquals(
            floatArrayOf(
                0f, 2f, 0f, 0f,
                -3f, 0f, 0f, 0f,
                0f, 0f, 4f, 0f,
                10f, 20f, 30f, 1f,
            ),
            matrix,
        )
    }

    @Test
    fun `model matrix supports x and y rotations`() {
        val rotateX = Modifier3D.Default.rotateX(90f.deg).toModelMatrix()
        val rotateY = Modifier3D.Default.rotateY(90f.deg).toModelMatrix()

        assertMatrixEquals(
            floatArrayOf(
                1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, -1f, 0f, 0f,
                0f, 0f, 0f, 1f,
            ),
            rotateX,
        )
        assertMatrixEquals(
            floatArrayOf(
                0f, 0f, -1f, 0f,
                0f, 1f, 0f, 0f,
                1f, 0f, 0f, 0f,
                0f, 0f, 0f, 1f,
            ),
            rotateY,
        )
    }

    @Test
    fun `combined rotation matches extrinsic X then Y then Z about fixed world axes`() {
        // Item 3.1 audit (2026-07-08): regression coverage for the KDoc claim on toModelMatrix().
        // Simulates "extrinsic X, then Y, then Z" by hand: rotate a vector about the FIXED world X
        // axis, then about the FIXED world Y axis, then about the FIXED world Z axis - never about
        // an intermediate, already-rotated axis - and compares against what toModelMatrix() produces
        // for the same angles. If this ever regresses to a genuinely different convention (e.g. an
        // intrinsic X->Y->Z sequence, which is a different matrix), this test will catch it.
        val xDeg = 30f
        val yDeg = 40f
        val zDeg = 50f

        val matrix = Modifier3D.Default
            .rotateX(xDeg.deg)
            .rotateY(yDeg.deg)
            .rotateZ(zDeg.deg)
            .toModelMatrix()

        val v = floatArrayOf(1f, 0f, 0f)
        val expected = extrinsicXThenYThenZ(v, xDeg, yDeg, zDeg)
        val actual = matrix.transformPoint(v)

        assertEquals("x", expected[0], actual[0], 0.0001f)
        assertEquals("y", expected[1], actual[1], 0.0001f)
        assertEquals("z", expected[2], actual[2], 0.0001f)
    }

    /** Hand-rolled "extrinsic, fixed-world-axes" rotation, independent of `toModelMatrix()`. */
    private fun extrinsicXThenYThenZ(v: FloatArray, xDeg: Float, yDeg: Float, zDeg: Float): FloatArray {
        fun rotateAboutFixedX(p: FloatArray, deg: Float): FloatArray {
            val r = Math.toRadians(deg.toDouble())
            val c = kotlin.math.cos(r).toFloat(); val s = kotlin.math.sin(r).toFloat()
            return floatArrayOf(p[0], p[1] * c - p[2] * s, p[1] * s + p[2] * c)
        }
        fun rotateAboutFixedY(p: FloatArray, deg: Float): FloatArray {
            val r = Math.toRadians(deg.toDouble())
            val c = kotlin.math.cos(r).toFloat(); val s = kotlin.math.sin(r).toFloat()
            return floatArrayOf(p[0] * c + p[2] * s, p[1], -p[0] * s + p[2] * c)
        }
        fun rotateAboutFixedZ(p: FloatArray, deg: Float): FloatArray {
            val r = Math.toRadians(deg.toDouble())
            val c = kotlin.math.cos(r).toFloat(); val s = kotlin.math.sin(r).toFloat()
            return floatArrayOf(p[0] * c - p[1] * s, p[0] * s + p[1] * c, p[2])
        }
        val afterX = rotateAboutFixedX(v, xDeg)
        val afterY = rotateAboutFixedY(afterX, yDeg)
        return rotateAboutFixedZ(afterY, zDeg)
    }

    /** Applies a column-major 4x4 matrix to a 3D point (implicit w=1, translation ignored for w=0 test vectors is fine here since we only care about the rotation block and these points are direction-like). */
    private fun FloatArray.transformPoint(p: FloatArray): FloatArray {
        val x = this[0] * p[0] + this[4] * p[1] + this[8] * p[2]
        val y = this[1] * p[0] + this[5] * p[1] + this[9] * p[2]
        val z = this[2] * p[0] + this[6] * p[1] + this[10] * p[2]
        return floatArrayOf(x, y, z)
    }

    private fun assertMatrixEquals(
        expected: FloatArray,
        actual: FloatArray,
        tolerance: Float = 0.0001f,
    ) {
        assertEquals("matrix size", expected.size, actual.size)
        expected.forEachIndexed { index, expectedValue ->
            assertEquals("matrix[$index]", expectedValue, actual[index], tolerance)
        }
    }
}