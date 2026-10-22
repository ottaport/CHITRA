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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
fun LayersPanel(
    uiState: StudioUiState,
    onSelectLayer: (String?) -> Unit,
    onToggleVisibility: (String, Boolean) -> Unit,
    onSetOpacity: (String, Float) -> Unit,
    onMoveLayerZ: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val layers = uiState.project.layers.sortedByDescending { it.zIndex }
    val selectedLayer = layers.find { it.id == uiState.selectedLayerId }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Horizontal list of layer chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(layers) { layer ->
                val isSelected = layer.id == uiState.selectedLayerId
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .background(if (isSelected) Color(0xFFEFF6FF) else Color.White)
                        .clickable { onSelectLayer(layer.id) }
                        .padding(horizontal = 8.dp)
                        .testTag("layer_chip_${layer.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(layer.primaryColorHex))
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = layer.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFF2563EB) else Color(0xFF334155)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Selected Layer Details
        if (selectedLayer != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedLayer.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Bone: ${selectedLayer.attachedBoneId}",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Visibility toggle
                    IconButton(
                        onClick = { onToggleVisibility(selectedLayer.id, !selectedLayer.isVisible) },
                        modifier = Modifier.size(28.dp).testTag("btn_layer_vis")
                    ) {
                        Icon(
                            imageVector = if (selectedLayer.isVisible) StudioIcons.Visibility else StudioIcons.VisibilityOff,
                            contentDescription = "Toggle Visibility",
                            tint = if (selectedLayer.isVisible) Color(0xFF2563EB) else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Move Up (towards front)
                    IconButton(
                        onClick = { onMoveLayerZ(selectedLayer.id, 1) },
                        modifier = Modifier.size(28.dp).testTag("btn_layer_up")
                    ) {
                        Icon(StudioIcons.ArrowUp, "Move Forward", tint = Color(0xFF475569), modifier = Modifier.size(16.dp))
                    }

                    // Move Down (towards back)
                    IconButton(
                        onClick = { onMoveLayerZ(selectedLayer.id, -1) },
                        modifier = Modifier.size(28.dp).testTag("btn_layer_down")
                    ) {
                        Icon(StudioIcons.ArrowDown, "Move Backward", tint = Color(0xFF475569), modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Opacity slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Opacity ${(selectedLayer.opacity * 100).toInt()}%",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.width(72.dp)
                )
                Slider(
                    value = selectedLayer.opacity,
                    onValueChange = { onSetOpacity(selectedLayer.id, it) },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF2563EB),
                        activeTrackColor = Color(0xFF2563EB),
                        inactiveTrackColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .testTag("slider_layer_opacity")
                )
            }
        }
    }
}
