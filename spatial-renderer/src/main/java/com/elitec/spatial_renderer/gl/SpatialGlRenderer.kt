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
import com.elitec.spatial_core.scene.RenderableNode

class SpatialGlRenderer : GLSurfaceView.Renderer {
    private val meshRegistry = PrimitiveMeshRegistry()
    private var meshBuffers: Map<String, GlMeshBuffers> = emptyMap()
    private var programId: Int = 0
    private var nodes: List<RenderableNode> = emptyList()
    private var cameraSnapshot: CameraSnapshot = CameraSnapshot()
    private var aspectRatio: Float = 1f
    private var uniforms: UniformLocations? = null

    /** Called once GL surface is fully initialized so the host can trigger a first render pass. */
    var onSurfaceReadyCallback: (() -> Unit)? = null

    fun updateNodes(newNodes: List<RenderableNode>) {
        nodes = newNodes
    }

    fun updateCamera(snapshot: CameraSnapshot) {
        cameraSnapshot = snapshot
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        releaseGlResources()

        GLES30.glClearColor(0.08f, 0.12f, 0.18f, 1.0f)
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        programId = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        uniforms = UniformLocations.fromProgram(programId)

        meshBuffers = PrimitiveMeshRegistry.defaultMeshes().mapValues { (_, meshData) ->
            meshData.toGlMeshBuffers()
        }

        onSurfaceReadyCallback?.invoke()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        aspectRatio = if (height == 0) 1f else width.toFloat() / height.toFloat()
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        if (nodes.isEmpty() || programId == 0) return

        val uniformLocations = uniforms ?: return

        GLES30.glUseProgram(programId)

        val viewMatrix = FloatArray(16)
        val projectionMatrix = FloatArray(16)

        // Configuración de cámara básica (Orbit). CameraSnapshot.zoom is visual magnification,
        // so orbital distance is inversely proportional to zoom.
        val orbitDistance = orbitDistanceForVisualZoom(cameraSnapshot.zoom)
        val eyeX = (orbitDistance * Math.sin(Math.toRadians(cameraSnapshot.yaw.toDouble())) * Math.cos(Math.toRadians(cameraSnapshot.pitch.toDouble()))).toFloat()
        val eyeY = (orbitDistance * Math.sin(Math.toRadians(cameraSnapshot.pitch.toDouble()))).toFloat()
        val eyeZ = (orbitDistance * Math.cos(Math.toRadians(cameraSnapshot.yaw.toDouble())) * Math.cos(Math.toRadians(cameraSnapshot.pitch.toDouble()))).toFloat()

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, 0f, 0f, 0f, 0f, 1f, 0f)

        GLES30.glUniformMatrix4fv(uniformLocations.viewMatrix, 1, false, viewMatrix, 0)

        // Proyección básica
        Matrix.perspectiveM(projectionMatrix, 0, 45f, aspectRatio, 0.1f, 100f)
        GLES30.glUniformMatrix4fv(uniformLocations.projectionMatrix, 1, false, projectionMatrix, 0)

        GLES30.glEnableVertexAttribArray(0)

        nodes.forEach { node ->
            if (meshRegistry.resolveOrNull(node.meshId) == null) {
                Log.w(TAG, "Skipping renderable with unknown primitive mesh id: ${node.meshId}")
                return@forEach
            }

            val mesh = meshBuffers[node.meshId]
            if (mesh == null) {
                Log.w(TAG, "Skipping renderable because GL buffers are missing for mesh id: ${node.meshId}")
                return@forEach
            }

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mesh.vertexBufferId)
            GLES30.glVertexAttribPointer(
                0,
                MeshData.CoordinatesPerVertex,
                GLES30.GL_FLOAT,
                false,
                MeshData.CoordinatesPerVertex * Float.SIZE_BYTES,
                0,
            )

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
        }
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)

        GLES30.glDisableVertexAttribArray(0)
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
        meshBuffers = emptyMap()

        if (programId != 0) {
            GLES30.glDeleteProgram(programId)
            programId = 0
        }
        uniforms = null
    }

    private fun MeshData.toGlMeshBuffers(): GlMeshBuffers {
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertices)
                position(0)
            }
        val vertexBufferIds = IntArray(1)
        GLES30.glGenBuffers(1, vertexBufferIds, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vertexBufferIds[0])
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            vertices.size * Float.SIZE_BYTES,
            vertexBuffer,
            GLES30.GL_STATIC_DRAW,
        )
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)

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
            vertexBufferId = vertexBufferIds[0],
            indexBufferId = indexBufferId,
            vertexCount = vertexCount,
            indexCount = indexCount,
            drawMode = drawMode,
        )
    }

    private fun MeshDrawMode.toGlDrawMode(): Int = when (this) {
        MeshDrawMode.Triangles -> GLES30.GL_TRIANGLES
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
        val indexBufferId: Int,
        val vertexCount: Int,
        val indexCount: Int,
        val drawMode: MeshDrawMode,
    ) {
        fun release() {
            val bufferIds = intArrayOf(vertexBufferId, indexBufferId).filter { it != 0 }.toIntArray()
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
        private const val VERTEX_SHADER = "#version 300 es\n" +
            "layout (location = 0) in vec4 aPosition;\n" +
            "uniform mat4 uModelMatrix;\n" +
            "uniform mat4 uViewMatrix;\n" +
            "uniform mat4 uProjectionMatrix;\n" +
            "void main() {\n" +
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