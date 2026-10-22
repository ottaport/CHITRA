package com.ottaport.chitra.model.rig

import com.ottaport.chitra.model.Vector2D
import kotlinx.serialization.Serializable

@Serializable
data class Bone(
    val id: String,
    val name: String,
    val parentId: String? = null,
    val localPosition: Vector2D = Vector2D.Zero,
    val localAngle: Float = 0f, // in degrees
    val length: Float = 80f,
    val isIkTarget: Boolean = false,
    val ikChainLength: Int = 2,
    val ikPoleDirection: Float = 1f, // +1.0 for standard bend, -1.0 for inverted
    val ikTargetPosition: Vector2D? = null,
    val isLocked: Boolean = false,
    val colorHex: Long = 0xFF2563EB
) {
    // Computed world transforms for rendering and child propagation
    var worldPosition: Vector2D = Vector2D.Zero
    var worldAngle: Float = 0f // in degrees
    var worldEndPosition: Vector2D = Vector2D.Zero

    fun updateWorldTransform(parent: Bone?) {
        if (parent == null) {
            worldPosition = localPosition
            worldAngle = localAngle
        } else {
            // Attached to parent end position with local angle offset
            worldPosition = parent.worldEndPosition + localPosition
            worldAngle = parent.worldAngle + localAngle
        }
        val rad = Math.toRadians(worldAngle.toDouble()).toFloat()
        worldEndPosition = worldPosition + Vector2D.fromAngle(rad, length)
    }

    fun containsPoint(point: Vector2D, threshold: Float = 24f): Boolean {
        // Point-to-segment distance check
        val seg = worldEndPosition - worldPosition
        val segLenSq = seg.x * seg.x + seg.y * seg.y
        if (segLenSq < 0.001f) {
            return point.distance(worldPosition) <= threshold
        }
        val t = ((point.x - worldPosition.x) * seg.x + (point.y - worldPosition.y) * seg.y) / segLenSq
        val clampedT = t.coerceIn(0f, 1f)
        val proj = worldPosition + seg * clampedT
        return point.distance(proj) <= threshold
    }

    fun isJointNear(point: Vector2D, threshold: Float = 28f): Boolean {
        return point.distance(worldEndPosition) <= threshold
    }

    fun isRootNear(point: Vector2D, threshold: Float = 28f): Boolean {
        return point.distance(worldPosition) <= threshold
    }
}
