package com.elitec.spatial_core.render

import com.elitec.spatial_core.camera.CameraSnapshot
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifica que el cálculo de matrices de Core #1 sea resiliente a valores extremos.
 * Previene el error de "pantalla negra" causado por valores NaN o Infinity en la
 * tubería de renderizado cuando la cámara alcanza ángulos o zooms límite.
 */
class CameraStabilityTest {

    @Test
    fun `zenith and nadir angles produce finite matrices`() {
        val frameTime = 1000L
        val aspect = 1.77f

        // Caso 1: Mirando exactamente desde el polo norte (Pitch 90)
        val zenithCamera = CameraSnapshot(yaw = 0f, pitch = 90f, zoom = 1f)
        val zenithSnapshot = buildOrbitFrameSnapshot(frameTime, zenithCamera, aspect)
        assertMatrixIsFinite(zenithSnapshot.viewProjection, "Zenith (Pitch 90)")

        // Caso 2: Mirando exactamente desde el polo sur (Pitch -90)
        val nadirCamera = CameraSnapshot(yaw = 0f, pitch = -90f, zoom = 1f)
        val nadirSnapshot = buildOrbitFrameSnapshot(frameTime, nadirCamera, aspect)
        assertMatrixIsFinite(nadirSnapshot.viewProjection, "Nadir (Pitch -90)")
    }

    @Test
    fun `invalid zoom values are handled gracefully`() {
        val frameTime = 1000L
        val aspect = 1.0f

        val testCases = listOf(
            CameraSnapshot(zoom = 0f),          // Zero
            CameraSnapshot(zoom = -1f),         // Negativo
            CameraSnapshot(zoom = Float.NaN),   // Not a Number
            CameraSnapshot(zoom = Float.POSITIVE_INFINITY) // Infinito
        )

        testCases.forEach { camera ->
            val snapshot = buildOrbitFrameSnapshot(frameTime, camera, aspect)
            assertMatrixIsFinite(snapshot.viewProjection, "Zoom inválido: ${camera.zoom}")
        }
    }

    @Test
    fun `extreme viewport aspect ratios do not break projection`() {
        val frameTime = 1000L
        val camera = CameraSnapshot(zoom = 1f)

        val testCases = listOf(0f, -1.5f, Float.NaN, Float.POSITIVE_INFINITY)

        testCases.forEach { aspect ->
            val snapshot = buildOrbitFrameSnapshot(frameTime, camera, aspect)
            assertMatrixIsFinite(snapshot.viewProjection, "Aspect Ratio inválido: $aspect")
        }
    }

    /**
     * Utilidad para asegurar que cada float dentro de la matriz 4x4 es un número válido.
     */
    private fun assertMatrixIsFinite(matrix: Mat4, context: String) {
        val values = matrix.toFloatArray()
        values.forEachIndexed { index, value ->
            assertTrue(
                "El valor en el índice $index no es finito ($value) en el contexto: $context",
                value.isFinite()
            )
        }
    }
}