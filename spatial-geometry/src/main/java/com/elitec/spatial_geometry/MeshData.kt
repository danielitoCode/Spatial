package com.elitec.spatial_geometry

import com.elitec.spatial_core.scene.MaterialData
import kotlin.math.max

/** Draw mode understood by the renderer without exposing Android GL classes to mesh tests. */
enum class MeshDrawMode {
    Triangles,
    TriangleStrip,
    Lines,
    LineStrip,
}

/**
 * Pure mesh description. Vertices are packed as x/y/z triples; indices address vertices.
 *
 * This is the common exchange format between geometry loaders (glTF, OBJ) and the renderer.
 */
data class MeshData(
    val vertices: FloatArray,
    val indices: IntArray = intArrayOf(),
    val drawMode: MeshDrawMode = MeshDrawMode.Triangles,
    val normals: FloatArray = floatArrayOf(),
    val texCoords: FloatArray = floatArrayOf(),
    val material: MaterialData = MaterialData(),
) {
    init {
        require(vertices.size % CoordinatesPerVertex == 0) {
            "Mesh vertices must be packed as x/y/z triples."
        }
        require(indices.all { it >= 0 && it < vertexCount }) {
            "Mesh indices must reference existing vertices."
        }
        require(normals.isEmpty() || normals.size == vertexCount * CoordinatesPerNormal) {
            "Mesh normals must be empty or packed as x/y/z triples for every vertex."
        }
        require(texCoords.isEmpty() || texCoords.size == vertexCount * CoordinatesPerTexCoord) {
            "Mesh texture coordinates must be empty or packed as u/v pairs for every vertex."
        }
    }

    val vertexCount: Int get() = vertices.size / CoordinatesPerVertex
    val indexCount: Int get() = indices.size
    val hasIndices: Boolean get() = indices.isNotEmpty()

    /** Axis-aligned bounds of [vertices], or null if empty. */
    fun computeBounds(): MeshBounds? {
        if (vertexCount == 0) return null
        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var minZ = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY
        var maxZ = Float.NEGATIVE_INFINITY
        var i = 0
        while (i < vertices.size) {
            val x = vertices[i]
            val y = vertices[i + 1]
            val z = vertices[i + 2]
            if (x < minX) minX = x
            if (y < minY) minY = y
            if (z < minZ) minZ = z
            if (x > maxX) maxX = x
            if (y > maxY) maxY = y
            if (z > maxZ) maxZ = z
            i += CoordinatesPerVertex
        }
        return MeshBounds(minX, minY, minZ, maxX, maxY, maxZ)
    }

    /**
     * Centers the mesh at the origin and scales so the longest AABB axis is 1.
     * Keeps framing comparable to Core #1 unit primitives under Modifier3D.size.
     */
    fun normalizedToUnitCube(): MeshData {
        val bounds = computeBounds() ?: return this
        val extentX = bounds.maxX - bounds.minX
        val extentY = bounds.maxY - bounds.minY
        val extentZ = bounds.maxZ - bounds.minZ
        val maxExtent = max(extentX, max(extentY, extentZ)).coerceAtLeast(1e-6f)
        val scale = 1f / maxExtent
        val cx = (bounds.minX + bounds.maxX) * 0.5f
        val cy = (bounds.minY + bounds.maxY) * 0.5f
        val cz = (bounds.minZ + bounds.maxZ) * 0.5f

        val out = FloatArray(vertices.size)
        var i = 0
        while (i < vertices.size) {
            out[i] = (vertices[i] - cx) * scale
            out[i + 1] = (vertices[i + 1] - cy) * scale
            out[i + 2] = (vertices[i + 2] - cz) * scale
            i += CoordinatesPerVertex
        }
        return copy(vertices = out)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MeshData) return false
        return vertices.contentEquals(other.vertices) &&
                indices.contentEquals(other.indices) &&
                drawMode == other.drawMode &&
                normals.contentEquals(other.normals) &&
                texCoords.contentEquals(other.texCoords) &&
                material == other.material
    }

    override fun hashCode(): Int {
        var result = vertices.contentHashCode()
        result = 31 * result + indices.contentHashCode()
        result = 31 * result + drawMode.hashCode()
        result = 31 * result + normals.contentHashCode()
        result = 31 * result + texCoords.contentHashCode()
        result = 31 * result + material.hashCode()
        return result
    }

    companion object {
        const val CoordinatesPerVertex = 3
        const val CoordinatesPerNormal = 3
        const val CoordinatesPerTexCoord = 2

        val FallbackTriangle: MeshData = MeshData(
            vertices = floatArrayOf(0f, 1f, 0f, -1f, -1f, 0f, 1f, -1f, 0f),
            indices = intArrayOf(0, 1, 2)
        )

        val ErrorMesh: MeshData = MeshData(
            vertices = floatArrayOf(
                -1f, 1f, 0f,
                -0.33f, 0.33f, 0f,
                0.33f, 1f, 0f,
                1f, 0.33f, 0f,
                0.33f, -0.33f, 0f,
                1f, -1f, 0f,
                0.33f, -1f, 0f,
                -0.33f, -0.33f, 0f,
                -1f, -1f, 0f,
                -0.33f, 0.33f, 0f,
            ),
            indices = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 0, 9, 8),
        )
    }
}

/** Axis-aligned bounding box for [MeshData.vertices]. */
data class MeshBounds(
    val minX: Float,
    val minY: Float,
    val minZ: Float,
    val maxX: Float,
    val maxY: Float,
    val maxZ: Float,
) {
    val extentX: Float get() = maxX - minX
    val extentY: Float get() = maxY - minY
    val extentZ: Float get() = maxZ - minZ
}
