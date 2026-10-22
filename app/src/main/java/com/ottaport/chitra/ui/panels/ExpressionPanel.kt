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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.ottaport.chitra.model.expression.ExpressionPreset
import com.ottaport.chitra.model.expression.FacialExpression
import com.ottaport.chitra.viewmodel.StudioUiState

@Composable
fun ExpressionPanel(
    uiState: StudioUiState,
    onSelectPreset: (ExpressionPreset) -> Unit,
    onUpdateExpression: (FacialExpression) -> Unit,
    modifier: Modifier = Modifier
) {
    val expr = uiState.evaluatedPose.expression

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Preset chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(ExpressionPreset.values()) { preset ->
                val isSelected = expr.preset == preset
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color(0xFFF43F5E) else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .background(if (isSelected) Color(0xFFFFF1F2) else Color.White)
                        .clickable { onSelectPreset(preset) }
                        .padding(horizontal = 10.dp)
                        .testTag("preset_${preset.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = preset.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFFF43F5E) else Color(0xFF334155)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Sliders for Eye Openness & Mouth Smile
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Eye Blink", fontSize = 10.sp, color = Color(0xFF64748B), modifier = Modifier.width(60.dp))
            Slider(
                value = expr.eyeOpenness,
                onValueChange = { onUpdateExpression(expr.copy(eyeOpenness = it)) },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF2563EB),
                    activeTrackColor = Color(0xFF2563EB)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(24.dp)
                    .testTag("slider_eye_open")
            )
            Spacer(Modifier.width(12.dp))
            Text("Mouth Smile", fontSize = 10.sp, color = Color(0xFF64748B), modifier = Modifier.width(70.dp))
            Slider(
                value = expr.mouthSmile,
                onValueChange = { onUpdateExpression(expr.copy(mouthSmile = it)) },
                valueRange = -1f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFF43F5E),
                    activeTrackColor = Color(0xFFF43F5E)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(24.dp)
                    .testTag("slider_mouth_smile")
            )
        }

        // Sliders for Mouth Open (Talk) & Anime Blush
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mouth Open", fontSize = 10.sp, color = Color(0xFF64748B), modifier = Modifier.width(60.dp))
            Slider(
                value = expr.mouthOpenness,
                onValueChange = { onUpdateExpression(expr.copy(mouthOpenness = it)) },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFF43F5E),
                    activeTrackColor = Color(0xFFF43F5E)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(24.dp)
                    .testTag("slider_mouth_open")
            )
            Spacer(Modifier.width(12.dp))
            Text("Blush Intensity", fontSize = 10.sp, color = Color(0xFF64748B), modifier = Modifier.width(70.dp))
            Slider(
                value = expr.blushIntensity,
                onValueChange = { onUpdateExpression(expr.copy(blushIntensity = it)) },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFFB7185),
                    activeTrackColor = Color(0xFFFB7185)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(24.dp)
                    .testTag("slider_blush")
            )
        }
    }
}
