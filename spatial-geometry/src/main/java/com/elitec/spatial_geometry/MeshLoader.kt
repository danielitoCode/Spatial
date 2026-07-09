package com.elitec.spatial_geometry

import java.io.InputStream

/**
 * Interface for loading 3D mesh data from external sources.
 */
interface MeshLoader {
    /**
     * Loads mesh data from the provided [inputStream].
     * @throws Exception if parsing fails.
     */
    fun load(inputStream: InputStream): MeshData
}
