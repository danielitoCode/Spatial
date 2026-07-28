package com.elitec.spatial_geometry

import com.elitec.spatial_core.scene.ColorFactor
import com.elitec.spatial_core.scene.MaterialData
import com.elitec.spatial_core.scene.TextureReference
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

        val vertices = mutableListOf<Float>()
        val indices = mutableListOf<Int>()
        val normals = mutableListOf<Float>()
        val texCoords = mutableListOf<Float>()
        var allPrimitivesHaveNormals = true
        var allPrimitivesHaveTexCoords = true

        for ((meshIndex, meshValue) in meshes.list.withIndex()) {
            val mesh = meshValue as? JsonValue.JsonObject
                ?: throw IllegalArgumentException("Invalid mesh entry at index $meshIndex")
            val primitives = mesh.getArrayOrNull("primitives")
                ?: throw IllegalArgumentException("No primitives found in mesh at index $meshIndex")
            if (primitives.list.isEmpty()) {
                throw IllegalArgumentException("Primitives array is empty in mesh at index $meshIndex")
            }

            for ((primitiveIndex, primitiveValue) in primitives.list.withIndex()) {
                val primitive = primitiveValue as? JsonValue.JsonObject
                    ?: throw IllegalArgumentException("Invalid primitive entry at mesh $meshIndex primitive $primitiveIndex")
                val attributes = primitive.getObjectOrNull("attributes")
                    ?: throw IllegalArgumentException("No attributes found in mesh $meshIndex primitive $primitiveIndex")

                val posAccessorIdx = attributes.getIntOrNull("POSITION")
                    ?: throw IllegalArgumentException("POSITION attribute not found in mesh $meshIndex primitive $primitiveIndex attributes")
                val primitiveVertices = readFloatAccessor(
                    accessorIndex = posAccessorIdx,
                    accessors = accessors,
                    bufferViews = bufferViews,
                    bin = bin,
                    expectedType = "VEC3",
                    attributeName = "POSITION"
                )
                val vertexOffset = vertices.size / MeshData.CoordinatesPerVertex
                vertices.addAll(primitiveVertices.asIterable())

                val normalAccessorIdx = attributes.getIntOrNull("NORMAL")

                if (normalAccessorIdx != null) {
                    val primitiveNormals = readFloatAccessor(
                        accessorIndex = normalAccessorIdx,
                        accessors = accessors,
                        bufferViews = bufferViews,
                        bin = bin,
                        expectedType = "VEC3",
                        attributeName = "NORMAL"
                    )
                    if (primitiveNormals.size != primitiveVertices.size) {
                        throw IllegalArgumentException("NORMAL count must match POSITION count in mesh $meshIndex primitive $primitiveIndex")
                    }
                    normals.addAll(primitiveNormals.asIterable())
                } else {
                    allPrimitivesHaveNormals = false
                }

                val texCoordAccessorIdx = attributes.getIntOrNull("TEXCOORD_0")
                if (texCoordAccessorIdx != null) {
                    val primitiveTexCoords = readFloatAccessor(
                        accessorIndex = texCoordAccessorIdx,
                        accessors = accessors,
                        bufferViews = bufferViews,
                        bin = bin,
                        expectedType = "VEC2",
                        attributeName = "TEXCOORD_0"
                    )
                    if (primitiveTexCoords.size / MeshData.CoordinatesPerTexCoord != primitiveVertices.size / MeshData.CoordinatesPerVertex) {
                        throw IllegalArgumentException("TEXCOORD_0 count must match POSITION count in mesh $meshIndex primitive $primitiveIndex")
                    }
                    texCoords.addAll(primitiveTexCoords.asIterable())
                } else {
                    allPrimitivesHaveTexCoords = false
                }

                val indicesAccessorIdx = primitive.getIntOrNull("indices")
                if (indicesAccessorIdx != null) {
                    val primitiveIndices = readIndexAccessor(indicesAccessorIdx, accessors, bufferViews, bin)
                    indices.addAll(primitiveIndices.map { it + vertexOffset })
                }
            }
        }

        val material = parseMaterial(root, primitive.getIntOrNull("material"))

        return MeshData(
            vertices = vertices.toFloatArray(),
            indices = indices.toIntArray(),
            drawMode = MeshDrawMode.Triangles,
            normals = if (allPrimitivesHaveNormals) normals.toFloatArray() else floatArrayOf(),
            texCoords = if (allPrimitivesHaveTexCoords) texCoords.toFloatArray() else floatArrayOf(),
            material = material
        )
    }

    private fun parseMaterial(root: JsonValue.JsonObject, materialIndex: Int?): MaterialData {
        val material = materialIndex?.let { index ->
            val materials = root.getArrayOrNull("materials")
                ?: throw IllegalArgumentException("Primitive references material $index but glTF has no materials array")
            materials.list.getOrNull(index) as? JsonValue.JsonObject
                ?: throw IllegalArgumentException("Invalid material index in primitive: $index")
        } ?: return MaterialData(r = 1f, g = 1f, b = 1f, a = 1f)

        val pbr = material.getObjectOrNull("pbrMetallicRoughness")
        val baseColor = pbr?.getFloatArrayOrNull("baseColorFactor", expectedSize = 4)
            ?: floatArrayOf(1f, 1f, 1f, 1f)

        return MaterialData(
            r = baseColor[0],
            g = baseColor[1],
            b = baseColor[2],
            a = baseColor[3],
            baseColor = ColorFactor(baseColor[0], baseColor[1], baseColor[2], baseColor[3]),
            metallicFactor = pbr?.getFloatOrNull("metallicFactor") ?: 1f,
            roughnessFactor = pbr?.getFloatOrNull("roughnessFactor") ?: 1f,
            baseColorTexture = pbr?.getTextureReferenceOrNull("baseColorTexture"),
            metallicRoughnessTexture = pbr?.getTextureReferenceOrNull("metallicRoughnessTexture"),
            normalTexture = material.getTextureReferenceOrNull("normalTexture"),
            occlusionTexture = material.getTextureReferenceOrNull("occlusionTexture"),
            emissiveTexture = material.getTextureReferenceOrNull("emissiveTexture"),
        )
    }

    private fun readIndexAccessor(
        accessorIndex: Int,
        accessors: JsonValue.JsonArray,
        bufferViews: JsonValue.JsonArray,
        bin: ByteBuffer,
    ): IntArray {
        val indAccessor = accessors.list.getOrNull(accessorIndex) as? JsonValue.JsonObject
            ?: throw IllegalArgumentException("Invalid indices accessor index: $accessorIndex")

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

        val indices = IntArray(indCount)
        when (indComponentType) {
            5121 -> {
                for (i in 0 until indCount) {
                    bin.position(indStart + i)
                    indices[i] = bin.get().toInt() and 0xFF
                }
            }
            5123 -> {
                for (i in 0 until indCount) {
                    bin.position(indStart + i * 2)
                    indices[i] = bin.short.toInt() and 0xFFFF
                }
            }
            5125 -> {
                for (i in 0 until indCount) {
                    bin.position(indStart + i * 4)
                    indices[i] = bin.int
                }
            }
            else -> throw IllegalArgumentException("Unsupported indices componentType: $indComponentType")
        }
        return indices
    }

    internal fun readFloatAccessor(
        accessorIndex: Int,
        accessors: JsonValue.JsonArray,
        bufferViews: JsonValue.JsonArray,
        bin: ByteBuffer,
        expectedType: String,
        attributeName: String,
    ): FloatArray {
        val accessor = accessors.list.getOrNull(accessorIndex) as? JsonValue.JsonObject
            ?: throw IllegalArgumentException("Invalid $attributeName accessor index: $accessorIndex")

        val bufferViewIdx = accessor.getIntOrNull("bufferView")
            ?: throw IllegalArgumentException("$attributeName accessor has no bufferView")
        val bufferView = bufferViews.list.getOrNull(bufferViewIdx) as? JsonValue.JsonObject
            ?: throw IllegalArgumentException("Invalid bufferView index in $attributeName accessor: $bufferViewIdx")

        val count = accessor.getIntOrNull("count") ?: 0
        val type = accessor.getStringOrNull("type") ?: expectedType
        val componentType = accessor.getIntOrNull("componentType") ?: 5126 // FLOAT
        val byteOffset = accessor.getIntOrNull("byteOffset") ?: 0

        if (type != expectedType) {
            throw IllegalArgumentException("$attributeName accessor must be $expectedType, was $type")
        }
        if (componentType != 5126) {
            throw IllegalArgumentException("$attributeName accessor componentType must be 5126 (FLOAT), was $componentType")
        }

        val componentCount = when (expectedType) {
            "VEC2" -> 2
            "VEC3" -> 3
            else -> throw IllegalArgumentException("Unsupported FLOAT accessor type for $attributeName: $expectedType")
        }
        val componentByteSize = 4
        val packedByteSize = componentCount * componentByteSize
        val bufferViewByteOffset = bufferView.getIntOrNull("byteOffset") ?: 0
        val bufferViewByteStride = bufferView.getIntOrNull("byteStride") ?: 0
        val start = bufferViewByteOffset + byteOffset
        val stride = if (bufferViewByteStride > 0) bufferViewByteStride else packedByteSize

        val values = FloatArray(count * componentCount)
        for (i in 0 until count) {
            val elementOffset = start + i * stride
            for (component in 0 until componentCount) {
                bin.position(elementOffset + component * componentByteSize)
                values[i * componentCount + component] = bin.float
            }
        }
        return values
    }
}

internal sealed class JsonValue {
    data class JsonObject(val map: Map<String, JsonValue>) : JsonValue() {
        fun getObjectOrNull(key: String): JsonObject? = map[key] as? JsonObject
        fun getArrayOrNull(key: String): JsonArray? = map[key] as? JsonArray
        fun getIntOrNull(key: String): Int? = (map[key] as? JsonNumber)?.value?.toInt()
        fun getFloatOrNull(key: String): Float? = (map[key] as? JsonNumber)?.value?.toFloat()
        fun getStringOrNull(key: String): String? = (map[key] as? JsonString)?.value
        fun getFloatArrayOrNull(key: String, expectedSize: Int): FloatArray? {
            val array = map[key] as? JsonArray ?: return null
            if (array.list.size != expectedSize) {
                throw IllegalArgumentException("$key must have $expectedSize numeric values")
            }
            return FloatArray(expectedSize) { index ->
                (array.list[index] as? JsonNumber)?.value?.toFloat()
                    ?: throw IllegalArgumentException("$key must contain only numeric values")
            }
        }

        fun getTextureReferenceOrNull(key: String): TextureReference? {
            val textureInfo = getObjectOrNull(key) ?: return null
            val index = textureInfo.getIntOrNull("index")
                ?: throw IllegalArgumentException("$key textureInfo has no index")
            return TextureReference(index = index, texCoord = textureInfo.getIntOrNull("texCoord") ?: 0)
        }
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
