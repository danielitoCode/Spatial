package com.elitec.spatial_units

import kotlin.math.PI

@JvmInline
value class Distance private constructor(val meters: Float) {
    companion object {
        fun meters(value: Float): Distance = Distance(value)
        fun centimeters(value: Float): Distance = Distance(value / 100f)
    }

    operator fun plus(other: Distance): Distance = Distance(meters + other.meters)
    operator fun minus(other: Distance): Distance = Distance(meters - other.meters)
}

@JvmInline
value class Angle private constructor(val radians: Float) {
    companion object {
        fun radians(value: Float): Angle = Angle(value)
        fun degrees(value: Float): Angle = Angle((value * PI / 180.0).toFloat())
    }
}

val Int.meters: Distance get() = Distance.meters(toFloat())
val Float.meters: Distance get() = Distance.meters(this)
val Int.cm: Distance get() = Distance.centimeters(toFloat())
val Float.cm: Distance get() = Distance.centimeters(this)
val Int.deg: Angle get() = Angle.degrees(toFloat())
val Float.deg: Angle get() = Angle.degrees(this)