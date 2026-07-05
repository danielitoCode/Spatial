package com.elitec.spatial_renderer.gl

import android.opengl.GLSurfaceView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.render.Color4
import com.elitec.spatial_core.scene.RenderableNode
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Core #1 Stability item 3.2: `cube_is_visible_on_first_frame`.
 *
 * This must run as an *instrumented* test (`./gradlew :spatial-renderer:connectedAndroidTest`) on a
 * real device or emulator: it drives real EGL/GLES30 calls through [SpatialGlRenderer], which a plain
 * JVM unit test cannot do (there is no GPU/EGL context available on the host JVM). It is intentionally
 * kept out of `src/test` for that reason - see CORE1_STABILITY.md item 3.2 for the audit note.
 *
 * The test wraps [SpatialGlRenderer] with a delegating [GLSurfaceView.Renderer] that snapshots the
 * framebuffer with `glReadPixels` right after the real `onDrawFrame()` call returns, then asserts that
 * at least one pixel differs from the configured (distinctive) clear color - i.e. the cube actually
 * drew triangles instead of the screen staying a flat background.
 */
@RunWith(AndroidJUnit4::class)
class CubeRendersOnFirstFrameTest {

    @Test
    fun cube_is_visible_on_first_frame() {
        val latch = CountDownLatch(1)
        val framebufferRef = AtomicReference<IntArray>()
        val sizeRef = AtomicReference<Pair<Int, Int>>()

        val distinctiveClearColor = Color4(0f, 0f, 0f, 1f) // pure black background
        val cubeMaterialColor = com.elitec.spatial_core.scene.MaterialData(r = 1f, g = 1f, b = 1f, a = 1f)

        val cubeNode = RenderableNode(
            meshId = PrimitiveMeshIds.Cube,
            material = cubeMaterialColor,
        )

        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val glSurfaceView = object : GLSurfaceView(instrumentation.targetContext) {}
            glSurfaceView.setEGLContextClientVersion(3)

            val spatialRenderer = SpatialGlRenderer()
            spatialRenderer.updateNodes(listOf(cubeNode))
            spatialRenderer.updateCamera(CameraSnapshot(yaw = 30f, pitch = 20f, zoom = 1f))
            spatialRenderer.updateClearColor(distinctiveClearColor)

            val capturingRenderer = object : GLSurfaceView.Renderer {
                private var width = 0
                private var height = 0

                override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                    spatialRenderer.onSurfaceCreated(gl, config)
                }

                override fun onSurfaceChanged(gl: GL10?, w: Int, h: Int) {
                    width = w
                    height = h
                    spatialRenderer.onSurfaceChanged(gl, w, h)
                }

                override fun onDrawFrame(gl: GL10?) {
                    spatialRenderer.onDrawFrame(gl)

                    if (framebufferRef.get() == null && width > 0 && height > 0) {
                        val pixels = readFramebufferPixels(width, height)
                        framebufferRef.set(pixels)
                        sizeRef.set(width to height)
                        latch.countDown()
                    }
                }
            }

            glSurfaceView.setRenderer(capturingRenderer)
            glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }

        val rendered = latch.await(5, TimeUnit.SECONDS)
        assertTrue("Timed out waiting for a frame to be captured from the GL thread", rendered)

        val pixels = framebufferRef.get()
        requireNotNull(pixels) { "No framebuffer was captured" }

        val backgroundArgb = distinctiveClearColor.toArgbInt()
        val nonBackgroundPixelCount = pixels.count { it != backgroundArgb }

        assertTrue(
            "Expected at least one non-background pixel (the cube should have drawn something), " +
                "but the framebuffer only contained the clear color",
            nonBackgroundPixelCount > 0,
        )
    }

    /** Reads the currently-bound GL framebuffer as packed ARGB ints (top-left origin). */
    private fun readFramebufferPixels(width: Int, height: Int): IntArray {
        val buffer = java.nio.ByteBuffer.allocateDirect(width * height * 4)
            .order(java.nio.ByteOrder.nativeOrder())
        android.opengl.GLES30.glReadPixels(
            0, 0, width, height,
            android.opengl.GLES30.GL_RGBA, android.opengl.GLES30.GL_UNSIGNED_BYTE, buffer,
        )
        buffer.rewind()
        val out = IntArray(width * height)
        for (i in 0 until width * height) {
            val r = buffer.get().toInt() and 0xFF
            val g = buffer.get().toInt() and 0xFF
            val b = buffer.get().toInt() and 0xFF
            val a = buffer.get().toInt() and 0xFF
            out[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }
        return out
    }

    private fun Color4.toArgbInt(): Int {
        val r = (this.r * 255f).toInt() and 0xFF
        val g = (this.g * 255f).toInt() and 0xFF
        val b = (this.b * 255f).toInt() and 0xFF
        val a = (this.a * 255f).toInt() and 0xFF
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }
}
