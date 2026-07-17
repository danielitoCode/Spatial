package com.elitec.spatial_geometry

import java.util.concurrent.ConcurrentHashMap

/**
 * A thread-safe global registry for dynamically loaded 3D models.
 *
 * This registry acts as a bridge between the asynchronous loading layer (Compose/rememberModel)
 * and the rendering layer (SpatialGlRenderer), allowing loaded [MeshData] to be resolved by [meshId].
 */
public object GlobalMeshRegistry {
    private val meshes = ConcurrentHashMap<String, MeshData>()

    /**
     * Registers a mesh data under the given [meshId].
     */
    public fun register(meshId: String, meshData: MeshData) {
        meshes[meshId] = meshData
    }

    /**
     * Resolves the [MeshData] registered under [meshId], or returns `null` if not found.
     */
    public fun get(meshId: String): MeshData? {
        return meshes[meshId]
    }

    /**
     * Clears all registered meshes.
     */
    public fun clear() {
        meshes.clear()
    }
}
