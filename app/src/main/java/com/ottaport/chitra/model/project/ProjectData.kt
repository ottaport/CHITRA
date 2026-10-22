package com.ottaport.chitra.model.project

import com.ottaport.chitra.model.anim.Timeline
import com.ottaport.chitra.model.expression.FacialExpression
import com.ottaport.chitra.model.layer.RigLayer
import com.ottaport.chitra.model.rig.Skeleton
import kotlinx.serialization.Serializable

@Serializable
data class ProjectData(
    val id: String,
    val name: String,
    val version: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val skeleton: Skeleton,
    val layers: List<RigLayer>,
    val timeline: Timeline,
    val currentExpression: FacialExpression = FacialExpression()
)
