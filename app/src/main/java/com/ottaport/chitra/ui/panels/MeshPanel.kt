package com.ottaport.chitra.ui.panels

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.ottaport.chitra.model.Vector2D
import com.ottaport.chitra.ui.theme.StudioIcons
import com.ottaport.chitra.viewmodel.StudioUiState

@Composable
fun MeshPanel(
    uiState: StudioUiState,
    onSelectVertex: (Int?) -> Unit,
    onMoveVertex: (Vector2D) -> Unit,
    onResetMesh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedLayer = uiState.project.layers.find { it.id == uiState.selectedLayerId }

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
            Column {
                Text(
                    text = "Mesh Deformation Lattice (3x3)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Warp: ${selectedLayer?.name ?: "Select a layer"}",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
            }

            // Reset mesh warp button
            OutlinedButton(
                onClick = onResetMesh,
                modifier = Modifier.height(28.dp).testTag("btn_reset_mesh")
            ) {
                Icon(StudioIcons.Refresh, "Reset", modifier = Modifier.size(14.dp), tint = Color(0xFF475569))
                Spacer(Modifier.width(4.dp))
                Text("Reset Warp", fontSize = 11.sp, color = Color(0xFF475569))
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // 3x3 Vertex Selector Mini-Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Select Vertex", fontSize = 10.sp, color = Color(0xFF94A3B8))
                for (r in 0 until 3) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (c in 0 until 3) {
                            val vId = r * 3 + c
                            val isSelected = uiState.selectedVertexId == vId
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                                        shape = CircleShape
                                    )
                                    .background(if (isSelected) Color(0xFFFEF3C7) else Color(0xFFF1F5F9))
                                    .clickable { onSelectVertex(vId) }
                                    .testTag("vertex_selector_$vId"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = vId.toString(),
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFFB45309) else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }
            }

            // Directional Nudge D-Pad for Selected Vertex
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Nudge Vertex", fontSize = 10.sp, color = Color(0xFF94A3B8))
                IconButton(
                    onClick = { onMoveVertex(Vector2D(0f, -0.15f)) },
                    modifier = Modifier.size(28.dp).testTag("nudge_up")
                ) {
                    Icon(StudioIcons.ArrowUp, "Up", tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onMoveVertex(Vector2D(-0.15f, 0f)) },
                        modifier = Modifier.size(28.dp).testTag("nudge_left")
                    ) {
                        Icon(StudioIcons.ArrowLeft, "Left", tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                    }
                    Box(
                        modifier = Modifier.size(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${uiState.selectedVertexId ?: 4}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                    }
                    IconButton(
                        onClick = { onMoveVertex(Vector2D(0.15f, 0f)) },
                        modifier = Modifier.size(28.dp).testTag("nudge_right")
                    ) {
                        Icon(StudioIcons.ArrowRight, "Right", tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                    }
                }
                IconButton(
                    onClick = { onMoveVertex(Vector2D(0f, 0.15f)) },
                    modifier = Modifier.size(28.dp).testTag("nudge_down")
                ) {
                    Icon(StudioIcons.ArrowDown, "Down", tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
