package com.ottaport.chitra.model

import kotlinx.serialization.Serializable
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Serializable
data class Vector2D(
    val x: Float = 0f,
    val y: Float = 0f
) {
    operator fun plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)
    operator fun minus(other: Vector2D) = Vector2D(x - other.x, y - other.y)
    operator fun times(scalar: Float) = Vector2D(x * scalar, y * scalar)
    operator fun div(scalar: Float) = if (scalar != 0f) Vector2D(x / scalar, y / scalar) else Vector2D()

    fun length(): Float = sqrt(x * x + y * y)

    fun distance(other: Vector2D): Float = (this - other).length()

    fun normalized(): Vector2D {
        val len = length()
        return if (len > 0.0001f) Vector2D(x / len, y / len) else Vector2D(1f, 0f)
    }

    fun angle(): Float = atan2(y, x)

    fun rotated(radians: Float): Vector2D {
        val cosA = cos(radians)
        val sinA = sin(radians)
        return Vector2D(
            x * cosA - y * sinA,
            x * sinA + y * cosA
        )
    }

    companion object {
        val Zero = Vector2D(0f, 0f)
        val One = Vector2D(1f, 1f)

        fun fromAngle(radians: Float, length: Float = 1f): Vector2D {
            return Vector2D(cos(radians) * length, sin(radians) * length)
        }

        fun lerp(start: Vector2D, end: Vector2D, fraction: Float): Vector2D {
            return Vector2D(
                start.x + (end.x - start.x) * fraction,
                start.y + (end.y - start.y) * fraction
            )
        }
    }
}
