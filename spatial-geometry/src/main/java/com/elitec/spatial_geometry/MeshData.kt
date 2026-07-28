package com.elitec.spatial_geometry

import com.elitec.spatial_core.scene.MaterialData

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

        /** A simple 1x1 triangle used as a fallback while real models are loading. */
        val FallbackTriangle: MeshData = MeshData(
            vertices = floatArrayOf(0f, 1f, 0f, -1f, -1f, 0f, 1f, -1f, 0f),
            indices = intArrayOf(0, 1, 2)
        )

        /**
         * A distinguishable mesh registered when model loading fails.
         *
         * This must not match [FallbackTriangle], so renderers and diagnostics can tell an
         * invalid model apart from a model that is still loading.
         */
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
