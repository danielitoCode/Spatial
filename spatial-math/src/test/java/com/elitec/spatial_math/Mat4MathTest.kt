package com.elitec.spatial_math

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Mat4MathTest {

    @Test
    fun identityMultipliedByIdentityIsIdentity() {
        val identity = Mat4Math.identity()
        val result = Mat4Math.multiply(identity, identity)
        assertArrayEqualsWithTolerance(identity, result)
    }

    @Test
    fun multiplyIsNotCommutativeButAssociativeWithIdentity() {
        val m = Mat4Math.perspective(45f, 1f, 0.1f, 100f)
        val identity = Mat4Math.identity()
        assertArrayEqualsWithTolerance(m, Mat4Math.multiply(m, identity))
        assertArrayEqualsWithTolerance(m, Mat4Math.multiply(identity, m))
    }

    @Test
    fun orbitEyePositionAtZeroYawPitchLiesOnPositiveZAxis() {
        val eye = Mat4Math.orbitEyePosition(yawDegrees = 0f, pitchDegrees = 0f, distance = 10f)
        assertEquals(0f, eye[0], 0.0001f)
        assertEquals(0f, eye[1], 0.0001f)
        assertEquals(10f, eye[2], 0.0001f)
    }

    @Test
    fun orbitEyePositionAtNinetyYawLiesOnPositiveXAxis() {
        val eye = Mat4Math.orbitEyePosition(yawDegrees = 90f, pitchDegrees = 0f, distance = 10f)
        assertEquals(10f, eye[0], 0.001f)
        assertEquals(0f, eye[1], 0.001f)
        assertEquals(0f, eye[2], 0.001f)
    }

    @Test
    fun perspectiveProducesFiniteValuesForValidInputs() {
        val projection = Mat4Math.perspective(45f, 16f / 9f, 0.1f, 100f)
        assertTrue(projection.all { it.isFinite() })
    }

    @Test
    fun perspectiveFallsBackToSquareAspectWhenInvalid() {
        val invalidAspect = Mat4Math.perspective(45f, 0f, 0.1f, 100f)
        val squareAspect = Mat4Math.perspective(45f, 1f, 0.1f, 100f)
        assertArrayEqualsWithTolerance(squareAspect, invalidAspect)
    }

    @Test
    fun lookAtFromPositiveZTowardsOriginIsFinite() {
        val view = Mat4Math.lookAt(
            eyeX = 0f, eyeY = 0f, eyeZ = 10f,
            centerX = 0f, centerY = 0f, centerZ = 0f,
            upX = 0f, upY = 1f, upZ = 0f,
        )
        assertTrue(view.all { it.isFinite() })
    }

    private fun assertArrayEqualsWithTolerance(expected: FloatArray, actual: FloatArray, tolerance: Float = 0.0001f) {
        assertEquals(expected.size, actual.size)
        for (i in expected.indices) {
            assertEquals("mismatch at index $i", expected[i], actual[i], tolerance)
        }
    }
}
