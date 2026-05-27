package com.elitec.spatial_core.render

/** Vector inmutable de 3 componentes. Ownership: value-object, sin mutación compartida. */
data class Vec3(
    val x: Float,
    val y: Float,
    val z: Float,
) {
    companion object {
        val ZERO = Vec3(0f, 0f, 0f)
    }
}

/** Color RGBA inmutable. Ownership: value-object, copy-by-value entre productor/consumidor. */
data class Color4(
    val r: Float,
    val g: Float,
    val b: Float,
    val a: Float,
) {
    companion object {
        val BLACK = Color4(0f, 0f, 0f, 1f)
    }
}

/**
 * Matriz 4x4 inmutable.
 *
 * Política de ownership:
 * - [raw] es borrowed en la factory y se copia defensivamente (copy-on-write al crear).
 * - Internamente nunca expone referencia mutable al array.
 */
class Mat4 private constructor(
    private val values: FloatArray,
) {
    fun toFloatArray(): FloatArray = values.copyOf()

    operator fun get(index: Int): Float = values[index]

    override fun equals(other: Any?): Boolean = other is Mat4 && values.contentEquals(other.values)

    override fun hashCode(): Int = values.contentHashCode()

    companion object {
        fun identity(): Mat4 = from(
            floatArrayOf(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f,
            ),
        )

        fun from(raw: FloatArray): Mat4 {
            require(raw.size == 16) { "Mat4 expects 16 values, received ${raw.size}" }
            return Mat4(raw.copyOf())
        }
    }
}