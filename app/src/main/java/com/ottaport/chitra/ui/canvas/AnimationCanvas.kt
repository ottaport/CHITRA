package com.ottaport.chitra.ui.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import com.ottaport.chitra.model.Vector2D
import com.ottaport.chitra.model.anim.Interpolator
import com.ottaport.chitra.model.layer.CharacterRenderer
import com.ottaport.chitra.model.rig.Bone
import com.ottaport.chitra.viewmodel.StudioMode
import com.ottaport.chitra.viewmodel.StudioUiState
import kotlin.math.atan2

@Composable
fun AnimationCanvas(
    uiState: StudioUiState,
    onSelectBone: (String?) -> Unit,
    onRotateBone: (Float) -> Unit,
    onMoveIkTarget: (Vector2D) -> Unit,
    onSelectVertex: (Int?) -> Unit,
    onMoveVertex: (Vector2D) -> Unit,
    onUpdateZoomAndPan: (Float, Vector2D) -> Unit,
    onResetView: () -> Unit,
    modifier: Modifier = Modifier
) {
    var canvasCenter by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFC))
            .testTag("animation_canvas")
            .pointerInput(Unit) {
                // Double tap to reset view, single tap to select
                detectTapGestures(
                    onDoubleTap = { onResetView() },
                    onTap = { tapOffset ->
                        val zoom = uiState.zoomScale
                        val pan = uiState.panOffset
                        // Transform tap screen coordinate to character world coordinates
                        val worldX = (tapOffset.x - canvasCenter.x - pan.x) / zoom
                        val worldY = (tapOffset.y - canvasCenter.y - pan.y) / zoom
                        val worldPoint = Vector2D(worldX, worldY)

                        // Check bone hit
                        val hitBone = uiState.evaluatedPose.skeleton.bones.findLast {
                            it.containsPoint(worldPoint, threshold = 28f / zoom)
                        }
                        if (hitBone != null) {
                            onSelectBone(hitBone.id)
                        }
                    }
                )
            }
            .pointerInput(uiState.currentMode, uiState.selectedBoneId, uiState.selectedVertexId, uiState.isIkActive) {
                // Drag gestures depending on tool mode
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val zoom = uiState.zoomScale

                    when (uiState.currentMode) {
                        StudioMode.ANIMATE, StudioMode.RIGGING -> {
                            val selectedBone = uiState.evaluatedPose.skeleton.bones.find {
                                it.id == uiState.selectedBoneId
                            }
                            if (selectedBone != null) {
                                if (selectedBone.isIkTarget && uiState.isIkActive) {
                                    // Move IK target in world space
                                    val currentTarget = selectedBone.ikTargetPosition ?: selectedBone.worldEndPosition
                                    val newTarget = currentTarget + Vector2D(dragAmount.x / zoom, dragAmount.y / zoom)
                                    onMoveIkTarget(newTarget)
                                } else {
                                    // FK Rotation: compute angular delta relative to bone root
                                    val boneScreenRoot = canvasCenter + Offset(
                                        (selectedBone.worldPosition.x * zoom) + uiState.panOffset.x,
                                        (selectedBone.worldPosition.y * zoom) + uiState.panOffset.y
                                    )
                                    val prevPos = change.position - dragAmount
                                    val anglePrev = atan2(prevPos.y - boneScreenRoot.y, prevPos.x - boneScreenRoot.x)
                                    val angleCurr = atan2(change.position.y - boneScreenRoot.y, change.position.x - boneScreenRoot.x)
                                    var deltaDeg = Math.toDegrees((angleCurr - anglePrev).toDouble()).toFloat()
                                    if (deltaDeg > 180f) deltaDeg -= 360f
                                    if (deltaDeg < -180f) deltaDeg += 360f
                                    onRotateBone(deltaDeg)
                                }
                            }
                        }
                        StudioMode.MESH -> {
                            val vertexDelta = Vector2D(dragAmount.x / (zoom * 20f), dragAmount.y / (zoom * 20f))
                            onMoveVertex(vertexDelta)
                        }
                        else -> {
                            // Pan canvas
                            val newPan = uiState.panOffset + Vector2D(dragAmount.x, dragAmount.y)
                            onUpdateZoomAndPan(uiState.zoomScale, newPan)
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                // Two-finger pinch-to-zoom and pan
                detectTransformGestures { _, pan, zoom, _ ->
                    val newZoom = (uiState.zoomScale * zoom).coerceIn(0.4f, 3.5f)
                    val newPan = uiState.panOffset + Vector2D(pan.x, pan.y)
                    onUpdateZoomAndPan(newZoom, newPan)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            canvasCenter = Offset(size.width * 0.5f, size.height * 0.45f)

            val zoom = uiState.zoomScale
            val pan = uiState.panOffset

            // Draw studio background grid dots
            val gridSpacing = 40f * zoom
            val startX = (canvasCenter.x + pan.x) % gridSpacing
            val startY = (canvasCenter.y + pan.y) % gridSpacing

            var x = startX
            while (x < size.width) {
                var y = startY
                while (y < size.height) {
                    drawCircle(Color(0xFFE2E8F0), radius = 1.5f, center = Offset(x, y))
                    y += gridSpacing
                }
                x += gridSpacing
            }

            // Draw origin studio floor reference line
            val floorY = canvasCenter.y + pan.y + (160f * zoom)
            drawLine(
                color = Color(0xFFCBD5E1),
                start = Offset(0f, floorY),
                end = Offset(size.width, floorY),
                strokeWidth = 1.5f
            )

            // Center transform for character rendering
            translate(canvasCenter.x + pan.x, canvasCenter.y + pan.y) {
                scale(zoom, pivot = Offset.Zero) {

                    // Onion Skinning (Ghost previous and next frames)
                    if (uiState.project.timeline.onionSkinEnabled) {
                        val currFrame = uiState.project.timeline.currentFrame
                        val prevFrame = (currFrame - 3f).coerceAtLeast(0f)
                        val nextFrame = (currFrame + 3f).coerceAtMost(uiState.project.timeline.totalFrames - 1f)

                        // Ghost Previous Frame in subtle Cyan
                        val prevPose = Interpolator.evaluatePoseAtFrame(
                            prevFrame,
                            uiState.project.timeline.keyframes,
                            uiState.project.skeleton,
                            uiState.project.layers
                        )
                        CharacterRenderer.renderCharacter(
                            drawScope = this,
                            skeleton = prevPose.skeleton,
                            layers = prevPose.layers,
                            expression = prevPose.expression,
                            alphaMultiplier = 0.28f,
                            tintColor = Color(0xFF0284C7)
                        )

                        // Ghost Next Frame in subtle Rose
                        val nextPose = Interpolator.evaluatePoseAtFrame(
                            nextFrame,
                            uiState.project.timeline.keyframes,
                            uiState.project.skeleton,
                            uiState.project.layers
                        )
                        CharacterRenderer.renderCharacter(
                            drawScope = this,
                            skeleton = nextPose.skeleton,
                            layers = nextPose.layers,
                            expression = nextPose.expression,
                            alphaMultiplier = 0.28f,
                            tintColor = Color(0xFFF43F5E)
                        )
                    }

                    // Render current active character pose
                    CharacterRenderer.renderCharacter(
                        drawScope = this,
                        skeleton = uiState.evaluatedPose.skeleton,
                        layers = uiState.evaluatedPose.layers,
                        expression = uiState.evaluatedPose.expression,
                        alphaMultiplier = 1.0f
                    )

                    // Draw Mode-specific Gizmos
                    when (uiState.currentMode) {
                        StudioMode.RIGGING, StudioMode.ANIMATE -> {
                            BoneGizmo.drawSkeleton(
                                drawScope = this,
                                skeleton = uiState.evaluatedPose.skeleton,
                                selectedBoneId = uiState.selectedBoneId,
                                isIkActive = uiState.isIkActive
                            )
                        }
                        StudioMode.MESH -> {
                            val selectedLayer = uiState.evaluatedPose.layers.find { it.id == uiState.selectedLayerId }
                            val attachedBone = selectedLayer?.let { l ->
                                uiState.evaluatedPose.skeleton.bones.find { it.id == l.attachedBoneId }
                            }
                            if (selectedLayer != null && attachedBone != null) {
                                MeshGizmo.drawMeshLattice(
                                    drawScope = this,
                                    layer = selectedLayer,
                                    bone = attachedBone,
                                    selectedVertexId = uiState.selectedVertexId
                                )
                            }
                        }
                        else -> {
                            // Subdued bone lines in layers/expressions/audio modes
                            BoneGizmo.drawSkeleton(
                                drawScope = this,
                                skeleton = uiState.evaluatedPose.skeleton,
                                selectedBoneId = null,
                                isIkActive = false
                            )
                        }
                    }
                }
            }
        }
    }
}
