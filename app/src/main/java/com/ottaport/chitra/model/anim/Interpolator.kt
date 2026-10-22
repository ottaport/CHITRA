package com.ottaport.chitra.model.anim

import com.ottaport.chitra.model.Vector2D
import com.ottaport.chitra.model.expression.FacialExpression
import com.ottaport.chitra.model.layer.RigLayer
import com.ottaport.chitra.model.rig.Bone
import com.ottaport.chitra.model.rig.Skeleton

object Interpolator {

    fun interpolateAngleDeg(start: Float, end: Float, fraction: Float): Float {
        var diff = (end - start) % 360f
        if (diff > 180f) diff -= 360f
        if (diff < -180f) diff += 360f
        return start + diff * fraction
    }

    fun applyCurve(t: Float, type: InterpolationType): Float {
        val clamped = t.coerceIn(0f, 1f)
        return when (type) {
            InterpolationType.LINEAR -> clamped
            InterpolationType.EASE_IN_OUT -> {
                // Smoothstep cubic hermite: 3t^2 - 2t^3
                clamped * clamped * (3f - 2f * clamped)
            }
            InterpolationType.STEP -> if (clamped >= 1f) 1f else 0f
        }
    }

    data class EvaluatedPose(
        val skeleton: Skeleton,
        val layers: List<RigLayer>,
        val expression: FacialExpression
    )

    fun evaluatePoseAtFrame(
        frame: Float,
        keyframes: List<Keyframe>,
        baseSkeleton: Skeleton,
        baseLayers: List<RigLayer>
    ): EvaluatedPose {
        if (keyframes.isEmpty()) {
            return EvaluatedPose(
                skeleton = baseSkeleton.updateAllWorldTransforms(),
                layers = baseLayers,
                expression = FacialExpression()
            )
        }

        val sortedKeys = keyframes.sortedBy { it.frame }

        // Find surrounding keyframes
        val prevKey = sortedKeys.lastOrNull { it.frame <= frame } ?: sortedKeys.first()
        val nextKey = sortedKeys.firstOrNull { it.frame > frame } ?: sortedKeys.last()

        val fraction = if (prevKey.frame == nextKey.frame) {
            0f
        } else {
            val rawFraction = (frame - prevKey.frame) / (nextKey.frame - prevKey.frame).toFloat()
            applyCurve(rawFraction, prevKey.interpolation)
        }

        // Interpolate Bone angles and positions
        val updatedBones = baseSkeleton.bones.map { bone ->
            val angleA = prevKey.boneAngles[bone.id] ?: bone.localAngle
            val angleB = nextKey.boneAngles[bone.id] ?: bone.localAngle
            val interpAngle = interpolateAngleDeg(angleA, angleB, fraction)

            val posA = prevKey.bonePositions[bone.id] ?: bone.localPosition
            val posB = nextKey.bonePositions[bone.id] ?: bone.localPosition
            val interpPos = Vector2D.lerp(posA, posB, fraction)

            val ikTargetA = prevKey.ikTargets[bone.id] ?: bone.ikTargetPosition
            val ikTargetB = nextKey.ikTargets[bone.id] ?: bone.ikTargetPosition
            val interpIkTarget = if (ikTargetA != null && ikTargetB != null) {
                Vector2D.lerp(ikTargetA, ikTargetB, fraction)
            } else ikTargetA ?: ikTargetB

            bone.copy(
                localAngle = interpAngle,
                localPosition = interpPos,
                ikTargetPosition = interpIkTarget
            )
        }

        val evaluatedSkeleton = baseSkeleton.copy(bones = updatedBones).updateAllWorldTransforms()

        // Interpolate Layers (mesh offsets, opacities)
        val updatedLayers = baseLayers.map { layer ->
            val opacityA = prevKey.layerOpacities[layer.id] ?: layer.opacity
            val opacityB = nextKey.layerOpacities[layer.id] ?: layer.opacity
            val interpOpacity = opacityA + (opacityB - opacityA) * fraction

            val meshMapA = prevKey.meshOffsets[layer.id] ?: emptyMap()
            val meshMapB = nextKey.meshOffsets[layer.id] ?: emptyMap()

            val updatedVertices = layer.meshGrid.vertices.map { v ->
                val offA = meshMapA[v.id] ?: v.offset
                val offB = meshMapB[v.id] ?: v.offset
                v.copy(offset = Vector2D.lerp(offA, offB, fraction))
            }

            layer.copy(
                opacity = interpOpacity,
                meshGrid = layer.meshGrid.copy(vertices = updatedVertices)
            )
        }

        // Interpolate Facial Expression
        val interpExpression = FacialExpression.lerp(prevKey.expression, nextKey.expression, fraction)

        return EvaluatedPose(
            skeleton = evaluatedSkeleton,
            layers = updatedLayers,
            expression = interpExpression
        )
    }
}
