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
 * Small registry resolving scene mesh ids to built-in primitive mesh data.
 *
 * Unknown mesh ids are intentionally not mapped to a primitive fallback. Callers must
 * handle [UnknownPrimitiveMeshException] or use [resolveOrNull] to apply an explicit
 * policy such as logging and skipping the node.
 */

class PrimitiveMeshRegistry(
    initialMeshes: Map<String, MeshData> = defaultMeshes(),
) {
    private val meshes = ConcurrentHashMap<String, MeshData>(initialMeshes)

    fun register(meshId: String, meshData: MeshData) {
        meshes[meshId] = meshData
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