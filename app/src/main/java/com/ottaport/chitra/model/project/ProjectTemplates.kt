package com.ottaport.chitra.model.project

import com.ottaport.chitra.model.Vector2D
import com.ottaport.chitra.model.anim.InterpolationType
import com.ottaport.chitra.model.anim.Keyframe
import com.ottaport.chitra.model.anim.Timeline
import com.ottaport.chitra.model.expression.ExpressionPreset
import com.ottaport.chitra.model.expression.FacialExpression
import com.ottaport.chitra.model.layer.RigLayer
import com.ottaport.chitra.model.rig.Skeleton

object ProjectTemplates {

    fun createAnimeWaveProject(): ProjectData {
        val baseSkeleton = Skeleton.createAnimeCharacterSkeleton(Vector2D(0f, 0f))
        val baseLayers = RigLayer.createDefaultAnimeLayers()

        // Create animation keyframes for Wave & Blink loop
        val keyframe0 = Keyframe(
            frame = 0,
            interpolation = InterpolationType.EASE_IN_OUT,
            boneAngles = mapOf(
                "root_pelvis" to -90f,
                "chest" to 0f,
                "head" to 0f,
                "hair_left" to 100f,
                "hair_right" to 80f,
                "arm_upper_l" to 20f,
                "arm_fore_l" to -15f,
                "arm_upper_r" to -20f,
                "arm_fore_r" to 15f
            ),
            expression = FacialExpression.fromPreset(ExpressionPreset.HAPPY)
        )

        val keyframe15 = Keyframe(
            frame = 15,
            interpolation = InterpolationType.EASE_IN_OUT,
            boneAngles = mapOf(
                "root_pelvis" to -90f,
                "chest" to 3f,
                "head" to -5f,
                "hair_left" to 115f,
                "hair_right" to 75f,
                "arm_upper_l" to -70f, // Raising left arm to wave
                "arm_fore_l" to -55f,
                "hand_l" to 25f,
                "arm_upper_r" to -15f,
                "arm_fore_r" to 10f
            ),
            expression = FacialExpression.fromPreset(ExpressionPreset.HAPPY).copy(eyeOpenness = 1f, mouthOpenness = 0.5f)
        )

        val keyframe30 = Keyframe(
            frame = 30,
            interpolation = InterpolationType.EASE_IN_OUT,
            boneAngles = mapOf(
                "root_pelvis" to -90f,
                "chest" to -2f,
                "head" to 4f,
                "hair_left" to 95f,
                "hair_right" to 90f,
                "arm_upper_l" to -65f,
                "arm_fore_l" to -80f, // Waving hand left
                "hand_l" to -20f,
                "arm_upper_r" to -20f,
                "arm_fore_r" to 15f
            ),
            expression = FacialExpression.fromPreset(ExpressionPreset.WINK)
        )

        val keyframe45 = Keyframe(
            frame = 45,
            interpolation = InterpolationType.EASE_IN_OUT,
            boneAngles = mapOf(
                "root_pelvis" to -90f,
                "chest" to 2f,
                "head" to -3f,
                "hair_left" to 110f,
                "hair_right" to 70f,
                "arm_upper_l" to -75f,
                "arm_fore_l" to -45f, // Waving hand right
                "hand_l" to 20f,
                "arm_upper_r" to -22f,
                "arm_fore_r" to 12f
            ),
            expression = FacialExpression.fromPreset(ExpressionPreset.HAPPY).copy(eyeOpenness = 0.9f, mouthOpenness = 0.3f)
        )

        val keyframe60 = keyframe0.copy(frame = 60) // Seamless loop

        val timeline = Timeline(
            totalFrames = 60,
            fps = 24,
            currentFrame = 0f,
            isPlaying = false,
            keyframes = listOf(keyframe0, keyframe15, keyframe30, keyframe45, keyframe60)
        )

        return ProjectData(
            id = "proj_anime_wave",
            name = "Aoi - Wave & Blink Loop",
            skeleton = baseSkeleton,
            layers = baseLayers,
            timeline = timeline,
            currentExpression = FacialExpression.fromPreset(ExpressionPreset.HAPPY)
        )
    }

    fun createChibiIdleProject(): ProjectData {
        val baseSkeleton = Skeleton.createAnimeCharacterSkeleton(Vector2D(0f, 0f))
        val baseLayers = RigLayer.createDefaultAnimeLayers()

        val keyframe0 = Keyframe(
            frame = 0,
            interpolation = InterpolationType.EASE_IN_OUT,
            boneAngles = mapOf(
                "root_pelvis" to -90f,
                "chest" to 0f,
                "head" to 0f,
                "hair_left" to 100f,
                "hair_right" to 80f
            ),
            expression = FacialExpression.fromPreset(ExpressionPreset.NEUTRAL)
        )

        val keyframe30 = Keyframe(
            frame = 30,
            interpolation = InterpolationType.EASE_IN_OUT,
            boneAngles = mapOf(
                "root_pelvis" to -90f,
                "chest" to 4f, // Breathing chest expansion
                "head" to 2f,
                "hair_left" to 108f,
                "hair_right" to 72f
            ),
            expression = FacialExpression.fromPreset(ExpressionPreset.NEUTRAL).copy(eyeOpenness = 0.1f) // Natural blink
        )

        val keyframe60 = keyframe0.copy(frame = 60)

        val timeline = Timeline(
            totalFrames = 60,
            fps = 24,
            keyframes = listOf(keyframe0, keyframe30, keyframe60)
        )

        return ProjectData(
            id = "proj_chibi_idle",
            name = "Chibi Breathing & Blink",
            skeleton = baseSkeleton,
            layers = baseLayers,
            timeline = timeline,
            currentExpression = FacialExpression.fromPreset(ExpressionPreset.NEUTRAL)
        )
    }
}
