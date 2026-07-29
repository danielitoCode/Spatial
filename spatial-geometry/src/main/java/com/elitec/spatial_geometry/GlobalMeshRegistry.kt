package com.elitec.spatial_geometry

import java.util.concurrent.ConcurrentHashMap

/**
 * A thread-safe global registry for dynamically loaded 3D models.
 *
 * This registry acts as a bridge between the asynchronous loading layer (Compose/rememberModel)
 * and the rendering layer (SpatialGlRenderer), allowing loaded [MeshData] to be resolved by [meshId].
 *
 * **Lifecycle (Core #2 item 1.8):** entries are not cleared when a single [Scene] leaves composition
 * (models may be shared across screens). Call [unregister] for a specific id, [clear] on process
 * teardown/tests, or rely on the soft [maxEntries] LRU cap to bound growth in long-lived apps.
 */
public object GlobalMeshRegistry {
    private val meshes = ConcurrentHashMap<String, VersionedMeshData>()
    private val lastAccessNanos = ConcurrentHashMap<String, Long>()

    /** Soft cap; when exceeded after [register], least-recently-used entries are dropped. */
    @Volatile
    public var maxEntries: Int = DEFAULT_MAX_ENTRIES
        set(value) {
            field = value.coerceAtLeast(1)
            evictIfNeeded()
        }

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
        touch(meshId)
        evictIfNeeded()
    }

    /** Removes a single entry. No-op if [meshId] is not registered. */
    public fun unregister(meshId: String) {
        meshes.remove(meshId)
        lastAccessNanos.remove(meshId)
    }

    /** Number of registered mesh ids. */
    public fun size(): Int = meshes.size

    /**
     * Resolves the [MeshData] registered under [meshId], or returns `null` if not found.
     */
    public fun get(meshId: String): MeshData? {
        return getVersioned(meshId)?.meshData
    }

    public fun getVersioned(meshId: String): VersionedMeshData? {
        val entry = meshes[meshId] ?: return null
        touch(meshId)
        return entry
    }

    /**
     * Clears all registered meshes.
     */
    public fun clear() {
        meshes.clear()
        lastAccessNanos.clear()
    }

    private fun touch(meshId: String) {
        lastAccessNanos[meshId] = System.nanoTime()
    }

    private fun evictIfNeeded() {
        val limit = maxEntries
        while (meshes.size > limit) {
            val victim = lastAccessNanos.entries
                .minByOrNull { it.value }
                ?.key
                ?: meshes.keys.firstOrNull()
                ?: break
            meshes.remove(victim)
            lastAccessNanos.remove(victim)
        }
    }

    /** Mesh registry entry with a monotonically increasing version for renderer-side invalidation. */
    public data class VersionedMeshData(
        val meshData: MeshData,
        val version: Long,
    )

    public const val DEFAULT_MAX_ENTRIES: Int = 64
}
