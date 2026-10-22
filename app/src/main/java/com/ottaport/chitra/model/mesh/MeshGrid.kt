package com.ottaport.chitra.model.mesh

import com.ottaport.chitra.model.Vector2D
import kotlinx.serialization.Serializable

@Serializable
data class MeshVertex(
    val id: Int,
    val restPos: Vector2D, // Normalized [0..1] in part bounds
    val offset: Vector2D = Vector2D.Zero // Deformed displacement
) {
    val currentPos: Vector2D get() = restPos + offset
}

@Serializable
data class MeshGrid(
    val rows: Int = 3,
    val cols: Int = 3,
    val vertices: List<MeshVertex> = defaultGrid(3, 3)
) {
    fun vertexAt(row: Int, col: Int): MeshVertex? {
        val index = row * cols + col
        return vertices.getOrNull(index)
    }

    fun updateVertexOffset(vertexId: Int, newOffset: Vector2D): MeshGrid {
        val updated = vertices.map {
            if (it.id == vertexId) it.copy(offset = newOffset) else it
        }
        return copy(vertices = updated)
    }

    fun reset(): MeshGrid {
        return copy(vertices = vertices.map { it.copy(offset = Vector2D.Zero) })
    }

    fun hasDeformation(): Boolean {
        return vertices.any { it.offset.length() > 0.001f }
    }

    companion object {
        fun defaultGrid(rows: Int = 3, cols: Int = 3): List<MeshVertex> {
            val list = mutableListOf<MeshVertex>()
            var id = 0
            for (r in 0 until rows) {
                val y = if (rows > 1) r.toFloat() / (rows - 1) else 0.5f
                for (c in 0 until cols) {
                    val x = if (cols > 1) c.toFloat() / (cols - 1) else 0.5f
                    list.add(MeshVertex(id = id++, restPos = Vector2D(x, y)))
                }
            }
            return list
        }
    }
}
