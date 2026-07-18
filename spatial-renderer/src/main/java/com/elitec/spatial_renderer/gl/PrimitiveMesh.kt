package com.elitec.spatial_renderer.gl

import com.elitec.spatial_geometry.MeshData
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Public mesh ids kept aligned with Element.Cube, Element.Sphere and Element.Plane. */
object PrimitiveMeshIds {
    const val Cube = "Cube"
    const val Sphere = "Sphere"
    const val Plane = "Plane"
}

/**
 * Resolves scene mesh ids to [MeshData], backing both built-in primitives and dynamically loaded
 * meshes (e.g. models parsed from `.glb` files at runtime).
 *
 * Resolution order:
 *  1. Entries registered locally via [register] / [registerAll] (or supplied via [initialMeshes]).
 *  2. Entries published to the process-wide [com.elitec.spatial_geometry.GlobalMeshRegistry] by
 *     asynchronous loaders such as `rememberModel`. This fallback is what makes the registry
 *     *dynamically extensible* without forcing the renderer to know about specific load paths.
 *
 * Designed to plug into [SpatialGlRenderer.onDrawFrame], where unknown `meshId`s resolved through
 * this registry are uploaded to GPU buffers just-in-time (Phase 3 of
 * `PLAN_PREDEFINED_3D_MODELS.md`).
 *
 * Unknown mesh ids that are not found in either layer are intentionally NOT mapped to a primitive
 * fallback. Callers must handle [UnknownPrimitiveMeshException] or use [resolveOrNull] to apply an
 * explicit policy such as logging and skipping the node.
 *
 * Thread-safety: all operations are safe to call from any thread (backed by [ConcurrentHashMap]),
 * matching the real usage pattern where the GL thread reads while the IO/Compose thread writes
 * loaded meshes concurrently.
 */

class PrimitiveMeshRegistry(
    initialMeshes: Map<String, MeshData> = defaultMeshes(),
) {
    private val meshes = ConcurrentHashMap<String, MeshData>(initialMeshes)

    /** Registers a single [meshData] under [meshId], overwriting any previous entry. */
    fun register(meshId: String, meshData: MeshData) {
        meshes[meshId] = meshData
    }

    /**
     * Bulk-registers [meshes] keyed by their `meshId`, overwriting any previous entries.
     *
     * Phase 3 of `PLAN_PREDEFINED_3D_MODELS.md` (item L198). Useful for loading multiple GLB
     * assets in a single batch from a loader coroutine before publishing to the renderer, or
     * for registering all primitive overrides at construction in tests.
     *
     * Example:
     * ```kotlin
     * val registry = PrimitiveMeshRegistry()
     * registry.registerAll(mapOf(
     *     "raw:${R.raw.model_a}" to meshA,
     *     "raw:${R.raw.model_b}" to meshB,
     * ))
     * ```
     */
    fun registerAll(meshes: Map<String, MeshData>) {
        this.meshes.putAll(meshes)
    }

    fun resolve(meshId: String): MeshData = resolveOrNull(meshId) ?: throw UnknownPrimitiveMeshException(meshId)

    fun resolveOrNull(meshId: String): MeshData? {
        return meshes[meshId] ?: com.elitec.spatial_geometry.GlobalMeshRegistry.get(meshId)
    }

    fun contains(meshId: String): Boolean {
        return meshes.containsKey(meshId) || com.elitec.spatial_geometry.GlobalMeshRegistry.get(meshId) != null
    }

    companion object {
        private val fallbackMesh: MeshData by lazy { createCube() }

        fun defaultMeshes(): Map<String, MeshData> = mapOf(
            PrimitiveMeshIds.Cube to createCube(),
            PrimitiveMeshIds.Sphere to createSphere(),
            PrimitiveMeshIds.Plane to createPlane(),
        )
    }
}
class UnknownPrimitiveMeshException(meshId: String) :
    IllegalArgumentException("Unknown primitive mesh id: $meshId")


fun createCube(): MeshData = MeshData(
    vertices = floatArrayOf(
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        -0.5f, 0.5f, -0.5f,
        -0.5f, -0.5f, 0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f,
        -0.5f, 0.5f, 0.5f,
    ),
    indices = intArrayOf(
        0, 2, 1, 0, 3, 2, // back
        4, 5, 6, 4, 6, 7, // front
        0, 4, 7, 0, 7, 3, // left
        1, 2, 6, 1, 6, 5, // right
        3, 7, 6, 3, 6, 2, // top
        0, 1, 5, 0, 5, 4, // bottom
    ),
)

fun createPlane(): MeshData = MeshData(
    vertices = floatArrayOf(
        -0.5f, 0.0f, -0.5f,
        0.5f, 0.0f, -0.5f,
        0.5f, 0.0f, 0.5f,
        -0.5f, 0.0f, 0.5f,
    ),
    indices = intArrayOf(
        0, 1, 2,
        0, 2, 3,
    ),
)

fun createSphere(
    latitudeSegments: Int = DefaultSphereLatitudeSegments,
    longitudeSegments: Int = DefaultSphereLongitudeSegments,
): MeshData {
    require(latitudeSegments >= MinimumSphereLatitudeSegments) {
        "Sphere requires at least $MinimumSphereLatitudeSegments latitude segments."
    }
    require(longitudeSegments >= MinimumSphereLongitudeSegments) {
        "Sphere requires at least $MinimumSphereLongitudeSegments longitude segments."
    }

    val vertices = ArrayList<Float>((latitudeSegments + 1) * (longitudeSegments + 1) * MeshData.CoordinatesPerVertex)
    for (lat in 0..latitudeSegments) {
        val theta = PI * lat.toDouble() / latitudeSegments.toDouble()
        val y = cos(theta) * SphereRadius
        val ringRadius = sin(theta) * SphereRadius

        for (lon in 0..longitudeSegments) {
            val phi = 2.0 * PI * lon.toDouble() / longitudeSegments.toDouble()
            vertices += (ringRadius * cos(phi)).toFloat()
            vertices += y.toFloat()
            vertices += (ringRadius * sin(phi)).toFloat()
        }
    }

    val indices = ArrayList<Int>(latitudeSegments * longitudeSegments * 6)
    val stride = longitudeSegments + 1
    for (lat in 0 until latitudeSegments) {
        for (lon in 0 until longitudeSegments) {
            val first = lat * stride + lon
            val second = first + stride
            indices += first
            indices += second
            indices += first + 1
            indices += second
            indices += second + 1
            indices += first + 1
        }
    }

    return MeshData(
        vertices = vertices.toFloatArray(),
        indices = indices.toIntArray(),
    )
}

private const val SphereRadius = 0.5f
private const val DefaultSphereLatitudeSegments = 16
private const val DefaultSphereLongitudeSegments = 24
private const val MinimumSphereLatitudeSegments = 2
private const val MinimumSphereLongitudeSegments = 3