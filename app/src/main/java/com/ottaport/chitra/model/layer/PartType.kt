package com.ottaport.chitra.model.layer

import kotlinx.serialization.Serializable

@Serializable
enum class PartType(val displayName: String, val defaultBoneId: String) {
    HAIR_BACK("Hair (Back)", "head"),
    TORSO("Torso / Dress", "chest"),
    HEAD("Face / Head", "head"),
    EYE_L("Eye (L)", "head"),
    EYE_R("Eye (R)", "head"),
    BROW_L("Eyebrow (L)", "head"),
    BROW_R("Eyebrow (R)", "head"),
    MOUTH("Mouth", "head"),
    BLUSH("Blush", "head"),
    HAIR_FRONT("Hair (Bangs)", "head"),
    HAIR_SIDE_L("Hair Strand (L)", "hair_left"),
    HAIR_SIDE_R("Hair Strand (R)", "hair_right"),
    ARM_UPPER_L("Upper Arm (L)", "arm_upper_l"),
    ARM_FORE_L("Forearm (L)", "arm_fore_l"),
    HAND_L("Hand (L)", "hand_l"),
    ARM_UPPER_R("Upper Arm (R)", "arm_upper_r"),
    ARM_FORE_R("Forearm (R)", "arm_fore_r"),
    HAND_R("Hand (R)", "hand_r"),
    THIGH_L("Thigh (L)", "thigh_l"),
    SHIN_L("Shin (L)", "shin_l"),
    FOOT_L("Foot (L)", "foot_l"),
    THIGH_R("Thigh (R)", "thigh_r"),
    SHIN_R("Shin (R)", "shin_r"),
    FOOT_R("Foot (R)", "foot_r"),
    ACCESSORY("Ribbon / Accessory", "chest")
}
