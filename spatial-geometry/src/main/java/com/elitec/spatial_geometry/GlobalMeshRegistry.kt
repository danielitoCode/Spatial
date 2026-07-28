package com.elitec.spatial_geometry

import java.util.concurrent.ConcurrentHashMap

/**
 * A thread-safe global registry for dynamically loaded 3D models.
 *
 * This registry acts as a bridge between the asynchronous loading layer (Compose/rememberModel)
 * and the rendering layer (SpatialGlRenderer), allowing loaded [MeshData] to be resolved by [meshId].
 */
public object GlobalMeshRegistry {
    private val meshes = ConcurrentHashMap<String, VersionedMeshData>()

    /**
     * Registers a mesh data under the given [meshId].
     *
     * The version advances when [meshId] is new or [meshData] differs from the current entry.
     * Renderers compare versions to invalidate existing GPU buffers when an asynchronous load
     * replaces a fallback mesh with the final loaded mesh (or with [MeshData.ErrorMesh]) without
     * forcing a re-upload for idempotent re-publication during recomposition.
     */
    public fun register(meshId: String, meshData: MeshData) {
        meshes.compute(meshId) { _, current ->
            if (current?.meshData == meshData) {
                current
            } else {
                VersionedMeshData(
                    meshData = meshData,
                    version = (current?.version ?: 0L) + 1L,
                )
            }
        }
    }

    /**
     * Resolves the [MeshData] registered under [meshId], or returns `null` if not found.
     */
    public fun get(meshId: String): MeshData? {
        return getVersioned(meshId)?.meshData
    }

    public fun getVersioned(meshId: String): VersionedMeshData? {
        return meshes[meshId]
    }

    /**
     * Clears all registered meshes.
     */
    public fun clear() {
        meshes.clear()
    }

    /** Mesh registry entry with a monotonically increasing version for renderer-side invalidation. */
    public data class VersionedMeshData(
        val meshData: MeshData,
        val version: Long,
    )
}
