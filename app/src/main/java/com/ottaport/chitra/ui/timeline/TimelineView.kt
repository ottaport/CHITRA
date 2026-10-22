package com.ottaport.chitra.ui.timeline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.ottaport.chitra.model.anim.Timeline
import com.ottaport.chitra.model.audio.AudioTrackReference

@Composable
fun TimelineView(
    timeline: Timeline,
    onSeekFrame: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalFrames = timeline.totalFrames
    val currentFrame = timeline.currentFrame
    val keyframes = timeline.keyframes
    val audioTrack = timeline.audioTrack

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .testTag("timeline_dope_sheet")
            .pointerInput(totalFrames) {
                detectTapGestures { tapOffset ->
                    val frameWidth = size.width.toFloat() / totalFrames
                    val targetFrame = (tapOffset.x / frameWidth).toInt().coerceIn(0, totalFrames - 1)
                    onSeekFrame(targetFrame)
                }
            }
            .pointerInput(totalFrames) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val frameWidth = size.width.toFloat() / totalFrames
                    val targetFrame = (change.position.x / frameWidth).toInt().coerceIn(0, totalFrames - 1)
                    onSeekFrame(targetFrame)
                }
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val frameWidth = canvasWidth / totalFrames.toFloat()

            // 1. Background dividing line between frame ruler and track
            drawLine(
                color = Color(0xFFE2E8F0),
                start = Offset(0f, 16f),
                end = Offset(canvasWidth, 16f),
                strokeWidth = 1f
            )

            // 2. Audio Waveform background strip
            val waveform = audioTrack.waveformSamples
            if (waveform.isNotEmpty()) {
                val waveBaseY = 32f
                val waveHeight = 12f
                for (f in 0 until totalFrames) {
                    val sample = waveform.getOrNull(f % waveform.size) ?: 0.5f
                    val x = f * frameWidth + frameWidth * 0.5f
                    val h = sample * waveHeight
                    drawLine(
                        color = Color(0x338B5CF6),
                        start = Offset(x, waveBaseY - h * 0.5f),
                        end = Offset(x, waveBaseY + h * 0.5f),
                        strokeWidth = (frameWidth * 0.6f).coerceIn(1f, 4f)
                    )
                }
            }

            // Audio Beat / Dialogue Markers
            for (marker in audioTrack.markers) {
                if (marker.frame in 0 until totalFrames) {
                    val markerX = marker.frame * frameWidth + frameWidth * 0.5f
                    drawCircle(
                        color = Color(marker.colorHex),
                        radius = 2.5f,
                        center = Offset(markerX, 16f)
                    )
                }
            }

            // 3. Frame ticks & ruler numbers
            for (f in 0 until totalFrames) {
                val x = f * frameWidth
                val isMajor = f % 5 == 0
                val tickHeight = if (isMajor) 10f else 5f
                val tickColor = if (isMajor) Color(0xFF64748B) else Color(0xFFCBD5E1)

                drawLine(
                    color = tickColor,
                    start = Offset(x, 16f - tickHeight),
                    end = Offset(x, 16f),
                    strokeWidth = 1f
                )

                if (isMajor && f % 10 == 0) {
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#64748B")
                            textSize = 20f
                            isAntiAlias = true
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawText(f.toString(), x, 12f, paint)
                    }
                }
            }

            // 4. Keyframe Diamonds (◆)
            val diamondTrackY = 40f
            val diamondRadius = 5.5f

            for (key in keyframes) {
                val keyX = key.frame * frameWidth + frameWidth * 0.5f
                val diamondPath = Path().apply {
                    moveTo(keyX, diamondTrackY - diamondRadius)
                    lineTo(keyX + diamondRadius, diamondTrackY)
                    lineTo(keyX, diamondTrackY + diamondRadius)
                    lineTo(keyX - diamondRadius, diamondTrackY)
                    close()
                }
                drawPath(diamondPath, color = Color(0xFFF43F5E), style = Fill)
                drawPath(diamondPath, color = Color.White, style = Stroke(width = 1.2f))
            }

            // 5. Playhead Vertical Scrubber Line & Handle
            val playheadX = currentFrame * frameWidth + frameWidth * 0.5f

            // Line across entire track
            drawLine(
                color = Color(0xFF2563EB),
                start = Offset(playheadX, 0f),
                end = Offset(playheadX, canvasHeight),
                strokeWidth = 2f
            )

            // Scrubber handle badge at top
            val handleWidth = 14f
            val handleHeight = 16f
            val handlePath = Path().apply {
                moveTo(playheadX - handleWidth * 0.5f, 0f)
                lineTo(playheadX + handleWidth * 0.5f, 0f)
                lineTo(playheadX + handleWidth * 0.5f, handleHeight - 4f)
                lineTo(playheadX, handleHeight)
                lineTo(playheadX - handleWidth * 0.5f, handleHeight - 4f)
                close()
            }
            drawPath(handlePath, color = Color(0xFF2563EB), style = Fill)
        }
    }
}
