package com.elitec.spatial_renderer.gl

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import android.opengl.Matrix
import android.util.Log
import com.elitec.spatial_core.camera.CameraSnapshot
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import com.elitec.spatial_core.render.Color4
import com.elitec.spatial_core.scene.RenderableNode
import com.elitec.spatial_geometry.GlobalMeshRegistry
import com.elitec.spatial_geometry.MeshData
import com.elitec.spatial_geometry.MeshDrawMode
import com.elitec.spatial_renderer.BuildConfig

class SpatialGlRenderer : GLSurfaceView.Renderer {
    private val meshRegistry = PrimitiveMeshRegistry()
    private val meshBuffers = java.util.concurrent.ConcurrentHashMap<String, GlMeshBuffers>()
    private var programId: Int = 0
    private var nodes: List<RenderableNode> = emptyList()
    private var cameraSnapshot: CameraSnapshot = CameraSnapshot()
    private var aspectRatio: Float = 1f
    private var uniforms: UniformLocations? = null

    /** Called once GL surface is fully initialized so the host can trigger a first render pass. */
    var onSurfaceReadyCallback: (() -> Unit)? = null

    /** Called every time the viewport size changes, so the host can keep its own aspect ratio in sync. */
    var onViewportChangedCallback: ((aspectRatio: Float) -> Unit)? = null

    private val projectionMatrix = FloatArray(16)

    private var surfaceReadyCallbackFired = false

    fun resetSurfaceReadyGate() {
        surfaceReadyCallbackFired = false
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "resetSurfaceReadyGate: re-armed, will fire onSurfaceReadyCallback on next onSurfaceChanged")
        }
    }

    fun updateNodes(newNodes: List<RenderableNode>) {
        nodes = newNodes
        val modelIds = newNodes.map { it.meshId }.filter { it.startsWith("raw:") }
        if (modelIds.isNotEmpty()) {
            Log.i(MODEL_TAG, "updateNodes count=${newNodes.size} modelMeshIds=$modelIds allIds=${newNodes.map { it.meshId }}")
        }
    }

    fun updateCamera(snapshot: CameraSnapshot) {
        cameraSnapshot = snapshot
    }

    private var frameClearColor: Color4 = Color4.TRANSPARENT

    fun updateClearColor(color: Color4) {
        frameClearColor = color
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        releaseGlResources()

        applyClearColor(frameClearColor)
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        programId = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        uniforms = UniformLocations.fromProgram(programId)

        PrimitiveMeshRegistry.defaultMeshes().forEach { (meshId, meshData) ->
            meshBuffers[meshId] = meshData.toGlMeshBuffers()
        }

        if (BuildConfig.DEBUG) {
            Log.d(
                TAG,
                "onSurfaceCreated: programId=$programId, meshBuffers.size=${meshBuffers.size}, meshIds=${meshBuffers.keys}",
            )
        }

        surfaceReadyCallbackFired = false
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        aspectRatio = if (height == 0) 1f else width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 45f, aspectRatio, 0.1f, 100f)
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onSurfaceChanged: width=$width, height=$height, aspectRatio=$aspectRatio")
        }
        onViewportChangedCallback?.invoke(aspectRatio)
        if (!surfaceReadyCallbackFired) {
            surfaceReadyCallbackFired = true
            onSurfaceReadyCallback?.invoke()
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        applyClearColor(frameClearColor)

        if (nodes.isEmpty()) {
            Log.i(TAG, "Skipping draw frame: GL surface is ready but there are no renderable nodes")
            return
        }

        if (programId == 0) {
            Log.e(
                TAG,
                "Skipping draw frame: GL program is not ready while ${nodes.size} renderable node(s) are present",
            )
            return
        }

        val uniformLocations = uniforms
        if (uniformLocations == null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onDrawFrame: returning because uniforms are missing; nodes.size=${nodes.size}, programId=$programId")
            }
            return
        }

        GLES30.glUseProgram(programId)

        val viewMatrix = FloatArray(16)

        val orbitDistance = orbitDistanceForVisualZoom(cameraSnapshot.zoom)
        val eyeX = (orbitDistance * Math.sin(Math.toRadians(cameraSnapshot.yaw.toDouble())) * Math.cos(Math.toRadians(cameraSnapshot.pitch.toDouble()))).toFloat()
        val eyeY = (orbitDistance * Math.sin(Math.toRadians(cameraSnapshot.pitch.toDouble()))).toFloat()
        val eyeZ = (orbitDistance * Math.cos(Math.toRadians(cameraSnapshot.yaw.toDouble())) * Math.cos(Math.toRadians(cameraSnapshot.pitch.toDouble()))).toFloat()

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, 0f, 0f, 0f, 0f, 1f, 0f)

        GLES30.glUniformMatrix4fv(uniformLocations.viewMatrix, 1, false, viewMatrix, 0)
        GLES30.glUniformMatrix4fv(uniformLocations.projectionMatrix, 1, false, projectionMatrix, 0)

        GLES30.glEnableVertexAttribArray(PositionAttributeLocation)

        var drawCalls = 0
        var skippedUnknownMeshIds = 0
        var skippedMissingBuffers = 0
        var modelDrawCalls = 0

        nodes.forEach { node ->
            val isModel = node.meshId.startsWith("raw:")
            val meshData = meshRegistry.resolveOrNull(node.meshId)
            if (meshData == null) {
                skippedUnknownMeshIds++
                if (isModel) {
                    Log.w(
                        MODEL_TAG,
                        "SKIP unknown meshId=${node.meshId} registryHas=${GlobalMeshRegistry.get(node.meshId) != null} " +
                            "registrySize=${GlobalMeshRegistry.size()}",
                    )
                } else if (BuildConfig.DEBUG) {
                    Log.w(TAG, "Skipping renderable with unknown primitive mesh id: ${node.meshId}")
                }
                return@forEach
            }

            if (isModel) {
                Log.i(
                    MODEL_TAG,
                    "RESOLVED meshId=${node.meshId} verts=${meshData.vertexCount} idx=${meshData.indexCount}",
                )
            }

            val registryVersion = GlobalMeshRegistry.getVersioned(node.meshId)?.version
            var mesh = meshBuffers[node.meshId]
            if (mesh == null || mesh.registryVersion != registryVersion) {
                try {
                    val newBuffers = meshData.toGlMeshBuffers(registryVersion)
                    val previousBuffers = meshBuffers.put(node.meshId, newBuffers)
                    if (previousBuffers != null && previousBuffers !== newBuffers) {
                        previousBuffers.release()
                    }
                    mesh = newBuffers
                    if (isModel || BuildConfig.DEBUG) {
                        val uploadReason = if (previousBuffers == null) "JIT uploaded" else "Re-uploaded changed"
                        Log.i(MODEL_TAG, "$uploadReason meshId=${node.meshId} registryVersion=$registryVersion")
                    }
                } catch (e: Exception) {
                    Log.e(MODEL_TAG, "Failed to upload GL buffers for meshId=${node.meshId}", e)
                }
            }

            if (mesh == null) {
                skippedMissingBuffers++
                if (isModel) {
                    Log.w(MODEL_TAG, "SKIP missing GL buffers meshId=${node.meshId}")
                } else if (BuildConfig.DEBUG) {
                    Log.w(TAG, "Skipping renderable because GL buffers are missing for mesh id: ${node.meshId}")
                }
                return@forEach
            }

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mesh.vertexBufferId)
            GLES30.glVertexAttribPointer(
                PositionAttributeLocation,
                MeshData.CoordinatesPerVertex,
                GLES30.GL_FLOAT,
                false,
                MeshData.CoordinatesPerVertex * Float.SIZE_BYTES,
                0,
            )

            if (mesh.normalBufferId != 0) {
                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mesh.normalBufferId)
                GLES30.glEnableVertexAttribArray(NormalAttributeLocation)
                GLES30.glVertexAttribPointer(
                    NormalAttributeLocation,
                    MeshData.CoordinatesPerNormal,
                    GLES30.GL_FLOAT,
                    false,
                    MeshData.CoordinatesPerNormal * Float.SIZE_BYTES,
                    0,
                )
            } else {
                GLES30.glDisableVertexAttribArray(NormalAttributeLocation)
            }

            if (mesh.texCoordBufferId != 0) {
                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mesh.texCoordBufferId)
                GLES30.glEnableVertexAttribArray(TexCoordAttributeLocation)
                GLES30.glVertexAttribPointer(
                    TexCoordAttributeLocation,
                    MeshData.CoordinatesPerTexCoord,
                    GLES30.GL_FLOAT,
                    false,
                    MeshData.CoordinatesPerTexCoord * Float.SIZE_BYTES,
                    0,
                )
            } else {
                GLES30.glDisableVertexAttribArray(TexCoordAttributeLocation)
            }

            GLES30.glUniformMatrix4fv(uniformLocations.modelMatrix, 1, false, node.modelMatrix, 0)
            GLES30.glUniform4f(
                uniformLocations.color,
                node.material.r,
                node.material.g,
                node.material.b,
                node.material.a,
            )

            if (mesh.indexBufferId != 0) {
                GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mesh.indexBufferId)
                GLES30.glDrawElements(mesh.drawMode.toGlDrawMode(), mesh.indexCount, GLES30.GL_UNSIGNED_INT, 0)
            } else {
                GLES30.glDrawArrays(mesh.drawMode.toGlDrawMode(), 0, mesh.vertexCount)
            }
            drawCalls++
            if (isModel) modelDrawCalls++
        }
        if (modelDrawCalls > 0 || skippedUnknownMeshIds > 0) {
            Log.i(
                MODEL_TAG,
                "onDrawFrame modelDraws=$modelDrawCalls totalDraws=$drawCalls " +
                    "skippedUnknown=$skippedUnknownMeshIds skippedMissingBuf=$skippedMissingBuffers nodes=${nodes.size}",
            )
        } else if (BuildConfig.DEBUG) {
            Log.d(
                TAG,
                "onDrawFrame: nodes.size=${nodes.size}, drawCalls=$drawCalls, skippedUnknownMeshIds=$skippedUnknownMeshIds, skippedMissingBuffers=$skippedMissingBuffers",
            )
        }
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)

        GLES30.glDisableVertexAttribArray(PositionAttributeLocation)
        GLES30.glDisableVertexAttribArray(NormalAttributeLocation)
        GLES30.glDisableVertexAttribArray(TexCoordAttributeLocation)
    }

    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = compileShader(GLES30.GL_VERTEX_SHADER, vertexSource)
        val fragmentShader = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource)

        val program = GLES30.glCreateProgram()
        GLES30.glAttachShader(program, vertexShader)
        GLES30.glAttachShader(program, fragmentShader)
        GLES30.glLinkProgram(program)

        val linkStatus = IntArray(1)
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val log = GLES30.glGetProgramInfoLog(program)
            GLES30.glDeleteProgram(program)
            throw IllegalStateException("Error al linkear programa GL: $log")
        }

        GLES30.glDeleteShader(vertexShader)
        GLES30.glDeleteShader(fragmentShader)
        return program
    }

    fun releaseGlResources() {
        meshBuffers.values.forEach { it.release() }
        meshBuffers.clear()

        if (programId != 0) {
            GLES30.glDeleteProgram(programId)
            programId = 0
        }
        uniforms = null
    }
    private fun applyClearColor(color: Color4) {
        GLES30.glClearColor(color.r, color.g, color.b, color.a)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
    }

    private fun MeshData.toGlMeshBuffers(registryVersion: Long? = null): GlMeshBuffers {
        val attributePlan = toMeshBufferAttributeAvailability()
        val vertexBufferId = uploadFloatArrayBuffer(vertices)
        val normalBufferId = if (attributePlan.hasNormals) uploadFloatArrayBuffer(normals) else 0
        val texCoordBufferId = if (attributePlan.hasTexCoords) uploadFloatArrayBuffer(texCoords) else 0

        val indexBufferId = if (hasIndices) {
            val indexBuffer = ByteBuffer.allocateDirect(indices.size * Int.SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .apply {
                    put(indices)
                    position(0)
                }
            val indexBufferIds = IntArray(1)
            GLES30.glGenBuffers(1, indexBufferIds, 0)
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexBufferIds[0])
            GLES30.glBufferData(
                GLES30.GL_ELEMENT_ARRAY_BUFFER,
                indices.size * Int.SIZE_BYTES,
                indexBuffer,
                GLES30.GL_STATIC_DRAW,
            )
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
            indexBufferIds[0]
        } else {
            0
        }

        return GlMeshBuffers(
            vertexBufferId = vertexBufferId,
            normalBufferId = normalBufferId,
            texCoordBufferId = texCoordBufferId,
            indexBufferId = indexBufferId,
            vertexCount = vertexCount,
            indexCount = indexCount,
            drawMode = drawMode,
            registryVersion = registryVersion,
        )
    }

    private fun uploadFloatArrayBuffer(values: FloatArray): Int {
        val buffer = ByteBuffer.allocateDirect(values.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(values)
                position(0)
            }
        val bufferIds = IntArray(1)
        GLES30.glGenBuffers(1, bufferIds, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, bufferIds[0])
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            values.size * Float.SIZE_BYTES,
            buffer,
            GLES30.GL_STATIC_DRAW,
        )
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        return bufferIds[0]
    }

    private fun MeshDrawMode.toGlDrawMode(): Int = when (this) {
        MeshDrawMode.Triangles -> GLES30.GL_TRIANGLES
        MeshDrawMode.TriangleStrip -> GLES30.GL_TRIANGLE_STRIP
        MeshDrawMode.Lines -> GLES30.GL_LINES
        MeshDrawMode.LineStrip -> GLES30.GL_LINE_STRIP
    }

    private fun compileShader(type: Int, source: String): Int {
        val shader = GLES30.glCreateShader(type)
        GLES30.glShaderSource(shader, source)
        GLES30.glCompileShader(shader)

        val compileStatus = IntArray(1)
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val log = GLES30.glGetShaderInfoLog(shader)
            GLES30.glDeleteShader(shader)
            throw IllegalStateException("Error al compilar shader GL: $log")
        }
        return shader
    }

    private data class GlMeshBuffers(
        val vertexBufferId: Int,
        val normalBufferId: Int,
        val texCoordBufferId: Int,
        val indexBufferId: Int,
        val vertexCount: Int,
        val indexCount: Int,
        val drawMode: MeshDrawMode,
        val registryVersion: Long?,
    ) {
        fun release() {
            val bufferIds = intArrayOf(
                vertexBufferId,
                normalBufferId,
                texCoordBufferId,
                indexBufferId,
            ).filter { it != 0 }.toIntArray()
            if (bufferIds.isNotEmpty()) {
                GLES30.glDeleteBuffers(bufferIds.size, bufferIds, 0)
            }
        }
    }

    private data class UniformLocations(
        val viewMatrix: Int,
        val projectionMatrix: Int,
        val modelMatrix: Int,
        val color: Int,
    ) {
        companion object {
            fun fromProgram(programId: Int): UniformLocations = UniformLocations(
                viewMatrix = requireUniform(programId, "uViewMatrix"),
                projectionMatrix = requireUniform(programId, "uProjectionMatrix"),
                modelMatrix = requireUniform(programId, "uModelMatrix"),
                color = requireUniform(programId, "uColor"),
            )

            private fun requireUniform(programId: Int, name: String): Int {
                val location = GLES30.glGetUniformLocation(programId, name)
                check(location >= 0) { "Uniform not found in GL program: $name" }
                return location
            }
        }
    }


    private companion object {
        private const val TAG = "SpatialGlRenderer"
        private const val MODEL_TAG = "SpatialModelGL"
        val LegacyNavyClearColor = Color4(0.02f, 0.05f, 0.18f, 1f)
        private const val PositionAttributeLocation = 0
        private const val NormalAttributeLocation = 1
        private const val TexCoordAttributeLocation = 2
        private const val VERTEX_SHADER = "#version 300 es\n" +
                "layout(location = 0) in vec4 aPosition;\n" +
                "layout(location = 1) in vec3 aNormal;\n" +
                "layout(location = 2) in vec2 aTexCoord;\n" +
                "uniform mat4 uModelMatrix;\n" +
                "uniform mat4 uViewMatrix;\n" +
                "uniform mat4 uProjectionMatrix;\n" +
                "out vec3 vNormal;\n" +
                "out vec2 vTexCoord;\n" +
                "void main() {\n" +
                "  vNormal = aNormal;\n" +
                "  vTexCoord = aTexCoord;\n" +
                "  gl_Position = uProjectionMatrix * uViewMatrix * uModelMatrix * aPosition;\n" +
                "}"

        private const val FRAGMENT_SHADER = "#version 300 es\n" +
            "precision mediump float;\n" +
            "out vec4 fragColor;\n" +
            "uniform vec4 uColor;\n" +
            "void main() {\n" +
            "  fragColor = uColor;\n" +
            "}"

    }
}

internal data class MeshBufferAttributeAvailability(
    val hasNormals: Boolean,
    val hasTexCoords: Boolean,
    val normalFloatCount: Int,
    val texCoordFloatCount: Int,
)

internal fun MeshData.toMeshBufferAttributeAvailability(): MeshBufferAttributeAvailability =
    MeshBufferAttributeAvailability(
        hasNormals = normals.isNotEmpty(),
        hasTexCoords = texCoords.isNotEmpty(),
        normalFloatCount = normals.size,
        texCoordFloatCount = texCoords.size,
    )


internal fun orbitDistanceForVisualZoom(
    zoom: Float,
    baseDistance: Float = DefaultOrbitCameraDistance,
): Float {
    val safeZoom = if (zoom.isFinite()) {
        zoom.coerceIn(CameraSnapshot.MIN_ZOOM, CameraSnapshot.MAX_ZOOM)
    } else {
        1f
    }
    val safeBaseDistance = if (baseDistance.isFinite() && baseDistance > 0f) {
        baseDistance
    } else {
        DefaultOrbitCameraDistance
    }
    return safeBaseDistance / safeZoom
}

private const val DefaultOrbitCameraDistance = 10f
