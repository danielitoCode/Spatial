package com.elitec.spatial_geometry

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GlobalMeshRegistryTest {
    @After
    fun tearDown() {
        GlobalMeshRegistry.clear()
        GlobalMeshRegistry.maxEntries = GlobalMeshRegistry.DEFAULT_MAX_ENTRIES
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
        assertEquals(0, GlobalMeshRegistry.size())
    }

    @Test
    fun unregisterRemovesSingleEntry() {
        GlobalMeshRegistry.register("a", MeshData.FallbackTriangle)
        GlobalMeshRegistry.register("b", MeshData.ErrorMesh)

        GlobalMeshRegistry.unregister("a")

        assertNull(GlobalMeshRegistry.get("a"))
        assertEquals(MeshData.ErrorMesh, GlobalMeshRegistry.get("b"))
        assertEquals(1, GlobalMeshRegistry.size())
    }

    @Test
    fun lruEvictsLeastRecentlyUsedWhenOverMaxEntries() {
        GlobalMeshRegistry.maxEntries = 2
        GlobalMeshRegistry.register("first", MeshData.FallbackTriangle)
        Thread.sleep(2)
        GlobalMeshRegistry.register("second", MeshData.ErrorMesh)
        Thread.sleep(2)
        // Touch "first" so "second" becomes the LRU victim when "third" is added.
        GlobalMeshRegistry.get("first")
        Thread.sleep(2)
        GlobalMeshRegistry.register("third", MeshData.FallbackTriangle)

        assertEquals(2, GlobalMeshRegistry.size())
        assertEquals(MeshData.FallbackTriangle, GlobalMeshRegistry.get("first"))
        assertNull(GlobalMeshRegistry.get("second"))
        assertEquals(MeshData.FallbackTriangle, GlobalMeshRegistry.get("third"))
    }

    @Test
    fun maxEntriesCoercedToAtLeastOne() {
        GlobalMeshRegistry.maxEntries = 0
        assertTrue(GlobalMeshRegistry.maxEntries >= 1)
        GlobalMeshRegistry.register("only", MeshData.FallbackTriangle)
        GlobalMeshRegistry.register("extra", MeshData.ErrorMesh)
        assertEquals(1, GlobalMeshRegistry.size())
    }
}
