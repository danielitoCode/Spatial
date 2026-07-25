package com.elitec.spatial_renderer.gl

import android.opengl.GLSurfaceView
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.elitec.spatial_core.camera.CameraSnapshot
import com.elitec.spatial_core.render.Color4
import com.elitec.spatial_core.scene.MaterialData
import com.elitec.spatial_core.scene.RenderableNode
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Core #1 device-closure task 1.1 / tracker item 3.2: `cube_is_visible_on_first_frame`.
 *
 * Sub-steps covered by this test:
 * - 1.1.1 Instrument exists under androidTest (not JVM unit test)
 * - 1.1.2 Flow: onSurfaceCreated → onSurfaceChanged → first onDrawFrame → glReadPixels
 * - 1.1.3 Distinctive clear color (magenta) vs cube color (white)
 * - 1.1.4 / 1.1.5 Must be executed on device/emulator by a human or CI with emulator:
 *   `./gradlew :spatial-renderer:connectedDebugAndroidTest`
 *
 * Canonical tracker: `roadmap/CORE1_STABILITY.md`.
 * Do not mark tracker Done until this test PASSes on a real device (×3 cold runs recommended).
 */
@RunWith(AndroidJUnit4::class)
class CubeRendersOnFirstFrameTest {

    @Test
    fun cube_is_visible_on_first_frame() {
        val latch = CountDownLatch(1)
        val framebufferRef = AtomicReference<IntArray>()
        val captured = AtomicBoolean(false)

        // Distinctive clear (not black/navy defaults) so false positives are unlikely.
        val distinctiveClearColor = Color4(1f, 0f, 1f, 1f) // magenta
        val cubeMaterial = MaterialData(r = 1f, g = 1f, b = 1f, a = 1f)

        val cubeNode = RenderableNode(
            meshId = PrimitiveMeshIds.Cube,
            material = cubeMaterial,
        )

        val scenario = ActivityScenario.launch(FirstFrameTestActivity::class.java)
        try {
            scenario.onActivity { activity ->
                val glSurfaceView = GLSurfaceView(activity)
                glSurfaceView.setEGLContextClientVersion(3)
                glSurfaceView.layoutParams = ViewGroup.LayoutParams(256, 256)

                val spatialRenderer = SpatialGlRenderer()
                spatialRenderer.updateNodes(listOf(cubeNode))
                // Slight orbit so the cube is not edge-on to a face-only degenerate case.
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

                        // Capture only the first frame after a valid viewport exists.
                        if (captured.compareAndSet(false, true) && width > 0 && height > 0) {
                            val pixels = readFramebufferPixels(width, height)
                            framebufferRef.set(pixels)
                            latch.countDown()
                        }
                    }
                }

                glSurfaceView.setRenderer(capturingRenderer)
                glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                activity.setContentView(glSurfaceView)
            }

            val rendered = latch.await(10, TimeUnit.SECONDS)
            assertTrue(
                "Timed out waiting for the first GL frame (surface not created or onDrawFrame never ran)",
                rendered,
            )

            val pixels = framebufferRef.get()
            requireNotNull(pixels) { "No framebuffer was captured after first onDrawFrame" }

            val backgroundArgb = distinctiveClearColor.toArgbInt()
            val nonBackgroundPixelCount = pixels.count { it != backgroundArgb }

            assertTrue(
                "Expected at least one non-magenta pixel (white cube triangles). " +
                    "Framebuffer was only the clear color — cube did not draw on the first frame.",
                nonBackgroundPixelCount > 0,
            )
        } finally {
            scenario.close()
        }
    }

    /** Reads the currently-bound GL framebuffer as packed ARGB ints. */
    private fun readFramebufferPixels(width: Int, height: Int): IntArray {
        val buffer = java.nio.ByteBuffer.allocateDirect(width * height * 4)
            .order(java.nio.ByteOrder.nativeOrder())
        android.opengl.GLES30.glReadPixels(
            0, 0, width, height,
            android.opengl.GLES30.GL_RGBA,
            android.opengl.GLES30.GL_UNSIGNED_BYTE,
            buffer,
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
        val r = (this.r * 255f).toInt().coerceIn(0, 255)
        val g = (this.g * 255f).toInt().coerceIn(0, 255)
        val b = (this.b * 255f).toInt().coerceIn(0, 255)
        val a = (this.a * 255f).toInt().coerceIn(0, 255)
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }
}
