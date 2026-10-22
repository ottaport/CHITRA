package com.ottaport.chitra.model.audio

import kotlinx.serialization.Serializable

@Serializable
data class AudioMarker(
    val frame: Int,
    val label: String, // e.g. "Vowel A", "Beat Drop", "Laugh", "Step"
    val colorHex: Long = 0xFF2563EB
)

@Serializable
data class AudioTrackReference(
    val id: String = "audio_1",
    val title: String = "Anime Dialogue & Beat Reference",
    val durationSeconds: Float = 2.5f,
    val sampleRate: Int = 44100,
    val markers: List<AudioMarker> = listOf(
        AudioMarker(frame = 0, label = "Intro Breath", colorHex = 0xFF38BDF8),
        AudioMarker(frame = 12, label = "Mouth Open (A)", colorHex = 0xFFF43F5E),
        AudioMarker(frame = 24, label = "Accent Beat", colorHex = 0xFF8B5CF6),
        AudioMarker(frame = 36, label = "Mouth Smile (I)", colorHex = 0xFFF43F5E),
        AudioMarker(frame = 48, label = "Footstep", colorHex = 0xFF10B981)
    ),
    // Simulated or decoded amplitude waveform heights [0..1] across frames
    val waveformSamples: List<Float> = (0..60).map { f ->
        val t = f.toFloat() / 60f
        val wave = kotlin.math.sin(t * 12.0).toFloat() * 0.4f +
                kotlin.math.sin(t * 28.0).toFloat() * 0.3f + 0.35f
        wave.coerceIn(0.1f, 0.95f)
    }
)
