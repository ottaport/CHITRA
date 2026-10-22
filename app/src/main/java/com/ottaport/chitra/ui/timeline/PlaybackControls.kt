package com.ottaport.chitra.ui.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottaport.chitra.model.anim.InterpolationType
import com.ottaport.chitra.ui.theme.StudioIcons
import com.ottaport.chitra.viewmodel.StudioUiState

@Composable
fun PlaybackControls(
    uiState: StudioUiState,
    onTogglePlay: () -> Unit,
    onStepPrev: () -> Unit,
    onStepNext: () -> Unit,
    onFirstFrame: () -> Unit,
    onToggleLoop: () -> Unit,
    onToggleOnionSkin: () -> Unit,
    onToggleIk: () -> Unit,
    onAddKeyframe: () -> Unit,
    onRemoveKeyframe: () -> Unit,
    onSetInterpolation: (InterpolationType) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeline = uiState.project.timeline
    var showInterpMenu by remember { mutableStateOf(false) }

    val currentFrame = timeline.selectedFrameIndex
    val totalFrames = timeline.totalFrames
    val hasKeyAtCurrent = timeline.hasKeyframeAt(currentFrame)
    val currentKey = timeline.getKeyframeAt(currentFrame)

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Transport buttons (|<, <, Play/Pause, >, Loop)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = onFirstFrame,
                    modifier = Modifier.size(36.dp).testTag("transport_first_frame")
                ) {
                    Icon(
                        imageVector = StudioIcons.SkipPrevious,
                        contentDescription = "First Frame",
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onStepPrev,
                    modifier = Modifier.size(36.dp).testTag("transport_prev_frame")
                ) {
                    Icon(
                        imageVector = StudioIcons.ArrowLeft,
                        contentDescription = "Prev Frame",
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Play / Pause Circle
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (timeline.isPlaying) Color(0xFF2563EB) else Color(0xFF0F172A))
                        .clickable { onTogglePlay() }
                        .testTag("transport_play_pause"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (timeline.isPlaying) StudioIcons.Pause else StudioIcons.Play,
                        contentDescription = if (timeline.isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = onStepNext,
                    modifier = Modifier.size(36.dp).testTag("transport_next_frame")
                ) {
                    Icon(
                        imageVector = StudioIcons.ArrowRight,
                        contentDescription = "Next Frame",
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Loop toggle
                IconButton(
                    onClick = onToggleLoop,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (timeline.isLooping) Color(0xFFEFF6FF) else Color.Transparent)
                        .testTag("transport_loop")
                ) {
                    Icon(
                        imageVector = StudioIcons.Refresh,
                        contentDescription = "Loop",
                        tint = if (timeline.isLooping) Color(0xFF2563EB) else Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Timecode display: e.g. "00:15 / 60"
            ColumnTimecode(frame = currentFrame, total = totalFrames, fps = timeline.fps)

            // Right Action Controls: Keyframe buttons & Onion Skin & IK
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Onion Skin Toggle
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(
                            width = 1.dp,
                            color = if (timeline.onionSkinEnabled) Color(0xFF2563EB) else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .background(if (timeline.onionSkinEnabled) Color(0xFFEFF6FF) else Color.White)
                        .clickable { onToggleOnionSkin() }
                        .padding(horizontal = 6.dp)
                        .testTag("btn_onion_skin"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ghost",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (timeline.onionSkinEnabled) Color(0xFF2563EB) else Color(0xFF64748B)
                    )
                }

                // IK / FK toggle
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(
                            width = 1.dp,
                            color = if (uiState.isIkActive) Color(0xFFF43F5E) else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .background(if (uiState.isIkActive) Color(0xFFFFF1F2) else Color.White)
                        .clickable { onToggleIk() }
                        .padding(horizontal = 6.dp)
                        .testTag("btn_ik_mode"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (uiState.isIkActive) "IK" else "FK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isIkActive) Color(0xFFF43F5E) else Color(0xFF64748B)
                    )
                }

                // Add / Remove Keyframe
                if (hasKeyAtCurrent) {
                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFEE2E2))
                            .clickable { onRemoveKeyframe() }
                            .padding(horizontal = 6.dp)
                            .testTag("btn_remove_key"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(StudioIcons.Delete, "Delete Key", tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(2.dp))
                            Text("Key", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF2563EB))
                            .clickable { onAddKeyframe() }
                            .padding(horizontal = 8.dp)
                            .testTag("btn_add_key"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(StudioIcons.Add, "Add Key", tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(2.dp))
                            Text("Key", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // Interpolation menu button
                Box {
                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                            .background(Color(0xFFFAFAFC))
                            .clickable { showInterpMenu = true }
                            .padding(horizontal = 6.dp)
                            .testTag("btn_interp_menu"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentKey?.interpolation ?: InterpolationType.EASE_IN_OUT).displayName,
                            fontSize = 10.sp,
                            color = Color(0xFF475569)
                        )
                    }

                    DropdownMenu(
                        expanded = showInterpMenu,
                        onDismissRequest = { showInterpMenu = false }
                    ) {
                        InterpolationType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    onSetInterpolation(type)
                                    showInterpMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnTimecode(frame: Int, total: Int, fps: Int) {
    val seconds = frame / fps
    val subFrame = frame % fps
    val timeFormatted = String.format("%02d:%02d", seconds, subFrame)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = timeFormatted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "F $frame / $total",
            fontSize = 9.sp,
            color = Color(0xFF94A3B8)
        )
    }
}
