package com.ottaport.chitra.model.anim

import com.ottaport.chitra.model.Vector2D
import com.ottaport.chitra.model.expression.FacialExpression
import kotlinx.serialization.Serializable

@Serializable
enum class InterpolationType(val displayName: String) {
    EASE_IN_OUT("Smooth Ease"),
    LINEAR("Linear"),
    STEP("Step / Hold")
}

@Serializable
data class Keyframe(
    val frame: Int,
    val interpolation: InterpolationType = InterpolationType.EASE_IN_OUT,
    val boneAngles: Map<String, Float> = emptyMap(),
    val bonePositions: Map<String, Vector2D> = emptyMap(),
    val ikTargets: Map<String, Vector2D> = emptyMap(),
    val meshOffsets: Map<String, Map<Int, Vector2D>> = emptyMap(),
    val layerOpacities: Map<String, Float> = emptyMap(),
    val expression: FacialExpression = FacialExpression()
)
