package com.ottaport.chitra.model.expression

import kotlinx.serialization.Serializable

@Serializable
enum class ExpressionPreset(val label: String) {
    NEUTRAL("Neutral"),
    HAPPY("Joy / Happy"),
    WINK("Wink / Playful"),
    SHOCKED("Shocked / Wide"),
    ANGRY("Tsundere / Angry"),
    SAD("Sad / Pout"),
    BLUSHING("Blushing")
}

@Serializable
data class FacialExpression(
    val preset: ExpressionPreset = ExpressionPreset.NEUTRAL,
    val eyeOpenness: Float = 1.0f, // 0.0 = closed/blink, 1.0 = open
    val isLeftWink: Boolean = false,
    val isRightWink: Boolean = false,
    val mouthSmile: Float = 0.2f, // -1.0 to 1.0
    val mouthOpenness: Float = 0.0f, // 0.0 = shut, 1.0 = open talk
    val mouthShape: String = "Neutral", // Neutral, A, I, U, E, O
    val browAngle: Float = 0.0f, // -25 to +25 degrees
    val browHeight: Float = 0.0f, // -10 to +10 px
    val blushIntensity: Float = 0.15f // 0.0 to 1.0
) {
    companion object {
        fun fromPreset(preset: ExpressionPreset): FacialExpression {
            return when (preset) {
                ExpressionPreset.NEUTRAL -> FacialExpression(
                    preset = preset,
                    eyeOpenness = 1.0f,
                    mouthSmile = 0.1f,
                    mouthOpenness = 0.0f,
                    browAngle = 0.0f,
                    blushIntensity = 0.1f
                )
                ExpressionPreset.HAPPY -> FacialExpression(
                    preset = preset,
                    eyeOpenness = 0.85f,
                    mouthSmile = 0.85f,
                    mouthOpenness = 0.45f,
                    browAngle = -10.0f,
                    blushIntensity = 0.5f
                )
                ExpressionPreset.WINK -> FacialExpression(
                    preset = preset,
                    eyeOpenness = 1.0f,
                    isLeftWink = true,
                    mouthSmile = 0.7f,
                    mouthOpenness = 0.2f,
                    browAngle = -5.0f,
                    blushIntensity = 0.4f
                )
                ExpressionPreset.SHOCKED -> FacialExpression(
                    preset = preset,
                    eyeOpenness = 1.0f,
                    mouthSmile = -0.1f,
                    mouthOpenness = 0.9f,
                    browAngle = 18.0f,
                    browHeight = 8.0f,
                    blushIntensity = 0.0f
                )
                ExpressionPreset.ANGRY -> FacialExpression(
                    preset = preset,
                    eyeOpenness = 0.75f,
                    mouthSmile = -0.8f,
                    mouthOpenness = 0.2f,
                    browAngle = 22.0f,
                    browHeight = -4.0f,
                    blushIntensity = 0.6f
                )
                ExpressionPreset.SAD -> FacialExpression(
                    preset = preset,
                    eyeOpenness = 0.6f,
                    mouthSmile = -0.6f,
                    mouthOpenness = 0.1f,
                    browAngle = -18.0f,
                    browHeight = 2.0f,
                    blushIntensity = 0.2f
                )
                ExpressionPreset.BLUSHING -> FacialExpression(
                    preset = preset,
                    eyeOpenness = 0.7f,
                    mouthSmile = 0.4f,
                    mouthOpenness = 0.1f,
                    browAngle = -8.0f,
                    blushIntensity = 0.95f
                )
            }
        }

        fun lerp(start: FacialExpression, end: FacialExpression, fraction: Float): FacialExpression {
            return FacialExpression(
                preset = if (fraction > 0.5f) end.preset else start.preset,
                eyeOpenness = start.eyeOpenness + (end.eyeOpenness - start.eyeOpenness) * fraction,
                isLeftWink = if (fraction > 0.5f) end.isLeftWink else start.isLeftWink,
                isRightWink = if (fraction > 0.5f) end.isRightWink else start.isRightWink,
                mouthSmile = start.mouthSmile + (end.mouthSmile - start.mouthSmile) * fraction,
                mouthOpenness = start.mouthOpenness + (end.mouthOpenness - start.mouthOpenness) * fraction,
                mouthShape = if (fraction > 0.5f) end.mouthShape else start.mouthShape,
                browAngle = start.browAngle + (end.browAngle - start.browAngle) * fraction,
                browHeight = start.browHeight + (end.browHeight - start.browHeight) * fraction,
                blushIntensity = start.blushIntensity + (end.blushIntensity - start.blushIntensity) * fraction
            )
        }
    }
}
