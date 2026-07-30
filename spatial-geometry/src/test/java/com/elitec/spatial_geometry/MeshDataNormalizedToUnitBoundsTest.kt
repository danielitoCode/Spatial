package com.elitec.spatial_geometry

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class MeshDataNormalizedToUnitBoundsTest {

    @Test
    fun largeAsymmetricMesh_fitsWithinUnitBounds_centeredAtOrigin() {
        // Mirrors the real-world scale of app/src/main/res/raw/sample_model.glb, whose raw
        // POSITION accessors span roughly -400..400 (and up to -600..600 on some axes) - the
        // exact class of mesh that renders invisible without this normalization.
        val mesh = MeshData(
            vertices = floatArrayOf(
                -400f, -400f, -400f,
                400f, 400f, 400f,
                0f, -600f, 0f,
            ),
        )

        val normalized = mesh.normalizedToUnitBounds()

        // Largest extent (Y: -600..600 = 1200) must map to exactly 1.0.
        val ys = listOf(normalized.vertices[1], normalized.vertices[4], normalized.vertices[7])
        assertEquals(1f, ys.max() - ys.min(), 1e-5f)

        // Centered: min/max must be symmetric around 0 for every axis.
        for (axis in 0..2) {
            val values = listOf(
                normalized.vertices[axis],
                normalized.vertices[3 + axis],
                normalized.vertices[6 + axis],
            )
            assertEquals(0f, values.min() + values.max(), 1e-5f)
        }
    }

    @Test
    fun alreadyUnitScaleMesh_isEffectivelyUnchanged() {
        // A mesh already centered at the origin spanning exactly -0.5..0.5 (the built-in
        // primitive convention) should come out (numerically) the same.
        val mesh = MeshData(
            vertices = floatArrayOf(
                -0.5f, -0.5f, -0.5f,
                0.5f, 0.5f, 0.5f,
            ),
        )

        val normalized = mesh.normalizedToUnitBounds()

        assertArrayEquals(mesh.vertices, normalized.vertices, 1e-5f)
    }

    @Test
    fun normalsTexCoordsIndicesAndMaterial_areUntouched() {
        val mesh = MeshData(
            vertices = floatArrayOf(-100f, 0f, 0f, 100f, 0f, 0f, 0f, 100f, 0f),
            indices = intArrayOf(0, 1, 2),
            normals = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f),
            texCoords = floatArrayOf(0f, 0f, 1f, 0f, 0.5f, 1f),
        )

        val normalized = mesh.normalizedToUnitBounds()

        assertArrayEquals(mesh.indices, normalized.indices)
        assertArrayEquals(mesh.normals, normalized.normals, 1e-5f)
        assertArrayEquals(mesh.texCoords, normalized.texCoords, 1e-5f)
        assertEquals(mesh.material, normalized.material)
    }

    @Test
    fun emptyMesh_isReturnedUnchanged() {
        val mesh = MeshData(vertices = floatArrayOf())

        assertSame(mesh, mesh.normalizedToUnitBounds())
    }

    @Test
    fun degenerateSinglePointMesh_isReturnedUnchanged_notDividedByZero() {
        // All vertices at the same point -> zero extent. Must not throw or produce NaN/Infinity.
        val mesh = MeshData(
            vertices = floatArrayOf(5f, 5f, 5f, 5f, 5f, 5f),
            indices = intArrayOf(0, 1, 0),
        )

        val normalized = mesh.normalizedToUnitBounds()

        assertArrayEquals(mesh.vertices, normalized.vertices, 0f)
    }
}
