package com.ottaport.chitra.ui.dialogs

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun ProjectDialog(
    uiState: StudioUiState,
    onLoadTemplate: (String) -> Unit,
    onImportJson: (String) -> Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var importText by remember { mutableStateOf("") }
    var showImportField by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Projects & Templates",
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
                    text = "Load a pre-rigged anime character template or import project JSON:",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                // Template 1: Aoi Wave & Wink
                TemplateCard(
                    title = "Aoi - Wave & Wink Loop",
                    desc = "Full anime character rig, 5 keyframes, blinking eyes & waving arm",
                    isCurrent = uiState.project.id == "proj_anime_wave",
                    onClick = {
                        onLoadTemplate("anime_wave")
                        onDismiss()
                    }
                )

                // Template 2: Chibi Idle
                TemplateCard(
                    title = "Chibi Breathing & Blink",
                    desc = "Breathing chest expansion, hair sway & blinking animation",
                    isCurrent = uiState.project.id == "proj_chibi_idle",
                    onClick = {
                        onLoadTemplate("chibi_idle")
                        onDismiss()
                    }
                )

                Spacer(Modifier.height(4.dp))

                if (showImportField) {
                    OutlinedTextField(
                        value = importText,
                        onValueChange = { importText = it },
                        label = { Text("Paste Project JSON") },
                        modifier = Modifier.fillMaxWidth().height(100.dp).testTag("field_import_json")
                    )
                    Button(
                        onClick = {
                            if (onImportJson(importText)) {
                                Toast.makeText(context, "Project imported successfully!", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            } else {
                                Toast.makeText(context, "Invalid JSON format", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        modifier = Modifier.fillMaxWidth().testTag("btn_confirm_import")
                    ) {
                        Text("Load Project", color = Color.White)
                    }
                } else {
                    TextButton(
                        onClick = { showImportField = true },
                        modifier = Modifier.fillMaxWidth().testTag("btn_show_import")
                    ) {
                        Icon(StudioIcons.Add, "Import", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Import Project from JSON", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
            ) {
                Text("Close", color = Color.White)
            }
        },
        containerColor = Color.White
    )
}

@Composable
private fun TemplateCard(
    title: String,
    desc: String,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = if (isCurrent) Color(0xFF2563EB) else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(8.dp)
            )
            .background(if (isCurrent) Color(0xFFEFF6FF) else Color(0xFFFAFAFC))
            .clickable { onClick() }
            .padding(10.dp)
            .testTag("template_card_${title.take(3)}")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF38BDF8).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(StudioIcons.Info, "Template", tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text(desc, fontSize = 10.sp, color = Color(0xFF64748B))
            }
        }
    }
}
