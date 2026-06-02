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