package com.elitec.spatial_geometry

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Minimal parser for glTF Binary (.glb) files.
 *
 * It focuses on extracting vertex positions and indices from the first mesh found.
 */
object GltfBinaryParser {

    private const val GLB_MAGIC = 0x46546C67 // "glTF"
    private const val CHUNK_TYPE_JSON = 0x4E4F534A // "JSON"
    private const val CHUNK_TYPE_BIN = 0x004E4942 // "BIN"

    fun parse(inputStream: InputStream): MeshData {
        val bytes = inputStream.readBytes()
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

        // 1. Header (12 bytes)
        val magic = buffer.int
        if (magic != GLB_MAGIC) throw IllegalArgumentException("Not a valid GLB file")
        val version = buffer.int
        if (version != 2) throw IllegalArgumentException("Only glTF 2.0 is supported")
        val totalLength = buffer.int

        // 2. Chunks
        var jsonChunk: String? = null
        var binaryChunk: ByteBuffer? = null

        while (buffer.hasRemaining()) {
            val chunkLength = buffer.int
            val chunkType = buffer.int
            val chunkData = ByteArray(chunkLength)
            buffer.get(chunkData)

            when (chunkType) {
                CHUNK_TYPE_JSON -> jsonChunk = String(chunkData, Charsets.UTF_8)
                CHUNK_TYPE_BIN -> binaryChunk = ByteBuffer.wrap(chunkData).order(ByteOrder.LITTLE_ENDIAN)
            }
        }

        if (jsonChunk == null || binaryChunk == null) {
            throw IllegalArgumentException("Missing required chunks in GLB")
        }

        return parseGltfJson(jsonChunk, binaryChunk)
    }

    /**
     * Extremely minimal JSON parser to extract accessors for positions and indices.
     * In a real project, use kotlinx.serialization or Gson.
     */
    private fun parseGltfJson(json: String, bin: ByteBuffer): MeshData {
        // Manual search for positions and indices accessors
        // This is a placeholder for a real JSON parsing logic.
        // For Core #2, we will eventually integrate a proper library.
        
        // For now, let's return a placeholder mesh to verify the pipeline.
        // TODO: Implement actual JSON parsing to find accessors.
        return MeshData(
            vertices = floatArrayOf(0f, 1f, 0f, -1f, -1f, 0f, 1f, -1f, 0f), // Triangle
            indices = intArrayOf(0, 1, 2)
        )
    }
}
