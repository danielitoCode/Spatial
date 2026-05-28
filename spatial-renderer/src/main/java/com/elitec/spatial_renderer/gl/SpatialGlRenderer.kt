package com.elitec.spatial_renderer.gl

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import com.elitec.spatial_core.scene.RenderableNode

class SpatialGlRenderer : GLSurfaceView.Renderer {
    private var vertexBuffer: FloatBuffer? = null
    private var programId: Int = 0
    private var nodes: List<RenderableNode> = emptyList()
    private var cameraSnapshot: com.elitec.spatial_camera.CameraSnapshot = com.elitec.spatial_camera.CameraSnapshot()

    fun updateNodes(newNodes: List<RenderableNode>) {
        nodes = newNodes
    }

    fun updateCamera(snapshot: com.elitec.spatial_camera.CameraSnapshot) {
        cameraSnapshot = snapshot
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0.08f, 0.12f, 0.18f, 1.0f)
        programId = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)

        val bb = ByteBuffer.allocateDirect(TRIANGLE_VERTICES.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer().apply {
            put(TRIANGLE_VERTICES)
            position(0)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        
        if (nodes.isEmpty()) return

        GLES30.glUseProgram(programId)

        val viewMatrix = FloatArray(16)
        val projectionMatrix = FloatArray(16)
        
        // Configuración de cámara básica (Orbit)
        val eyeX = (cameraSnapshot.zoom * 10f * Math.sin(Math.toRadians(cameraSnapshot.yaw.toDouble())) * Math.cos(Math.toRadians(cameraSnapshot.pitch.toDouble()))).toFloat()
        val eyeY = (cameraSnapshot.zoom * 10f * Math.sin(Math.toRadians(cameraSnapshot.pitch.toDouble()))).toFloat()
        val eyeZ = (cameraSnapshot.zoom * 10f * Math.cos(Math.toRadians(cameraSnapshot.yaw.toDouble())) * Math.cos(Math.toRadians(cameraSnapshot.pitch.toDouble()))).toFloat()

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, 0f, 0f, 0f, 0f, 1f, 0f)
        
        val uViewLoc = GLES30.glGetUniformLocation(programId, "uViewMatrix")
        val uProjLoc = GLES30.glGetUniformLocation(programId, "uProjectionMatrix")
        val uModelLoc = GLES30.glGetUniformLocation(programId, "uModelMatrix")
        val uColorLoc = GLES30.glGetUniformLocation(programId, "uColor")

        GLES30.glUniformMatrix4fv(uViewLoc, 1, false, viewMatrix, 0)
        
        // Proyección básica
        Matrix.perspectiveM(projectionMatrix, 0, 45f, 1f, 0.1f, 100f)
        GLES30.glUniformMatrix4fv(uProjLoc, 1, false, projectionMatrix, 0)

        val buffer = checkNotNull(vertexBuffer)
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 3 * Float.SIZE_BYTES, buffer)
        
        nodes.forEach { node ->
            GLES30.glUniformMatrix4fv(uModelLoc, 1, false, node.modelMatrix, 0)
            GLES30.glUniform4f(uColorLoc, node.material.r, node.material.g, node.material.b, node.material.a)
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3)
        }

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

    private companion object {
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

        private val TRIANGLE_VERTICES = floatArrayOf(
            0.0f, 0.55f, 0.0f,
            -0.55f, -0.45f, 0.0f,
            0.55f, -0.45f, 0.0f
        )
    }
}