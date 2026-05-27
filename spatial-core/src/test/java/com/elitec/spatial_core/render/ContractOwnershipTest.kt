package com.elitec.spatial_core.render

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class ContractOwnershipTest {

    @Test
    fun `mat4 copies source array defensively`() {
        val producerArray = FloatArray(16) { it.toFloat() }
        val mat4 = Mat4.from(producerArray)

        producerArray[0] = 999f

        assertEquals(0f, mat4[0])
        val consumerCopy = mat4.toFloatArray()
        consumerCopy[1] = -1f
        assertEquals(1f, mat4[1])
    }

    @Test
    fun `stable resources can reuse buffer without list recreation`() {
        val previous = StableFrameResources.copyFrom(listOf("a", "b", "c"))

        val reused = StableFrameResources.copyFrom(listOf("x", "y"), previous)

        assertSame(previous, reused)
        assertEquals(2, reused.size)
        assertEquals("x", reused[0])
        assertEquals("y", reused[1])
    }

    @Test
    fun `stable resources isolate producer collection mutations`() {
        val producer = mutableListOf("r1", "r2")
        val snapshotResources = StableFrameResources.copyFrom(producer)

        producer[0] = "mutated"
        producer.add("r3")

        assertEquals(2, snapshotResources.size)
        assertEquals("r1", snapshotResources[0])
        assertEquals("r2", snapshotResources[1])
    }
}