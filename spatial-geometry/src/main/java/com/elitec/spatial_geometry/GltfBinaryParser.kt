package com.elitec.spatial_geometry

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Minimal parser for glTF Binary (.glb) files.
 *
 * It focuses on extracting vertex positions and indices from the first mesh found.
 */
public object GltfBinaryParser : MeshLoader {

    private const val GLB_MAGIC = 0x46546C67 // "glTF"
    private const val CHUNK_TYPE_JSON = 0x4E4F534A // "JSON"
    private const val CHUNK_TYPE_BIN = 0x004E4942 // "BIN"

    override fun load(inputStream: InputStream): MeshData {
        return parse(inputStream)
    }

    public fun parse(inputStream: InputStream): MeshData {
        val bytes = inputStream.readBytes()
        if (bytes.size < 12) {
            throw IllegalArgumentException("File too small to be a GLB")
        }
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
            if (buffer.remaining() < 8) {
                // Ignore trailing padding bytes
                break
            }
            val chunkLength = buffer.int
            val chunkType = buffer.int
            if (buffer.remaining() < chunkLength) {
                throw IllegalArgumentException("Chunk length exceeds remaining bytes")
            }
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

    private fun parseGltfJson(json: String, bin: ByteBuffer): MeshData {
        val root = JsonParser(json).parse() as? JsonValue.JsonObject
            ?: throw IllegalArgumentException("Invalid glTF JSON root")

        val meshes = root.getArrayOrNull("meshes")
            ?: throw IllegalArgumentException("No meshes found in glTF")
        if (meshes.list.isEmpty()) {
            throw IllegalArgumentException("Meshes array is empty")
        }
        val firstMesh = meshes.list[0] as? JsonValue.JsonObject
            ?: throw IllegalArgumentException("Invalid mesh entry")

        val primitives = firstMesh.getArrayOrNull("primitives")
            ?: throw IllegalArgumentException("No primitives found in mesh")
        if (primitives.list.isEmpty()) {
            throw IllegalArgumentException("Primitives array is empty")
        }
        val primitive = primitives.list[0] as? JsonValue.JsonObject
            ?: throw IllegalArgumentException("Invalid primitive entry")

        val attributes = primitive.getObjectOrNull("attributes")
            ?: throw IllegalArgumentException("No attributes found in primitive")

        val posAccessorIdx = attributes.getIntOrNull("POSITION")
            ?: throw IllegalArgumentException("POSITION attribute not found in primitive attributes")

        val indicesAccessorIdx = primitive.getIntOrNull("indices")

        val accessors = root.getArrayOrNull("accessors")
            ?: throw IllegalArgumentException("No accessors found in glTF")
        val bufferViews = root.getArrayOrNull("bufferViews")
            ?: throw IllegalArgumentException("No bufferViews found in glTF")

        // 1. Read Positions
        val posAccessor = accessors.list.getOrNull(posAccessorIdx) as? JsonValue.JsonObject
            ?: throw IllegalArgumentException("Invalid POSITION accessor index: $posAccessorIdx")

        val posBufferViewIdx = posAccessor.getIntOrNull("bufferView")
            ?: throw IllegalArgumentException("POSITION accessor has no bufferView")
        val posBufferView = bufferViews.list.getOrNull(posBufferViewIdx) as? JsonValue.JsonObject
            ?: throw IllegalArgumentException("Invalid bufferView index in POSITION accessor: $posBufferViewIdx")

        val posCount = posAccessor.getIntOrNull("count") ?: 0
        val posType = posAccessor.getStringOrNull("type") ?: "VEC3"
        val posComponentType = posAccessor.getIntOrNull("componentType") ?: 5126 // FLOAT
        val posByteOffset = posAccessor.getIntOrNull("byteOffset") ?: 0

        val posBvByteOffset = posBufferView.getIntOrNull("byteOffset") ?: 0
        val posBvByteStride = posBufferView.getIntOrNull("byteStride") ?: 0

        if (posType != "VEC3") {
            throw IllegalArgumentException("POSITION accessor must be VEC3, was $posType")
        }
        if (posComponentType != 5126) {
            throw IllegalArgumentException("POSITION accessor componentType must be 5126 (FLOAT), was $posComponentType")
        }

        val vertices = FloatArray(posCount * 3)
        val posStart = posBvByteOffset + posByteOffset
        val posStride = if (posBvByteStride > 0) posBvByteStride else 12 // 3 * 4 bytes

        for (i in 0 until posCount) {
            val offset = posStart + i * posStride
            bin.position(offset)
            vertices[i * 3 + 0] = bin.float
            vertices[i * 3 + 1] = bin.float
            vertices[i * 3 + 2] = bin.float
        }

        // 2. Read Indices
        val indices: IntArray
        if (indicesAccessorIdx != null) {
            val indAccessor = accessors.list.getOrNull(indicesAccessorIdx) as? JsonValue.JsonObject
                ?: throw IllegalArgumentException("Invalid indices accessor index: $indicesAccessorIdx")

            val indBufferViewIdx = indAccessor.getIntOrNull("bufferView")
                ?: throw IllegalArgumentException("Indices accessor has no bufferView")
            val indBufferView = bufferViews.list.getOrNull(indBufferViewIdx) as? JsonValue.JsonObject
                ?: throw IllegalArgumentException("Invalid bufferView index in indices accessor: $indBufferViewIdx")

            val indCount = indAccessor.getIntOrNull("count") ?: 0
            val indComponentType = indAccessor.getIntOrNull("componentType")
                ?: throw IllegalArgumentException("Indices accessor has no componentType")
            val indByteOffset = indAccessor.getIntOrNull("byteOffset") ?: 0

            val indBvByteOffset = indBufferView.getIntOrNull("byteOffset") ?: 0
            val indStart = indBvByteOffset + indByteOffset

            indices = IntArray(indCount)
            when (indComponentType) {
                5121 -> { // UNSIGNED_BYTE
                    for (i in 0 until indCount) {
                        bin.position(indStart + i * 1)
                        indices[i] = bin.get().toInt() and 0xFF
                    }
                }
                5123 -> { // UNSIGNED_SHORT
                    for (i in 0 until indCount) {
                        bin.position(indStart + i * 2)
                        indices[i] = bin.short.toInt() and 0xFFFF
                    }
                }
                5125 -> { // UNSIGNED_INT
                    for (i in 0 until indCount) {
                        bin.position(indStart + i * 4)
                        indices[i] = bin.int
                    }
                }
                else -> throw IllegalArgumentException("Unsupported indices componentType: $indComponentType")
            }
        } else {
            indices = intArrayOf()
        }

        return MeshData(
            vertices = vertices,
            indices = indices,
            drawMode = MeshDrawMode.Triangles
        )
    }
}

internal sealed class JsonValue {
    data class JsonObject(val map: Map<String, JsonValue>) : JsonValue() {
        fun getObjectOrNull(key: String): JsonObject? = map[key] as? JsonObject
        fun getArrayOrNull(key: String): JsonArray? = map[key] as? JsonArray
        fun getIntOrNull(key: String): Int? = (map[key] as? JsonNumber)?.value?.toInt()
        fun getStringOrNull(key: String): String? = (map[key] as? JsonString)?.value
    }
    data class JsonArray(val list: List<JsonValue>) : JsonValue()
    data class JsonString(val value: String) : JsonValue()
    data class JsonNumber(val value: Double) : JsonValue()
    data class JsonBoolean(val value: Boolean) : JsonValue()
    object JsonNull : JsonValue()
}

internal class JsonParser(private val input: String) {
    private var pos = 0

    fun parse(): JsonValue {
        skipWhitespace()
        val value = parseValue()
        skipWhitespace()
        if (pos < input.length) {
            throw IllegalArgumentException("Unexpected characters at end of input")
        }
        return value
    }

    private fun parseValue(): JsonValue {
        if (pos >= input.length) throw IllegalArgumentException("Unexpected end of input")
        return when (val c = input[pos]) {
            '{' -> parseObject()
            '[' -> parseArray()
            '"' -> parseString()
            't', 'f' -> parseBoolean()
            'n' -> parseNull()
            in '0'..'9', '-', '+' -> parseNumber()
            else -> throw IllegalArgumentException("Unexpected character '$c' at position $pos")
        }
    }

    private fun parseObject(): JsonValue.JsonObject {
        expect('{')
        val map = mutableMapOf<String, JsonValue>()
        skipWhitespace()
        if (pos < input.length && input[pos] == '}') {
            pos++
            return JsonValue.JsonObject(map)
        }
        while (true) {
            skipWhitespace()
            if (pos >= input.length || input[pos] != '"') {
                throw IllegalArgumentException("Expected string key in object")
            }
            val key = (parseString() as JsonValue.JsonString).value
            skipWhitespace()
            expect(':')
            skipWhitespace()
            val value = parseValue()
            map[key] = value
            skipWhitespace()
            if (pos < input.length && input[pos] == '}') {
                pos++
                break
            }
            expect(',')
        }
        return JsonValue.JsonObject(map)
    }

    private fun parseArray(): JsonValue.JsonArray {
        expect('[')
        val list = mutableListOf<JsonValue>()
        skipWhitespace()
        if (pos < input.length && input[pos] == ']') {
            pos++
            return JsonValue.JsonArray(list)
        }
        while (true) {
            skipWhitespace()
            list.add(parseValue())
            skipWhitespace()
            if (pos < input.length && input[pos] == ']') {
                pos++
                break
            }
            expect(',')
        }
        return JsonValue.JsonArray(list)
    }

    private fun parseString(): JsonValue.JsonString {
        expect('"')
        val sb = StringBuilder()
        while (pos < input.length) {
            val c = input[pos++]
            if (c == '"') {
                return JsonValue.JsonString(sb.toString())
            } else if (c == '\\') {
                if (pos >= input.length) throw IllegalArgumentException("Unterminated escape sequence")
                val escaped = input[pos++]
                sb.append(when (escaped) {
                    '"' -> '"'
                    '\\' -> '\\'
                    '/' -> '/'
                    'b' -> '\b'
                    'f' -> '\u000C'
                    'n' -> '\n'
                    'r' -> '\r'
                    't' -> '\t'
                    'u' -> {
                        if (pos + 4 > input.length) throw IllegalArgumentException("Invalid unicode escape")
                        val hex = input.substring(pos, pos + 4)
                        pos += 4
                        hex.toInt(16).toChar()
                    }
                    else -> escaped
                })
            } else {
                sb.append(c)
            }
        }
        throw IllegalArgumentException("Unterminated string")
    }

    private fun parseBoolean(): JsonValue.JsonBoolean {
        if (input.startsWith("true", pos)) {
            pos += 4
            return JsonValue.JsonBoolean(true)
        } else if (input.startsWith("false", pos)) {
            pos += 5
            return JsonValue.JsonBoolean(false)
        }
        throw IllegalArgumentException("Expected boolean")
    }

    private fun parseNull(): JsonValue.JsonNull {
        if (input.startsWith("null", pos)) {
            pos += 4
            return JsonValue.JsonNull
        }
        throw IllegalArgumentException("Expected null")
    }

    private fun parseNumber(): JsonValue.JsonNumber {
        val start = pos
        if (pos < input.length && (input[pos] == '-' || input[pos] == '+')) {
            pos++
        }
        while (pos < input.length) {
            val c = input[pos]
            if (c in '0'..'9' || c == '.' || c == 'e' || c == 'E' || c == '-' || c == '+') {
                pos++
            } else {
                break
            }
        }
        val numStr = input.substring(start, pos)
        val value = numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number format: $numStr")
        return JsonValue.JsonNumber(value)
    }

    private fun expect(char: Char) {
        if (pos >= input.length || input[pos] != char) {
            throw IllegalArgumentException("Expected '$char' at position $pos")
        }
        pos++
    }

    private fun skipWhitespace() {
        while (pos < input.length && input[pos].isWhitespace()) {
            pos++
        }
    }
}
