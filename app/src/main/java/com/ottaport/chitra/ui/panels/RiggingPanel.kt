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
fun RiggingPanel(
    uiState: StudioUiState,
    onSelectBone: (String?) -> Unit,
    onRotateBone: (Float) -> Unit,
    onToggleIk: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bones = uiState.evaluatedPose.skeleton.bones
    val selectedBone = bones.find { it.id == uiState.selectedBoneId }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Horizontal bone selector chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(bones) { bone ->
                val isSelected = bone.id == uiState.selectedBoneId
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
                        .clickable { onSelectBone(bone.id) }
                        .padding(horizontal = 10.dp)
                        .testTag("bone_chip_${bone.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(bone.colorHex))
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = bone.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFF2563EB) else Color(0xFF334155)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Selected Bone controls: Angle Slider & Fast Adjust Buttons
        if (selectedBone != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedBone.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${selectedBone.localAngle.toInt()}°",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2563EB)
                    )
                }

                // Quick step rotate buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onRotateBone(-5f) },
                        modifier = Modifier.size(32.dp).testTag("btn_rotate_ccw")
                    ) {
                        Icon(StudioIcons.ArrowLeft, "Rotate CCW", tint = Color(0xFF475569), modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = { onRotateBone(5f) },
                        modifier = Modifier.size(32.dp).testTag("btn_rotate_cw")
                    ) {
                        Icon(StudioIcons.ArrowRight, "Rotate CW", tint = Color(0xFF475569), modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Continuous rotation slider
            Slider(
                value = selectedBone.localAngle,
                onValueChange = { newAngle ->
                    val delta = newAngle - selectedBone.localAngle
                    onRotateBone(delta)
                },
                valueRange = -180f..180f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF2563EB),
                    activeTrackColor = Color(0xFF2563EB),
                    inactiveTrackColor = Color(0xFFE2E8F0)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .testTag("slider_bone_angle")
            )
        }
    }
}
