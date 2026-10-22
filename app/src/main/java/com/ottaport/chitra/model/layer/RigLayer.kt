package com.ottaport.chitra.model.layer

import com.ottaport.chitra.model.Vector2D
import com.ottaport.chitra.model.mesh.MeshGrid
import kotlinx.serialization.Serializable

@Serializable
data class RigLayer(
    val id: String,
    val name: String,
    val type: PartType,
    val zIndex: Int,
    val attachedBoneId: String,
    val localOffset: Vector2D = Vector2D.Zero,
    val localRotation: Float = 0f,
    val scale: Vector2D = Vector2D.One,
    val opacity: Float = 1.0f,
    val isVisible: Boolean = true,
    val isLocked: Boolean = false,
    val meshGrid: MeshGrid = MeshGrid(),
    val primaryColorHex: Long = 0xFF475569,
    val secondaryColorHex: Long = 0xFF94A3B8,
    val customImagePath: String? = null
) {
    companion object {
        fun createDefaultAnimeLayers(): List<RigLayer> {
            return listOf(
                RigLayer(
                    id = "layer_hair_back",
                    name = "Hair Back",
                    type = PartType.HAIR_BACK,
                    zIndex = 0,
                    attachedBoneId = "head",
                    primaryColorHex = 0xFF38BDF8, // Vibrant Cyan/Anime Blue
                    secondaryColorHex = 0xFF0284C7
                ),
                RigLayer(
                    id = "layer_arm_upper_r",
                    name = "Upper Arm (R)",
                    type = PartType.ARM_UPPER_R,
                    zIndex = 1,
                    attachedBoneId = "arm_upper_r",
                    primaryColorHex = 0xFFFFEDD5, // Skin
                    secondaryColorHex = 0xFFFDBA74
                ),
                RigLayer(
                    id = "layer_arm_fore_r",
                    name = "Forearm (R)",
                    type = PartType.ARM_FORE_R,
                    zIndex = 2,
                    attachedBoneId = "arm_fore_r",
                    primaryColorHex = 0xFFFFEDD5,
                    secondaryColorHex = 0xFFFDBA74
                ),
                RigLayer(
                    id = "layer_hand_r",
                    name = "Hand (R)",
                    type = PartType.HAND_R,
                    zIndex = 3,
                    attachedBoneId = "hand_r",
                    primaryColorHex = 0xFFFFEDD5,
                    secondaryColorHex = 0xFFFDBA74
                ),
                RigLayer(
                    id = "layer_leg_r",
                    name = "Leg (R)",
                    type = PartType.THIGH_R,
                    zIndex = 4,
                    attachedBoneId = "thigh_r",
                    primaryColorHex = 0xFF1E293B, // Dark tights
                    secondaryColorHex = 0xFF334155
                ),
                RigLayer(
                    id = "layer_shin_r",
                    name = "Shin (R)",
                    type = PartType.SHIN_R,
                    zIndex = 5,
                    attachedBoneId = "shin_r",
                    primaryColorHex = 0xFF1E293B,
                    secondaryColorHex = 0xFF334155
                ),
                RigLayer(
                    id = "layer_foot_r",
                    name = "Shoe (R)",
                    type = PartType.FOOT_R,
                    zIndex = 6,
                    attachedBoneId = "foot_r",
                    primaryColorHex = 0xFF475569,
                    secondaryColorHex = 0xFF0F172A
                ),
                RigLayer(
                    id = "layer_leg_l",
                    name = "Leg (L)",
                    type = PartType.THIGH_L,
                    zIndex = 7,
                    attachedBoneId = "thigh_l",
                    primaryColorHex = 0xFF1E293B,
                    secondaryColorHex = 0xFF334155
                ),
                RigLayer(
                    id = "layer_shin_l",
                    name = "Shin (L)",
                    type = PartType.SHIN_L,
                    zIndex = 8,
                    attachedBoneId = "shin_l",
                    primaryColorHex = 0xFF1E293B,
                    secondaryColorHex = 0xFF334155
                ),
                RigLayer(
                    id = "layer_foot_l",
                    name = "Shoe (L)",
                    type = PartType.FOOT_L,
                    zIndex = 9,
                    attachedBoneId = "foot_l",
                    primaryColorHex = 0xFF475569,
                    secondaryColorHex = 0xFF0F172A
                ),
                RigLayer(
                    id = "layer_torso",
                    name = "Torso / Dress",
                    type = PartType.TORSO,
                    zIndex = 10,
                    attachedBoneId = "chest",
                    primaryColorHex = 0xFFFFFFFF, // Clean anime sailor uniform white
                    secondaryColorHex = 0xFF2563EB  // Sailor collar blue
                ),
                RigLayer(
                    id = "layer_accessory",
                    name = "Chest Ribbon",
                    type = PartType.ACCESSORY,
                    zIndex = 11,
                    attachedBoneId = "chest",
                    primaryColorHex = 0xFFF43F5E, // Bright red ribbon
                    secondaryColorHex = 0xFFBE123C
                ),
                RigLayer(
                    id = "layer_arm_upper_l",
                    name = "Upper Arm (L)",
                    type = PartType.ARM_UPPER_L,
                    zIndex = 12,
                    attachedBoneId = "arm_upper_l",
                    primaryColorHex = 0xFFFFEDD5,
                    secondaryColorHex = 0xFFFDBA74
                ),
                RigLayer(
                    id = "layer_arm_fore_l",
                    name = "Forearm (L)",
                    type = PartType.ARM_FORE_L,
                    zIndex = 13,
                    attachedBoneId = "arm_fore_l",
                    primaryColorHex = 0xFFFFEDD5,
                    secondaryColorHex = 0xFFFDBA74
                ),
                RigLayer(
                    id = "layer_hand_l",
                    name = "Hand (L)",
                    type = PartType.HAND_L,
                    zIndex = 14,
                    attachedBoneId = "hand_l",
                    primaryColorHex = 0xFFFFEDD5,
                    secondaryColorHex = 0xFFFDBA74
                ),
                RigLayer(
                    id = "layer_head",
                    name = "Face",
                    type = PartType.HEAD,
                    zIndex = 15,
                    attachedBoneId = "head",
                    primaryColorHex = 0xFFFFFAF0, // Fair skin
                    secondaryColorHex = 0xFFFFEDD5
                ),
                RigLayer(
                    id = "layer_eyes",
                    name = "Eyes",
                    type = PartType.EYE_L,
                    zIndex = 16,
                    attachedBoneId = "head",
                    primaryColorHex = 0xFF2563EB, // Big anime sparkling eyes
                    secondaryColorHex = 0xFF60A5FA
                ),
                RigLayer(
                    id = "layer_mouth",
                    name = "Mouth",
                    type = PartType.MOUTH,
                    zIndex = 17,
                    attachedBoneId = "head",
                    primaryColorHex = 0xFFFB7185,
                    secondaryColorHex = 0xFFE11D48
                ),
                RigLayer(
                    id = "layer_blush",
                    name = "Blush",
                    type = PartType.BLUSH,
                    zIndex = 18,
                    attachedBoneId = "head",
                    primaryColorHex = 0xFFFDA4AF,
                    secondaryColorHex = 0xFFF43F5E
                ),
                RigLayer(
                    id = "layer_hair_front",
                    name = "Hair Front & Bangs",
                    type = PartType.HAIR_FRONT,
                    zIndex = 19,
                    attachedBoneId = "head",
                    primaryColorHex = 0xFF38BDF8, // Cyan anime hair
                    secondaryColorHex = 0xFF0284C7
                ),
                RigLayer(
                    id = "layer_hair_side_l",
                    name = "Hair Strand (L)",
                    type = PartType.HAIR_SIDE_L,
                    zIndex = 20,
                    attachedBoneId = "hair_left",
                    primaryColorHex = 0xFF38BDF8,
                    secondaryColorHex = 0xFF0284C7
                ),
                RigLayer(
                    id = "layer_hair_side_r",
                    name = "Hair Strand (R)",
                    type = PartType.HAIR_SIDE_R,
                    zIndex = 21,
                    attachedBoneId = "hair_right",
                    primaryColorHex = 0xFF38BDF8,
                    secondaryColorHex = 0xFF0284C7
                )
            )
        }
    }
}
