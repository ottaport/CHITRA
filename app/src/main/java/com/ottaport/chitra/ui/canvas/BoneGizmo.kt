package com.ottaport.chitra.ui.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.ottaport.chitra.model.Vector2D
import com.ottaport.chitra.model.rig.Bone
import com.ottaport.chitra.model.rig.Skeleton

object BoneGizmo {

    fun drawSkeleton(
        drawScope: DrawScope,
        skeleton: Skeleton,
        selectedBoneId: String?,
        isIkActive: Boolean
    ) {
        val bones = skeleton.bones

        for (bone in bones) {
            val isSelected = bone.id == selectedBoneId
            drawScope.drawBone(bone, isSelected, isIkActive)
        }
    }

    private fun DrawScope.drawBone(bone: Bone, isSelected: Boolean, isIkActive: Boolean) {
        val start = Offset(bone.worldPosition.x, bone.worldPosition.y)
        val end = Offset(bone.worldEndPosition.x, bone.worldEndPosition.y)
        val boneVec = end - start
        val boneLen = boneVec.getDistance()
        if (boneLen < 1f) return

        val normVec = Offset(-boneVec.y / boneLen, boneVec.x / boneLen)
        val midPoint = start + (boneVec * 0.28f)
        val baseWidth = (boneLen * 0.14f).coerceIn(4f, 14f)

        val boneColor = if (isSelected) Color(0xFF2563EB) else Color(0x99475569)
        val jointColor = if (isSelected) Color(0xFFF43F5E) else Color(0xFF64748B)

        // Draw classic conical 2D bone diamond shape
        val bonePath = Path().apply {
            moveTo(start.x, start.y)
            lineTo(midPoint.x + normVec.x * baseWidth, midPoint.y + normVec.y * baseWidth)
            lineTo(end.x, end.y)
            lineTo(midPoint.x - normVec.x * baseWidth, midPoint.y - normVec.y * baseWidth)
            close()
        }

        drawPath(bonePath, boneColor.copy(alpha = if (isSelected) 0.35f else 0.2f), style = Fill)
        drawPath(bonePath, boneColor, style = Stroke(width = if (isSelected) 2.5f else 1.5f))

        // Draw root joint circle
        drawCircle(
            color = jointColor,
            radius = if (isSelected) 6.5f else 4.5f,
            center = start
        )
        drawCircle(
            color = Color.White,
            radius = if (isSelected) 6.5f else 4.5f,
            center = start,
            style = Stroke(width = 1.5f)
        )

        // Draw tip joint circle
        drawCircle(
            color = if (isSelected) Color(0xFF2563EB) else jointColor,
            radius = if (isSelected) 5.5f else 3.5f,
            center = end
        )

        // If bone is an IK target and selected or active, draw IK handle
        if (bone.isIkTarget && isIkActive) {
            val ikTargetPos = bone.ikTargetPosition?.let { Offset(it.x, it.y) } ?: end
            drawCircle(
                color = Color(0xFFF43F5E),
                radius = 12f,
                center = ikTargetPos,
                style = Stroke(width = 2.5f)
            )
            drawCircle(
                color = Color(0x33F43F5E),
                radius = 12f,
                center = ikTargetPos,
                style = Fill
            )
            // Crosshairs
            drawLine(
                color = Color(0xFFF43F5E),
                start = Offset(ikTargetPos.x - 16f, ikTargetPos.y),
                end = Offset(ikTargetPos.x + 16f, ikTargetPos.y),
                strokeWidth = 2f
            )
            drawLine(
                color = Color(0xFFF43F5E),
                start = Offset(ikTargetPos.x, ikTargetPos.y - 16f),
                end = Offset(ikTargetPos.x, ikTargetPos.y + 16f),
                strokeWidth = 2f
            )
        }

        // If selected, draw FK rotation arc indicator around start joint
        if (isSelected && !bone.isIkTarget) {
            drawCircle(
                color = Color(0x222563EB),
                radius = 28f,
                center = start,
                style = Fill
            )
            drawCircle(
                color = Color(0xFF2563EB),
                radius = 28f,
                center = start,
                style = Stroke(width = 1.5f)
            )
        }
    }
}
