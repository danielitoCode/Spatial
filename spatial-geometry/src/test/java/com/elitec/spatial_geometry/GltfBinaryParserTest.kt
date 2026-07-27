package com.elitec.spatial_geometry

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class GltfBinaryParserTest {

    @Test
    fun testParseMinimalValidGlb() {
        val positions = floatArrayOf(
            0f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f
        )
        val indices = intArrayOf(0, 1, 2)

        val meshData = GltfBinaryParser.parse(
            ByteArrayInputStream(createGlb(positions = positions, indices = indices))
        )

        assertEquals(3, meshData.vertexCount)
        assertEquals(3, meshData.indexCount)
        assertArrayEquals(positions, meshData.vertices, 1e-5f)
        assertArrayEquals(indices, meshData.indices)
        assertArrayEquals(floatArrayOf(), meshData.normals, 1e-5f)
        assertArrayEquals(floatArrayOf(), meshData.texCoords, 1e-5f)
    }

    @Test
    fun testParseGlbWithMultiplePrimitivesFailsClearly() {
        val json = """
            {
              "asset": { "version": "2.0" },
              "meshes": [
                {
                  "primitives": [
                    {
                      "attributes": { "POSITION": 0 },
                      "indices": 1
                    },
                    {
                      "attributes": { "POSITION": 0 },
                      "indices": 1
                    }
                  ]
                }
              ],
              "accessors": [
                {
                  "bufferView": 0,
                  "componentType": 5126,
                  "count": 3,
                  "type": "VEC3",
                  "byteOffset": 0
                },
                {
                  "bufferView": 1,
                  "componentType": 5123,
                  "count": 3,
                  "type": "SCALAR",
                  "byteOffset": 0
                }
              ],
              "bufferViews": [
                {
                  "buffer": 0,
                  "byteOffset": 0,
                  "byteLength": 36
                },
                {
                  "buffer": 0,
                  "byteOffset": 36,
                  "byteLength": 8
                }
              ],
              "buffers": [
                {
                  "byteLength": 44
                }
              ]
            }
        """.trimIndent()

        val exception = try {
            GltfBinaryParser.parse(ByteArrayInputStream(createMinimalGlb(json)))
            null
        } catch (e: IllegalArgumentException) {
            e
        }

        assertEquals(
            "GltfBinaryParser currently supports exactly one mesh with one primitive; found 2 primitives",
            exception?.message
        )
    }

    @Test
    fun testParseGlbWithMultipleMeshesFailsClearly() {
        val json = """
            {
              "asset": { "version": "2.0" },
              "meshes": [
                { "primitives": [ { "attributes": { "POSITION": 0 }, "indices": 1 } ] },
                { "primitives": [ { "attributes": { "POSITION": 0 }, "indices": 1 } ] }
              ],
              "accessors": [
                { "bufferView": 0, "componentType": 5126, "count": 3, "type": "VEC3" },
                { "bufferView": 1, "componentType": 5123, "count": 3, "type": "SCALAR" }
              ],
              "bufferViews": [
                { "buffer": 0, "byteOffset": 0, "byteLength": 36 },
                { "buffer": 0, "byteOffset": 36, "byteLength": 8 }
              ],
              "buffers": [ { "byteLength": 44 } ]
            }
        """.trimIndent()

        val exception = try {
            GltfBinaryParser.parse(ByteArrayInputStream(createMinimalGlb(json)))
            null
        } catch (e: IllegalArgumentException) {
            e
        }

        assertEquals(
            "GltfBinaryParser currently supports exactly one mesh with one primitive; found 2 meshes",
            exception?.message
        )
    }

    @Test
    fun testParsePositionsAndNormals() {
        val positions = floatArrayOf(
            0f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f
        )
        val normals = floatArrayOf(
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f
        )
        val meshData = GltfBinaryParser.parse(
            ByteArrayInputStream(createGlb(positions = positions, normals = normals))
        )
        assertArrayEquals(positions, meshData.vertices, 1e-5f)
        assertArrayEquals(normals, meshData.normals, 1e-5f)
        assertArrayEquals(floatArrayOf(), meshData.texCoords, 1e-5f)
    }

    @Test
    fun testParsePositionsAndTexCoords() {
        val positions = floatArrayOf(
            0f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f
        )
        val texCoords = floatArrayOf(
            0.5f, 1f,
            0f, 0f,
            1f, 0f
        )

        val meshData = GltfBinaryParser.parse(
            ByteArrayInputStream(createGlb(positions = positions, texCoords = texCoords))
        )

        assertArrayEquals(positions, meshData.vertices, 1e-5f)
        assertArrayEquals(floatArrayOf(), meshData.normals, 1e-5f)
        assertArrayEquals(texCoords, meshData.texCoords, 1e-5f)
    }

    @Test
    fun testParseWithoutNormalsOrTexCoords() {
        val positions = floatArrayOf(
            0f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f
        )

        val meshData = GltfBinaryParser.parse(
            ByteArrayInputStream(createGlb(positions = positions))
        )

        assertEquals(3, meshData.vertexCount)
        assertArrayEquals(positions, meshData.vertices, 1e-5f)
        assertArrayEquals(floatArrayOf(), meshData.normals, 1e-5f)
        assertArrayEquals(floatArrayOf(), meshData.texCoords, 1e-5f)
    }

    @Test
    fun testJsonParserBasic() {
        val json = """
            {
              "string": "value",
              "number": 123.45,
              "boolean": true,
              "nullValue": null,
              "array": [1, 2, 3],
              "nested": {
                "key": "val"
              }
            }
        """.trimIndent()

        val root = JsonParser(json).parse() as JsonValue.JsonObject
        assertEquals("value", root.getStringOrNull("string"))
        assertEquals(123, root.getIntOrNull("number"))
        assertEquals(JsonValue.JsonBoolean(true), root.map["boolean"])
        assertEquals(JsonValue.JsonNull, root.map["nullValue"])

        val array = root.getArrayOrNull("array")!!
        assertEquals(3, array.list.size)
        assertEquals(1, (array.list[0] as JsonValue.JsonNumber).value.toInt())

        val nested = root.getObjectOrNull("nested")!!
        assertEquals("val", nested.getStringOrNull("key"))
    }

    private fun createGlb(
        positions: FloatArray,
        normals: FloatArray = floatArrayOf(),
        texCoords: FloatArray = floatArrayOf(),
        indices: IntArray = intArrayOf(),
    ): ByteArray {
        val binary = ByteBuffer.allocate(4096)
            .order(ByteOrder.LITTLE_ENDIAN)
        val bufferViews = mutableListOf<String>()
        val accessors = mutableListOf<String>()
        val attributes = mutableListOf("\"POSITION\": 0")

        fun putFloats(values: FloatArray, componentType: String): Int {
            val byteOffset = binary.position()
            val componentCount = if (componentType == "VEC3") 3 else 2
            val byteStride = if (componentType == "VEC3") 16 else 12
            val count = values.size / componentCount
            for (element in 0 until count) {
                for (component in 0 until componentCount) {
                    binary.putFloat(values[element * componentCount + component])
                }
                repeat(byteStride - componentCount * 4) { binary.put(0) }
            }
            val byteLength = count * byteStride
            val bufferViewIndex = bufferViews.size
            bufferViews += """{ "buffer": 0, "byteOffset": $byteOffset, "byteLength": $byteLength, "byteStride": $byteStride }"""
            val accessorIndex = accessors.size
            accessors += """{ "bufferView": $bufferViewIndex, "componentType": 5126, "count": $count, "type": "$componentType", "byteOffset": 0 }"""
            return accessorIndex
        }

        putFloats(positions, "VEC3")
        if (normals.isNotEmpty()) {
            attributes += "\"NORMAL\": ${putFloats(normals, "VEC3")}"
        }
        if (texCoords.isNotEmpty()) {
            attributes += "\"TEXCOORD_0\": ${putFloats(texCoords, "VEC2")}"
        }

        val indicesAccessor = if (indices.isNotEmpty()) {
            val byteOffset = binary.position()
            for (index in indices) binary.putShort(index.toShort())
            if (binary.position() % 4 != 0) binary.putShort(0)
            val byteLength = indices.size * 2
            val bufferViewIndex = bufferViews.size
            bufferViews += """{ "buffer": 0, "byteOffset": $byteOffset, "byteLength": $byteLength }"""
            val accessorIndex = accessors.size
            accessors += """{ "bufferView": $bufferViewIndex, "componentType": 5123, "count": ${indices.size}, "type": "SCALAR", "byteOffset": 0 }"""
            ", \"indices\": $accessorIndex"
        } else {
            ""
        }

        val binLength = binary.position()
        val binBytes = binary.array().copyOf(binLength)
        val json = """
            {
              "asset": { "version": "2.0" },
              "meshes": [ { "primitives": [ { "attributes": { ${attributes.joinToString()} }$indicesAccessor } ] } ],
              "accessors": [ ${accessors.joinToString()} ],
              "bufferViews": [ ${bufferViews.joinToString()} ],
              "buffers": [ { "byteLength": $binLength } ]
            }
        """.trimIndent()

        return buildGlb(json, binBytes)
    }

    private fun createMinimalGlb(json: String): ByteArray {
        val jsonBytes = json.toByteArray(Charsets.UTF_8)
        val jsonPadding = (4 - (jsonBytes.size % 4)) % 4
        val jsonChunkLength = jsonBytes.size + jsonPadding

        val positions = floatArrayOf(
            0f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f
        )
        val indices = shortArrayOf(0, 1, 2)
        val binBytesSize = 36 + 8
        val glbSize = 12 + 8 + jsonChunkLength + 8 + binBytesSize

        val glbBuffer = ByteBuffer.allocate(glbSize).order(ByteOrder.LITTLE_ENDIAN)
        glbBuffer.putInt(0x46546C67)
        glbBuffer.putInt(2)
        glbBuffer.putInt(glbSize)

        glbBuffer.putInt(jsonChunkLength)
        glbBuffer.putInt(0x4E4F534A)
        glbBuffer.put(jsonBytes)
        for (i in 0 until jsonPadding) {
            glbBuffer.put(' '.toByte())
        }

        glbBuffer.putInt(binBytesSize)
        glbBuffer.putInt(0x004E4942)
        for (f in positions) {
            glbBuffer.putFloat(f)
        }
        for (index in indices) {
            glbBuffer.putShort(index)
        }
        glbBuffer.putShort(0)

        return glbBuffer.array()
    }

    private fun buildGlb(json: String, binBytes: ByteArray): ByteArray {
        val jsonBytes = json.toByteArray(Charsets.UTF_8)
        val jsonPadding = (4 - (jsonBytes.size % 4)) % 4
        val binPadding = (4 - (binBytes.size % 4)) % 4
        val jsonChunkLength = jsonBytes.size + jsonPadding
        val binChunkLength = binBytes.size + binPadding
        val glbSize = 12 + 8 + jsonChunkLength + 8 + binChunkLength

        return ByteBuffer.allocate(glbSize).order(ByteOrder.LITTLE_ENDIAN).apply {
            putInt(0x46546C67)
            putInt(2)
            putInt(glbSize)
            putInt(jsonChunkLength)
            putInt(0x4E4F534A)
            put(jsonBytes)
            repeat(jsonPadding) { put(' '.code.toByte()) }
            putInt(binChunkLength)
            putInt(0x004E4942)
            put(binBytes)
            repeat(binPadding) { put(0) }
        }.array()
    }
}
