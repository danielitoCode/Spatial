package com.elitec.spatial_core.render

/** Vista estable de recursos para evitar recrear listas completas por frame. */
class StableFrameResources private constructor(
    private val buffer: Array<String?>,
    var size: Int,
) : Iterable<String> {

    override fun iterator(): Iterator<String> = object : Iterator<String> {
        private var index = 0
        override fun hasNext(): Boolean = index < size
        override fun next(): String = requireNotNull(buffer[index++])
    }

    operator fun get(index: Int): String {
        require(index in 0 until size) { "index out of bounds: $index" }
        return requireNotNull(buffer[index])
    }

    fun copyAndResize(newCapacity: Int): StableFrameResources {
        val copy = Array<String?>(newCapacity) { null }
        val elements = minOf(size, newCapacity)
        for (i in 0 until elements) {
            copy[i] = buffer[i]
        }
        return StableFrameResources(copy, elements)
    }

    companion object {
        fun empty(capacity: Int = 0): StableFrameResources = StableFrameResources(Array(capacity) { null }, 0)

        /**
         * Reusa [target] cuando tiene capacidad suficiente y evita crear una lista nueva por frame.
         *
         * Política de ownership:
         * - [source] es borrowed y sólo se lee.
         * - El resultado pertenece al productor; el consumidor lo trata como read-only.
         */
        fun copyFrom(source: Collection<String>, target: StableFrameResources? = null): StableFrameResources {
            val reused = target?.takeIf { it.buffer.size >= source.size } ?: empty(source.size)
            var i = 0
            source.forEach { item ->
                reused.buffer[i++] = item
            }
            for (idx in i until reused.size) {
                reused.buffer[idx] = null
            }
            reused.size = i
            return reused
        }
    }
}