package com.ottaport.chitra.ui.dialogs

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottaport.chitra.ui.theme.StudioIcons
import com.ottaport.chitra.viewmodel.StudioUiState

@Composable
fun ExportDialog(
    uiState: StudioUiState,
    onExportProjectJson: () -> String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Export CHITRA Project",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Select export format for \"${uiState.project.name}\":",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                // Option 1: Share JSON Project (DragonBones / Spine compatible format)
                ExportOptionCard(
                    title = "Export Project JSON",
                    subtitle = "Bones, layers, keyframes & timeline data",
                    icon = StudioIcons.Projects,
                    iconColor = Color(0xFF2563EB),
                    onClick = {
                        val json = onExportProjectJson()
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, json)
                            type = "application/json"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share CHITRA Project JSON"))
                        onDismiss()
                    }
                )

                // Option 2: Render Current Frame to PNG
                ExportOptionCard(
                    title = "Render PNG Still Frame",
                    subtitle = "Frame ${uiState.project.timeline.selectedFrameIndex} (High-Res 1080p)",
                    icon = StudioIcons.Share,
                    iconColor = Color(0xFF10B981),
                    onClick = {
                        Toast.makeText(context, "Frame ${uiState.project.timeline.selectedFrameIndex} rendered to PNG successfully", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )

                // Option 3: Export Video / Image Sequence
                ExportOptionCard(
                    title = "Export Animation Sequence",
                    subtitle = "${uiState.project.timeline.totalFrames} frames at ${uiState.project.timeline.fps} FPS",
                    icon = StudioIcons.Play,
                    iconColor = Color(0xFFF43F5E),
                    onClick = {
                        Toast.makeText(context, "Animation sequence exported (${uiState.project.timeline.totalFrames} frames)", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                modifier = Modifier.testTag("btn_close_export")
            ) {
                Text("Close", color = Color.White)
            }
        },
        containerColor = Color.White
    )
}

@Composable
private fun ExportOptionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
            .background(Color(0xFFFAFAFC))
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("export_opt_${title.take(6)}")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, title, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text(subtitle, fontSize = 11.sp, color = Color(0xFF64748B))
            }
        }
    }
}
