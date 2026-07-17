package com.elitec.spatial_geometry

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

public class GltfBinaryParserTest {

    @Test
    fun testParseMinimalValidGlb() {
        val json = """
            {
              "asset": { "version": "2.0" },
              "meshes": [
                {
                  "primitives": [
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

        val jsonBytes = json.toByteArray(Charsets.UTF_8)
        val jsonPadding = (4 - (jsonBytes.size % 4)) % 4
        val jsonChunkLength = jsonBytes.size + jsonPadding

        // 9 floats = 36 bytes
        val positions = floatArrayOf(
            0f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f
        )
        // 3 shorts = 6 bytes + 2 bytes padding = 8 bytes
        val indices = shortArrayOf(0, 1, 2)

        val binBytesSize = 36 + 8
        val glbSize = 12 + 8 + jsonChunkLength + 8 + binBytesSize

        val glbBuffer = ByteBuffer.allocate(glbSize).order(ByteOrder.LITTLE_ENDIAN)
        // Header
        glbBuffer.putInt(0x46546C67) // magic
        glbBuffer.putInt(2) // version
        glbBuffer.putInt(glbSize) // total length

        // JSON Chunk
        glbBuffer.putInt(jsonChunkLength)
        glbBuffer.putInt(0x4E4F534A) // chunk type: JSON
        glbBuffer.put(jsonBytes)
        for (i in 0 until jsonPadding) {
            glbBuffer.put(' '.toByte())
        }

        // BIN Chunk
        glbBuffer.putInt(binBytesSize)
        glbBuffer.putInt(0x004E4942) // chunk type: BIN
        for (f in positions) {
            glbBuffer.putFloat(f)
        }
        for (s in indices) {
            glbBuffer.putShort(s)
        }
        // padding for bin chunk to align to 4 bytes
        glbBuffer.putShort(0)

        glbBuffer.flip()

        val stream = ByteArrayInputStream(glbBuffer.array())
        val meshData = GltfBinaryParser.parse(stream)

        assertEquals(3, meshData.vertexCount)
        assertEquals(3, meshData.indexCount)
        assertArrayEquals(positions, meshData.vertices, 1e-5f)
        assertArrayEquals(intArrayOf(0, 1, 2), meshData.indices)
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
}
