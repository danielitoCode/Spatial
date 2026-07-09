package com.elitec.spatial_geometry

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
) {
    init {
        require(vertices.size % CoordinatesPerVertex == 0) {
            "Mesh vertices must be packed as x/y/z triples."
        }
        require(indices.all { it >= 0 && it < vertexCount }) {
            "Mesh indices must reference existing vertices."
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
                drawMode == other.drawMode
    }

    override fun hashCode(): Int {
        var result = vertices.contentHashCode()
        result = 31 * result + indices.contentHashCode()
        result = 31 * result + drawMode.hashCode()
        return result
    }

    companion object {
        const val CoordinatesPerVertex = 3

        /** A simple 1x1 triangle used as a fallback while real models are loading. */
        val FallbackTriangle: MeshData = MeshData(
            vertices = floatArrayOf(0f, 1f, 0f, -1f, -1f, 0f, 1f, -1f, 0f),
            indices = intArrayOf(0, 1, 2)
        )
    }
}
