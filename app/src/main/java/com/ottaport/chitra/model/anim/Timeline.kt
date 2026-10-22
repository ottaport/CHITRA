package com.ottaport.chitra.model.anim

import com.ottaport.chitra.model.audio.AudioTrackReference
import kotlinx.serialization.Serializable

@Serializable
data class Timeline(
    val totalFrames: Int = 60,
    val fps: Int = 24,
    val currentFrame: Float = 0f,
    val isPlaying: Boolean = false,
    val isLooping: Boolean = true,
    val isPingPong: Boolean = false,
    val pingPongDirection: Float = 1f,
    val onionSkinEnabled: Boolean = false,
    val onionSkinFramesBefore: Int = 2,
    val onionSkinFramesAfter: Int = 2,
    val selectedFrameIndex: Int = 0,
    val keyframes: List<Keyframe> = emptyList(),
    val audioTrack: AudioTrackReference = AudioTrackReference()
) {
    fun getKeyframeAt(frame: Int): Keyframe? = keyframes.find { it.frame == frame }

    fun hasKeyframeAt(frame: Int): Boolean = keyframes.any { it.frame == frame }

    fun addOrUpdateKeyframe(keyframe: Keyframe): Timeline {
        val filtered = keyframes.filterNot { it.frame == keyframe.frame }
        val updated = (filtered + keyframe).sortedBy { it.frame }
        return copy(keyframes = updated)
    }

    fun removeKeyframeAt(frame: Int): Timeline {
        return copy(keyframes = keyframes.filterNot { it.frame == frame })
    }

    fun stepForward(): Timeline {
        val next = (currentFrame.toInt() + 1).coerceAtMost(totalFrames - 1).toFloat()
        return copy(currentFrame = next, selectedFrameIndex = next.toInt())
    }

    fun stepBackward(): Timeline {
        val prev = (currentFrame.toInt() - 1).coerceAtLeast(0).toFloat()
        return copy(currentFrame = prev, selectedFrameIndex = prev.toInt())
    }
}
