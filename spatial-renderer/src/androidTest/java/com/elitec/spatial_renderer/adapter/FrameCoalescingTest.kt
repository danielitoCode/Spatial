package com.elitec.spatial_renderer.adapter

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Verifica el ítem 2.2 de estabilidad: "Add frame request backpressure".
 * Este test asegura que el scheduler no sature el hilo de renderizado con tareas obsoletas
 * y que siempre entregue la información más reciente al hardware.
 */
@RunWith(AndroidJUnit4::class)
class FrameCoalescingTest {

    @Test
    fun testFrameCoalescingUsesLatestData() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val schedulerRef = arrayOf<ChoreographerFrameScheduler?>(null)

        // Inicializamos el scheduler en el hilo UI porque Choreographer.getInstance()
        // requiere un Looper activo.
        instrumentation.runOnMainSync {
            schedulerRef[0] = ChoreographerFrameScheduler()
        }
        val scheduler = schedulerRef[0]!!

        val callCount = AtomicInteger(0)
        val lastReceivedId = AtomicInteger(-1)
        val latch = CountDownLatch(1)

        instrumentation.runOnMainSync {
            // Simulamos 3 actualizaciones rápidas (p.ej. deltas de un gesto)
            // que ocurren antes del próximo VSYNC.

            // Llamada 1: Datos iniciales
            scheduler.requestFrame {
                callCount.incrementAndGet()
                lastReceivedId.set(1)
            }

            // Llamada 2: Actualización intermedia (debe ser coalescida/saltada)
            scheduler.requestFrame {
                callCount.incrementAndGet()
                lastReceivedId.set(2)
            }

            // Llamada 3: La actualización más reciente (la que debe ganar)
            scheduler.requestFrame {
                callCount.incrementAndGet()
                lastReceivedId.set(3)
                latch.countDown()
            }
        }

        // Esperamos al pulso de VSYNC (típico 16ms, usamos 500ms para seguridad en CI)
        val received = latch.await(500, TimeUnit.MILLISECONDS)

        assertEquals("El pulso de VSYNC nunca llegó o el scheduler se bloqueó", true, received)

        // VERIFICACIÓN 1: Coalescencia
        // No deben ejecutarse 3 frames. Solo 1 (el que estaba pendiente cuando llegó el VSYNC).
        assertEquals(
            "Se ejecutaron múltiples callbacks. El backpressure falló y está saturando el hilo.",
            1,
            callCount.get()
        )

        // VERIFICACIÓN 2: Frescura de datos
        // El único callback ejecutado DEBE contener el ID de la última llamada (3).
        assertEquals(
            "Se usaron datos obsoletos. El sistema no actualizó el callback con la última info.",
            3,
            lastReceivedId.get()
        )
    }
}