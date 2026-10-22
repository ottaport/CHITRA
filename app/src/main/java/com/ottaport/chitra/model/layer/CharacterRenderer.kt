package com.ottaport.chitra.model.layer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import com.ottaport.chitra.model.Vector2D
import com.ottaport.chitra.model.expression.FacialExpression
import com.ottaport.chitra.model.rig.Bone
import com.ottaport.chitra.model.rig.Skeleton

object CharacterRenderer {

    fun renderCharacter(
        drawScope: DrawScope,
        skeleton: Skeleton,
        layers: List<RigLayer>,
        expression: FacialExpression = FacialExpression(),
        alphaMultiplier: Float = 1.0f,
        tintColor: Color? = null
    ) {
        val sortedLayers = layers.filter { it.isVisible }.sortedBy { it.zIndex }
        val bonesMap = skeleton.bones.associateBy { it.id }

        for (layer in sortedLayers) {
            val bone = bonesMap[layer.attachedBoneId] ?: continue
            val layerOpacity = (layer.opacity * alphaMultiplier).coerceIn(0f, 1f)
            if (layerOpacity <= 0.01f) continue

            drawScope.renderLayer(
                layer = layer,
                bone = bone,
                expression = expression,
                alpha = layerOpacity,
                tint = tintColor
            )
        }
    }

    private fun DrawScope.renderLayer(
        layer: RigLayer,
        bone: Bone,
        expression: FacialExpression,
        alpha: Float,
        tint: Color?
    ) {
        val basePos = bone.worldPosition + layer.localOffset
        val angleDeg = bone.worldAngle + layer.localRotation

        translate(basePos.x, basePos.y) {
            rotate(angleDeg, pivot = Offset.Zero) {
                val primaryColor = tint?.copy(alpha = alpha) ?: Color(layer.primaryColorHex).copy(alpha = alpha)
                val secondaryColor = tint?.copy(alpha = alpha * 0.8f) ?: Color(layer.secondaryColorHex).copy(alpha = alpha)
                val outlineColor = tint?.copy(alpha = alpha) ?: Color(0xFF1E293B).copy(alpha = alpha)

                // Mesh warp factor (deforms width/height or bends)
                val meshCenterOffset = layer.meshGrid.vertexAt(1, 1)?.offset ?: Vector2D.Zero
                val warpX = meshCenterOffset.x * 20f
                val warpY = meshCenterOffset.y * 20f

                when (layer.type) {
                    PartType.HEAD -> {
                        // Anime Head Face Shape
                        val headPath = Path().apply {
                            moveTo(-32f + warpX, -20f)
                            cubicTo(-34f, -48f, 34f, -48f, 32f + warpX, -20f)
                            cubicTo(32f, 10f, 16f, 36f, 0f + warpX, 42f)
                            cubicTo(-16f, 36f, -32f, 10f, -32f + warpX, -20f)
                            close()
                        }
                        drawPath(headPath, primaryColor, style = Fill)
                        drawPath(headPath, outlineColor, style = Stroke(width = 2.5f))
                    }

                    PartType.HAIR_BACK -> {
                        // Long anime hair flowing down
                        val hairPath = Path().apply {
                            moveTo(-40f, -20f)
                            cubicTo(-50f + warpX, 30f, -45f, 90f + warpY, -20f, 110f)
                            cubicTo(0f, 120f, 20f, 120f, 30f + warpX, 105f)
                            cubicTo(50f, 85f + warpY, 52f, 20f, 40f, -20f)
                            close()
                        }
                        drawPath(hairPath, secondaryColor, style = Fill)
                        drawPath(hairPath, outlineColor, style = Stroke(width = 2.5f))
                    }

                    PartType.HAIR_FRONT -> {
                        // Anime bangs with spikes and side tendrils
                        val bangsPath = Path().apply {
                            moveTo(-36f, -28f)
                            cubicTo(-20f, -44f, 20f, -44f, 36f, -28f)
                            // Right side fringe
                            lineTo(34f + warpX, -5f)
                            lineTo(26f, -18f)
                            // Center fringe
                            lineTo(18f + warpX * 0.8f, -2f)
                            lineTo(10f, -16f)
                            lineTo(0f + warpX, 2f)
                            lineTo(-8f, -16f)
                            // Left side fringe
                            lineTo(-18f + warpX * 0.8f, -4f)
                            lineTo(-24f, -18f)
                            lineTo(-34f + warpX, -6f)
                            close()
                        }
                        drawPath(bangsPath, primaryColor, style = Fill)
                        drawPath(bangsPath, outlineColor, style = Stroke(width = 2.5f))
                    }

                    PartType.HAIR_SIDE_L, PartType.HAIR_SIDE_R -> {
                        // Animated hair strand that swings with hair bones
                        val strandPath = Path().apply {
                            moveTo(-10f, 0f)
                            cubicTo(-15f + warpX, 20f, -12f, 40f, 0f + warpX, bone.length)
                            cubicTo(8f, 35f, 10f + warpX, 15f, 10f, 0f)
                            close()
                        }
                        drawPath(strandPath, primaryColor, style = Fill)
                        drawPath(strandPath, outlineColor, style = Stroke(width = 2.5f))
                    }

                    PartType.EYE_L, PartType.EYE_R -> {
                        renderAnimeEyes(expression, primaryColor, outlineColor)
                    }

                    PartType.BROW_L, PartType.BROW_R -> {
                        renderEyebrows(expression, outlineColor)
                    }

                    PartType.MOUTH -> {
                        renderAnimeMouth(expression, outlineColor, secondaryColor)
                    }

                    PartType.BLUSH -> {
                        val blushAlpha = (expression.blushIntensity * alpha).coerceIn(0f, 1f)
                        if (blushAlpha > 0.05f) {
                            val blushCol = primaryColor.copy(alpha = blushAlpha * 0.7f)
                            drawCircle(blushCol, radius = 9f, center = Offset(-18f, 10f))
                            drawCircle(blushCol, radius = 9f, center = Offset(18f, 10f))
                        }
                    }

                    PartType.TORSO -> {
                        // Uniform Torso
                        val torsoPath = Path().apply {
                            moveTo(-22f, 0f)
                            lineTo(22f, 0f)
                            lineTo(18f + warpX, bone.length)
                            lineTo(-18f + warpX, bone.length)
                            close()
                        }
                        drawPath(torsoPath, primaryColor, style = Fill)
                        drawPath(torsoPath, outlineColor, style = Stroke(width = 2.5f))

                        // Sailor Collar
                        val collarPath = Path().apply {
                            moveTo(-22f, 0f)
                            lineTo(0f, 18f)
                            lineTo(22f, 0f)
                            lineTo(14f, -4f)
                            lineTo(-14f, -4f)
                            close()
                        }
                        drawPath(collarPath, secondaryColor, style = Fill)
                        drawPath(collarPath, outlineColor, style = Stroke(width = 2f))
                    }

                    PartType.ACCESSORY -> {
                        // Ribbon
                        val ribbonPath = Path().apply {
                            moveTo(0f, 15f)
                            lineTo(-14f, 8f)
                            lineTo(-10f, 22f)
                            lineTo(0f, 16f)
                            lineTo(10f, 22f)
                            lineTo(14f, 8f)
                            close()
                        }
                        drawPath(ribbonPath, primaryColor, style = Fill)
                        drawPath(ribbonPath, outlineColor, style = Stroke(width = 2f))
                        drawCircle(secondaryColor, radius = 4f, center = Offset(0f, 15f))
                    }

                    PartType.ARM_UPPER_L, PartType.ARM_UPPER_R, PartType.ARM_FORE_L, PartType.ARM_FORE_R -> {
                        // Rounded limb capsule along bone length
                        val limbRadius = 7f
                        val limbPath = Path().apply {
                            moveTo(-limbRadius, 0f)
                            lineTo(-limbRadius + 1f, bone.length)
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    -limbRadius + 1f, bone.length - limbRadius,
                                    limbRadius - 1f, bone.length + limbRadius
                                ),
                                startAngleDegrees = 180f,
                                sweepAngleDegrees = -180f,
                                forceMoveTo = false
                            )
                            lineTo(limbRadius, 0f)
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    -limbRadius, -limbRadius,
                                    limbRadius, limbRadius
                                ),
                                startAngleDegrees = 0f,
                                sweepAngleDegrees = -180f,
                                forceMoveTo = false
                            )
                            close()
                        }
                        drawPath(limbPath, primaryColor, style = Fill)
                        drawPath(limbPath, outlineColor, style = Stroke(width = 2f))
                    }

                    PartType.HAND_L, PartType.HAND_R -> {
                        drawCircle(primaryColor, radius = 9f, center = Offset(0f, bone.length * 0.5f))
                        drawCircle(outlineColor, radius = 9f, center = Offset(0f, bone.length * 0.5f), style = Stroke(width = 2f))
                    }

                    PartType.THIGH_L, PartType.THIGH_R, PartType.SHIN_L, PartType.SHIN_R -> {
                        val legRadius = 9f
                        val legPath = Path().apply {
                            moveTo(-legRadius, 0f)
                            lineTo(-legRadius + 2f, bone.length)
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    -legRadius + 2f, bone.length - legRadius,
                                    legRadius - 2f, bone.length + legRadius
                                ),
                                startAngleDegrees = 180f,
                                sweepAngleDegrees = -180f,
                                forceMoveTo = false
                            )
                            lineTo(legRadius, 0f)
                            close()
                        }
                        drawPath(legPath, primaryColor, style = Fill)
                        drawPath(legPath, outlineColor, style = Stroke(width = 2f))
                    }

                    PartType.FOOT_L, PartType.FOOT_R -> {
                        val shoePath = Path().apply {
                            moveTo(-8f, 0f)
                            lineTo(14f, 6f)
                            lineTo(12f, 16f)
                            lineTo(-8f, 14f)
                            close()
                        }
                        drawPath(shoePath, primaryColor, style = Fill)
                        drawPath(shoePath, outlineColor, style = Stroke(width = 2f))
                    }
                }
            }
        }
    }

    private fun DrawScope.renderAnimeEyes(
        expression: FacialExpression,
        irisColor: Color,
        outlineColor: Color
    ) {
        val eyeOpenness = expression.eyeOpenness
        val isLeftClosed = expression.isLeftWink || eyeOpenness < 0.2f
        val isRightClosed = expression.isRightWink || eyeOpenness < 0.2f

        // Left Eye (x = -16, y = -6)
        if (isLeftClosed) {
            // Wink line ^_^
            val winkPath = Path().apply {
                moveTo(-23f, -4f)
                quadraticTo(-16f, -11f, -9f, -4f)
            }
            drawPath(winkPath, outlineColor, style = Stroke(width = 2.8f))
        } else {
            val eyeHeight = 16f * eyeOpenness
            drawOval(Color.White, topLeft = Offset(-23f, -6f - eyeHeight * 0.5f), size = Size(14f, eyeHeight))
            drawOval(irisColor, topLeft = Offset(-21f, -5f - eyeHeight * 0.45f), size = Size(10f, eyeHeight * 0.9f))
            // Big anime sparkle
            drawCircle(Color.White, radius = 2.5f, center = Offset(-18f, -7f))
            drawCircle(Color.White, radius = 1.2f, center = Offset(-15f, -3f))
            // Eyelash upper line
            val lashPath = Path().apply {
                moveTo(-24f, -6f - eyeHeight * 0.5f)
                quadraticTo(-16f, -9f - eyeHeight * 0.5f, -8f, -6f - eyeHeight * 0.5f)
            }
            drawPath(lashPath, outlineColor, style = Stroke(width = 3f))
        }

        // Right Eye (x = +16, y = -6)
        if (isRightClosed) {
            val winkPath = Path().apply {
                moveTo(9f, -4f)
                quadraticTo(16f, -11f, 23f, -4f)
            }
            drawPath(winkPath, outlineColor, style = Stroke(width = 2.8f))
        } else {
            val eyeHeight = 16f * eyeOpenness
            drawOval(Color.White, topLeft = Offset(9f, -6f - eyeHeight * 0.5f), size = Size(14f, eyeHeight))
            drawOval(irisColor, topLeft = Offset(11f, -5f - eyeHeight * 0.45f), size = Size(10f, eyeHeight * 0.9f))
            drawCircle(Color.White, radius = 2.5f, center = Offset(14f, -7f))
            drawCircle(Color.White, radius = 1.2f, center = Offset(17f, -3f))
            val lashPath = Path().apply {
                moveTo(8f, -6f - eyeHeight * 0.5f)
                quadraticTo(16f, -9f - eyeHeight * 0.5f, 24f, -6f - eyeHeight * 0.5f)
            }
            drawPath(lashPath, outlineColor, style = Stroke(width = 3f))
        }
    }

    private fun DrawScope.renderEyebrows(expression: FacialExpression, outlineColor: Color) {
        val yOffset = expression.browHeight
        val angle = expression.browAngle

        // Left Eyebrow
        val browL = Path().apply {
            moveTo(-24f, -18f + yOffset)
            quadraticTo(-16f, -22f + yOffset - angle * 0.2f, -8f, -18f + yOffset - angle * 0.3f)
        }
        drawPath(browL, outlineColor, style = Stroke(width = 2.2f))

        // Right Eyebrow
        val browR = Path().apply {
            moveTo(8f, -18f + yOffset - angle * 0.3f)
            quadraticTo(16f, -22f + yOffset - angle * 0.2f, 24f, -18f + yOffset)
        }
        drawPath(browR, outlineColor, style = Stroke(width = 2.2f))
    }

    private fun DrawScope.renderAnimeMouth(
        expression: FacialExpression,
        outlineColor: Color,
        mouthFillColor: Color
    ) {
        val smile = expression.mouthSmile
        val open = expression.mouthOpenness

        if (open > 0.15f) {
            // Open talking mouth with pink interior
            val mouthPath = Path().apply {
                moveTo(-8f, 18f)
                quadraticTo(0f, 18f - smile * 5f, 8f, 18f)
                quadraticTo(0f, 18f + open * 14f, -8f, 18f)
                close()
            }
            drawPath(mouthPath, mouthFillColor, style = Fill)
            drawPath(mouthPath, outlineColor, style = Stroke(width = 2.2f))
        } else {
            // Subtle closed smile line
            val mouthPath = Path().apply {
                moveTo(-6f, 18f)
                quadraticTo(0f, 18f + smile * 6f, 6f, 18f)
            }
            drawPath(mouthPath, outlineColor, style = Stroke(width = 2.2f))
        }
    }
}
