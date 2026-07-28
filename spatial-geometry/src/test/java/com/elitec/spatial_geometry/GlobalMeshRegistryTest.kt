package com.elitec.spatial_geometry

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class GlobalMeshRegistryTest {
    @After
    fun tearDown() {
        GlobalMeshRegistry.clear()
    }

    @Test
    fun registerTracksMeshVersionsOnlyWhenMeshChanges() {
        GlobalMeshRegistry.register("model", MeshData.FallbackTriangle)
        val fallbackEntry = GlobalMeshRegistry.getVersioned("model")

        assertEquals(MeshData.FallbackTriangle, fallbackEntry?.meshData)
        assertEquals(1L, fallbackEntry?.version)
        assertSame(MeshData.FallbackTriangle, GlobalMeshRegistry.get("model"))

        GlobalMeshRegistry.register("model", MeshData.FallbackTriangle)
        assertEquals(1L, GlobalMeshRegistry.getVersioned("model")?.version)

        GlobalMeshRegistry.register("model", MeshData.ErrorMesh)
        val errorEntry = GlobalMeshRegistry.getVersioned("model")

        assertEquals(MeshData.ErrorMesh, errorEntry?.meshData)
        assertEquals(2L, errorEntry?.version)
    }

    @Test
    fun clearRemovesVersionedEntries() {
        GlobalMeshRegistry.register("model", MeshData.FallbackTriangle)

        GlobalMeshRegistry.clear()

        assertNull(GlobalMeshRegistry.getVersioned("model"))
        assertNull(GlobalMeshRegistry.get("model"))
    }
}