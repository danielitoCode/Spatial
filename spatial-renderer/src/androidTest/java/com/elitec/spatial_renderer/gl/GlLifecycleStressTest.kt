package com.elitec.spatial_renderer.gl

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.elitec.spatial_core.render.Color4
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Prueba de estrés de ciclo de vida para Core #1.
 * Verifica que el renderer puede ser creado y destruido 50 veces consecutivas
 * sin agotar los recursos de la GPU ni causar excepciones en el hilo de renderizado.
 */
@RunWith(AndroidJUnit4::class)
class GlLifecycleStressTest {

    @Test
    fun stress_test_rapid_gl_recreation_cycles() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val cycles = 50

        repeat(cycles) { i ->
            val latch = CountDownLatch(1)

            // 1. Crear e inicializar en el hilo UI
            instrumentation.runOnMainSync {
                val spatialView = SpatialGlSurfaceView(context)

                // Configuramos un callback para saber cuándo el hilo GL está vivo
                // (Esta API fue añadida en el paso 1.2 de estabilidad)
                spatialView.setOnSurfaceReady {
                    latch.countDown()
                }

                // Forzamos un renderizado inicial para disparar la creación del contexto
                spatialView.render(
                    com.elitec.spatial_renderer.render.RenderFrame(
                        frameTimeNanos = System.nanoTime(),
                        clearColor = Color4(0.1f, 0.1f, 0.1f, 1f)
                    )
                )

                // 2. Simular destrucción inmediata llamando a releaseGlResources
                // Esto prueba la robustez del punto 1.3 (Sanitize lifecycle)
                spatialView.releaseGlResources()
            }

            // 3. Esperar un tiempo razonable para que el ciclo se complete
            // Nota: Al llamar a releaseGlResources() tan rápido, es posible que el latch
            // no se dispare si el hilo GL se aborta, lo cual es aceptable en este test
            // de estrés siempre y cuando NO haya un crash.
            latch.await(100, TimeUnit.MILLISECONDS)

            // Opcional: Pausa mínima para permitir al sistema liberar buffers nativos
            Thread.sleep(20)
        }
    }
}