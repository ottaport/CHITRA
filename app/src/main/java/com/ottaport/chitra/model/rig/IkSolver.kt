package com.ottaport.chitra.model.rig

import com.ottaport.chitra.model.Vector2D
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.sqrt

object IkSolver {

    data class IkResult(
        val bone1AngleDeg: Float,
        val bone2AngleDeg: Float
    )

    /**
     * Solves 2-bone analytical IK.
     * @param origin Root joint position of bone 1
     * @param target Desired end-effector position of bone 2
     * @param len1 Length of bone 1
     * @param len2 Length of bone 2
     * @param parentAngleDeg World angle of the parent bone feeding into bone 1 (or 0 if root)
     * @param poleBendDirection +1.0 for standard bend (e.g. elbow forward / knee forward), -1.0 for reverse
     */
    fun solveTwoBoneIk(
        origin: Vector2D,
        target: Vector2D,
        len1: Float,
        len2: Float,
        parentAngleDeg: Float = 0f,
        poleBendDirection: Float = 1f
    ): IkResult {
        val diff = target - origin
        val dist = diff.length().coerceAtLeast(0.001f)
        val maxReach = len1 + len2
        val minReach = kotlin.math.abs(len1 - len2)

        // Clamped distance for triangle validity
        val clampedDist = dist.coerceIn(minReach + 0.001f, maxReach - 0.001f)

        // Direction to target
        val angleToTarget = atan2(diff.y, diff.x)

        // Law of cosines for angle at bone 1 joint (angle between line to target and bone 1)
        val cosAlpha = ((len1 * len1 + clampedDist * clampedDist - len2 * len2) / (2f * len1 * clampedDist)).coerceIn(-1f, 1f)
        val alpha = acos(cosAlpha)

        // Law of cosines for interior angle at elbow/knee joint between bone 1 and bone 2
        val cosBeta = ((len1 * len1 + len2 * len2 - clampedDist * clampedDist) / (2f * len1 * len2)).coerceIn(-1f, 1f)
        val interiorAngle = acos(cosBeta)

        // Pole multiplier: +1 or -1
        val pole = if (poleBendDirection >= 0f) 1f else -1f

        // World angle of bone 1
        val bone1WorldRad = angleToTarget - pole * alpha
        val bone1WorldDeg = Math.toDegrees(bone1WorldRad.toDouble()).toFloat()

        // Local angle of bone 2 relative to bone 1 (bend at joint)
        val bone2RelRad = pole * (Math.PI.toFloat() - interiorAngle)
        val bone2RelDeg = Math.toDegrees(bone2RelRad.toDouble()).toFloat()

        // Local angle of bone 1 relative to parent
        val bone1LocalDeg = normalizeDeg(bone1WorldDeg - parentAngleDeg)

        return IkResult(
            bone1AngleDeg = bone1LocalDeg,
            bone2AngleDeg = normalizeDeg(bone2RelDeg)
        )
    }

    private fun normalizeDeg(degrees: Float): Float {
        var a = degrees % 360f
        if (a > 180f) a -= 360f
        if (a < -180f) a += 360f
        return a
    }
}
