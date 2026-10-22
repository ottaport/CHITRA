package com.ottaport.chitra.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HelpDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, "Help", tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("CHITRA 2D Studio Guide", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HelpSection(
                    title = "1. Bone Rigging & Hierarchy",
                    desc = "Bones are organized in a parent-child skeleton tree (Pelvis -> Spine -> Chest -> Head, Arms, and Legs). Rotating a parent automatically transforms all children with Forward Kinematics."
                )
                HelpSection(
                    title = "2. Inverse Kinematics (IK)",
                    desc = "Toggle IK mode in the transport bar to pose limbs naturally. Dragging the hand or foot target automatically bends the elbow and knee joints using an analytical law-of-cosines 2-bone IK solver."
                )
                HelpSection(
                    title = "3. 2D Mesh Deformation",
                    desc = "Switch to Mesh Warp mode to adjust 3x3 control lattices on layers. Use it to add anime hair sway, breathing chest motion, and squash & stretch without redrawing."
                )
                HelpSection(
                    title = "4. Keyframe Timeline",
                    desc = "Tap any frame on the bottom timeline, adjust the character pose or expression, and tap '+ Key' to record a keyframe. CHITRA computes smooth cubic Hermite ease-in/out transitions."
                )
                HelpSection(
                    title = "5. Onion Skinning & Audio",
                    desc = "Enable 'Ghost' mode to see faint cyan (previous frame) and coral (next frame) silhouettes. Check the audio track for beat markers to sync mouth flaps and footsteps."
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
            ) {
                Text("Got It", color = Color.White)
            }
        },
        containerColor = Color.White
    )
}

@Composable
private fun HelpSection(title: String, desc: String) {
    Column {
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
        Spacer(Modifier.height(2.dp))
        Text(desc, fontSize = 11.sp, color = Color(0xFF475569), lineHeight = 16.sp)
    }
}
