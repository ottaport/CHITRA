package com.ottaport.chitra.model.rig

import com.ottaport.chitra.model.Vector2D
import kotlinx.serialization.Serializable

@Serializable
data class Skeleton(
    val bones: List<Bone> = emptyList(),
    val selectedBoneId: String? = null,
    val isIkEnabled: Boolean = true
) {
    fun findBone(id: String): Bone? = bones.find { it.id == id }

    fun getChildren(parentId: String): List<Bone> = bones.filter { it.parentId == parentId }

    fun updateAllWorldTransforms(): Skeleton {
        val updatedMap = mutableMapOf<String, Bone>()
        val unvisited = bones.toMutableList()

        // Process root bones first, then children level by level
        var iterations = 0
        while (unvisited.isNotEmpty() && iterations < 50) {
            val iterator = unvisited.iterator()
            while (iterator.hasNext()) {
                val bone = iterator.next()
                if (bone.parentId == null) {
                    bone.updateWorldTransform(null)
                    updatedMap[bone.id] = bone
                    iterator.remove()
                } else {
                    val parent = updatedMap[bone.parentId]
                    if (parent != null) {
                        bone.updateWorldTransform(parent)
                        updatedMap[bone.id] = bone
                        iterator.remove()
                    }
                }
            }
            iterations++
        }

        // Apply IK if enabled and there are IK targets
        val listAfterTransforms = bones.map { updatedMap[it.id] ?: it }
        return if (isIkEnabled) {
            applyIkConstraints(listAfterTransforms)
        } else {
            copy(bones = listAfterTransforms)
        }
    }

    private fun applyIkConstraints(currentBones: List<Bone>): Skeleton {
        val map = currentBones.associateBy { it.id }.toMutableMap()

        // Look for bones with IK targets
        for (bone in currentBones) {
            if (bone.isIkTarget && bone.ikTargetPosition != null && bone.parentId != null) {
                val bone2 = bone
                val bone1 = map[bone2.parentId] ?: continue
                val parentOfBone1 = bone1.parentId?.let { map[it] }
                val origin = bone1.worldPosition
                val parentAngle = parentOfBone1?.worldAngle ?: 0f

                val ikResult = IkSolver.solveTwoBoneIk(
                    origin = origin,
                    target = bone2.ikTargetPosition,
                    len1 = bone1.length,
                    len2 = bone2.length,
                    parentAngleDeg = parentAngle,
                    poleBendDirection = bone2.ikPoleDirection
                )

                // Update bone1 and bone2 local angles
                val updatedBone1 = bone1.copy(localAngle = ikResult.bone1AngleDeg)
                updatedBone1.updateWorldTransform(parentOfBone1)
                map[bone1.id] = updatedBone1

                val updatedBone2 = bone2.copy(localAngle = ikResult.bone2AngleDeg)
                updatedBone2.updateWorldTransform(updatedBone1)
                map[bone2.id] = updatedBone2
            }
        }

        // Re-update world transforms for all children
        val resultList = currentBones.map { map[it.id] ?: it }
        for (b in resultList) {
            val parent = b.parentId?.let { pId -> resultList.find { it.id == pId } }
            b.updateWorldTransform(parent)
        }

        return copy(bones = resultList)
    }

    companion object {
        fun createAnimeCharacterSkeleton(center: Vector2D = Vector2D(0f, 0f)): Skeleton {
            // Anime Rig hierarchy:
            // Root (pelvis) -> Torso -> Chest -> Neck -> Head
            // Chest -> ShoulderL -> UpperArmL -> ForearmL -> HandL
            // Chest -> ShoulderR -> UpperArmR -> ForearmR -> HandR
            // Pelvis -> ThighL -> ShinL -> FootL
            // Pelvis -> ThighR -> ShinR -> FootR
            // Head -> HairLeft, HairRight, HairBack
            val bones = listOf(
                // Spine & Torso
                Bone(id = "root_pelvis", name = "Pelvis", localPosition = center, localAngle = -90f, length = 45f),
                Bone(id = "chest", name = "Torso / Chest", parentId = "root_pelvis", localAngle = 0f, length = 55f),
                Bone(id = "neck", name = "Neck", parentId = "chest", localAngle = 0f, length = 20f),
                Bone(id = "head", name = "Head", parentId = "neck", localAngle = 0f, length = 60f),

                // Hair dynamics bones
                Bone(id = "hair_left", name = "Hair (L)", parentId = "head", localPosition = Vector2D(-20f, -10f), localAngle = 100f, length = 45f, colorHex = 0xFFEC4899),
                Bone(id = "hair_right", name = "Hair (R)", parentId = "head", localPosition = Vector2D(20f, -10f), localAngle = 80f, length = 45f, colorHex = 0xFFEC4899),

                // Left Arm (Screen Right / Character Left)
                Bone(id = "shoulder_l", name = "Shoulder (L)", parentId = "chest", localPosition = Vector2D(0f, 0f), localAngle = 100f, length = 28f),
                Bone(id = "arm_upper_l", name = "Upper Arm (L)", parentId = "shoulder_l", localAngle = 20f, length = 52f),
                Bone(id = "arm_fore_l", name = "Forearm (L)", parentId = "arm_upper_l", localAngle = -15f, length = 48f, isIkTarget = true, ikPoleDirection = -1f),
                Bone(id = "hand_l", name = "Hand (L)", parentId = "arm_fore_l", localAngle = 5f, length = 22f),

                // Right Arm (Screen Left / Character Right)
                Bone(id = "shoulder_r", name = "Shoulder (R)", parentId = "chest", localPosition = Vector2D(0f, 0f), localAngle = -100f, length = 28f),
                Bone(id = "arm_upper_r", name = "Upper Arm (R)", parentId = "shoulder_r", localAngle = -20f, length = 52f),
                Bone(id = "arm_fore_r", name = "Forearm (R)", parentId = "arm_upper_r", localAngle = 15f, length = 48f, isIkTarget = true, ikPoleDirection = 1f),
                Bone(id = "hand_r", name = "Hand (R)", parentId = "arm_fore_r", localAngle = -5f, length = 22f),

                // Left Leg
                Bone(id = "thigh_l", name = "Thigh (L)", parentId = "root_pelvis", localPosition = Vector2D(12f, -40f), localAngle = 175f, length = 70f),
                Bone(id = "shin_l", name = "Shin (L)", parentId = "thigh_l", localAngle = 5f, length = 68f, isIkTarget = true, ikPoleDirection = 1f),
                Bone(id = "foot_l", name = "Foot (L)", parentId = "shin_l", localAngle = 75f, length = 25f),

                // Right Leg
                Bone(id = "thigh_r", name = "Thigh (R)", parentId = "root_pelvis", localPosition = Vector2D(-12f, -40f), localAngle = -175f, length = 70f),
                Bone(id = "shin_r", name = "Shin (R)", parentId = "thigh_r", localAngle = -5f, length = 68f, isIkTarget = true, ikPoleDirection = 1f),
                Bone(id = "foot_r", name = "Foot (R)", parentId = "shin_r", localAngle = -75f, length = 25f)
            )
            return Skeleton(bones = bones).updateAllWorldTransforms()
        }
    }
}
