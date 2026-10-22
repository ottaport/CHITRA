package com.ottaport.chitra.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottaport.chitra.ui.theme.StudioIcons
import com.ottaport.chitra.viewmodel.StudioUiState

@Composable
fun AudioPanel(
    uiState: StudioUiState,
    modifier: Modifier = Modifier
) {
    val audioTrack = uiState.project.timeline.audioTrack

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(StudioIcons.Play, "Audio Track", tint = Color(0xFF8B5CF6), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = audioTrack.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }
            Text(
                text = "${audioTrack.durationSeconds}s (${audioTrack.markers.size} cues)",
                fontSize = 10.sp,
                color = Color(0xFF64748B)
            )
        }

        Spacer(Modifier.height(8.dp))

        // Audio Markers cues along the timeline
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(audioTrack.markers) { marker ->
                Box(
                    modifier = Modifier
                        .height(26.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(13.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(horizontal = 8.dp)
                        .testTag("audio_marker_${marker.frame}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(marker.colorHex))
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "F${marker.frame}: ${marker.label}",
                            fontSize = 10.sp,
                            color = Color(0xFF334155)
                        )
                    }
                }
            }
        }
    }
}
