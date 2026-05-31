package com.elitec.spatial_renderer

import com.elitec.spatial_renderer.gl.MeshData
import com.elitec.spatial_renderer.gl.MeshDrawMode
import com.elitec.spatial_renderer.gl.PrimitiveMeshIds
import com.elitec.spatial_renderer.gl.PrimitiveMeshRegistry
import com.elitec.spatial_renderer.gl.UnknownPrimitiveMeshException
import com.elitec.spatial_renderer.gl.createCube
import com.elitec.spatial_renderer.gl.createPlane
import com.elitec.spatial_renderer.gl.createSphere
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class PrimitiveMeshesTest {
    @Test
    fun cube_hasEightVerticesAndThirtySixIndices() {
        val cube = createCube()

        assertEquals(8, cube.vertexCount)
        assertEquals(36, cube.indexCount)
        assertEquals(MeshDrawMode.Triangles, cube.drawMode)
        assertTrue(cube.indices.all { it in 0 until cube.vertexCount })
    }

    @Test
    fun plane_hasTwoTriangles() {
        val plane = createPlane()

        assertEquals(4, plane.vertexCount)
        assertEquals(6, plane.indexCount)
        assertEquals(listOf(0, 1, 2, 0, 2, 3), plane.indices.toList())
        assertTrue(plane.indices.all { it in 0 until plane.vertexCount })
    }

    @Test
    fun sphere_usesConfiguredUvSegments() {
        val latitudeSegments = 4
        val longitudeSegments = 6
        val sphere = createSphere(latitudeSegments, longitudeSegments)

        assertEquals((latitudeSegments + 1) * (longitudeSegments + 1), sphere.vertexCount)
        assertEquals(latitudeSegments * longitudeSegments * 6, sphere.indexCount)
        assertEquals(MeshDrawMode.Triangles, sphere.drawMode)
        assertTrue(sphere.indices.all { it in 0 until sphere.vertexCount })
    }

    @Test
    fun meshDataRejectsInvalidPackingAndIndices() {
        assertThrows(IllegalArgumentException::class.java) {
            MeshData(vertices = floatArrayOf(0f, 1f))
        }
        assertThrows(IllegalArgumentException::class.java) {
            MeshData(vertices = floatArrayOf(0f, 0f, 0f), indices = intArrayOf(1))
        }
    }

    @Test
    fun registryResolvesPublicPrimitiveNames() {
        val registry = PrimitiveMeshRegistry()

        assertTrue(registry.contains(PrimitiveMeshIds.Cube))
        assertTrue(registry.contains(PrimitiveMeshIds.Sphere))
        assertTrue(registry.contains(PrimitiveMeshIds.Plane))
        assertFalse(registry.contains("Unknown"))
        assertEquals(8, registry.resolve(PrimitiveMeshIds.Cube).vertexCount)
        assertEquals(4, registry.resolve(PrimitiveMeshIds.Plane).vertexCount)
        assertEquals(registry.resolve(PrimitiveMeshIds.Cube), registry.resolve("Unknown"))
    }

    @Test
    fun registryThrowsControlledErrorForUnknownMesh() {
        val registry = PrimitiveMeshRegistry()

        assertThrows(UnknownPrimitiveMeshException::class.java) {
            registry.resolve("Unknown")
        }
    }

    @Test
    fun registryResolveOrNullMakesUnknownMeshPolicyExplicit() {
        val registry = PrimitiveMeshRegistry()

        assertEquals(registry.resolve(PrimitiveMeshIds.Cube), registry.resolveOrNull(PrimitiveMeshIds.Cube))
        assertEquals(null, registry.resolveOrNull("Unknown"))
    }
}