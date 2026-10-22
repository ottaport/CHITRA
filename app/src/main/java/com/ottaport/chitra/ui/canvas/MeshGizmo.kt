package com.ottaport.chitra.ui.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import com.ottaport.chitra.model.Vector2D
import com.ottaport.chitra.model.layer.RigLayer
import com.ottaport.chitra.model.rig.Bone

object MeshGizmo {

    fun drawMeshLattice(
        drawScope: DrawScope,
        layer: RigLayer,
        bone: Bone,
        selectedVertexId: Int?
    ) {
        val basePos = bone.worldPosition + layer.localOffset
        val angleDeg = bone.worldAngle + layer.localRotation
        val grid = layer.meshGrid

        drawScope.translate(basePos.x, basePos.y) {
            rotate(angleDeg, pivot = Offset.Zero) {
                // Approximate bounding box of layer in local units (-40..40, -40..80)
                val boundsWidth = 80f
                val boundsHeight = bone.length.coerceAtLeast(80f)

                fun vertexWorldLocal(v: com.ottaport.chitra.model.mesh.MeshVertex): Offset {
                    val nx = (v.restPos.x - 0.5f) * boundsWidth + v.offset.x * 20f
                    val ny = (v.restPos.y - 0.2f) * boundsHeight + v.offset.y * 20f
                    return Offset(nx, ny)
                }

                // Draw grid lines
                for (r in 0 until grid.rows) {
                    for (c in 0 until grid.cols) {
                        val currentV = grid.vertexAt(r, c) ?: continue
                        val currentPt = vertexWorldLocal(currentV)

                        // Connect right neighbor
                        if (c + 1 < grid.cols) {
                            val rightV = grid.vertexAt(r, c + 1)
                            if (rightV != null) {
                                val rightPt = vertexWorldLocal(rightV)
                                drawLine(
                                    color = Color(0x6610B981),
                                    start = currentPt,
                                    end = rightPt,
                                    strokeWidth = 1.5f
                                )
                            }
                        }

                        // Connect bottom neighbor
                        if (r + 1 < grid.rows) {
                            val bottomV = grid.vertexAt(r + 1, c)
                            if (bottomV != null) {
                                val bottomPt = vertexWorldLocal(bottomV)
                                drawLine(
                                    color = Color(0x6610B981),
                                    start = currentPt,
                                    end = bottomPt,
                                    strokeWidth = 1.5f
                                )
                            }
                        }
                    }
                }

                // Draw vertices
                for (v in grid.vertices) {
                    val pt = vertexWorldLocal(v)
                    val isSelected = v.id == selectedVertexId

                    // Outer halo
                    drawCircle(
                        color = if (isSelected) Color(0xFFF59E0B) else Color(0xFF10B981),
                        radius = if (isSelected) 8f else 5f,
                        center = pt,
                        style = Fill
                    )
                    drawCircle(
                        color = Color.White,
                        radius = if (isSelected) 8f else 5f,
                        center = pt,
                        style = Stroke(width = 1.8f)
                    )
                }
            }
        }
    }
}
