package field.movement

object MovePathAttribute {
    const val NORMAL: Byte = 0x0
    const val JUMP: Byte = 0x1
    const val IMPACT: Byte = 0x2
    const val IMMEDIATE: Byte = 0x3
    const val TELEPORT: Byte = 0x4
    const val HANG_ON_BACK: Byte = 0x5
    const val ASSAULTER: Byte = 0x6
    const val ASSASSINATION: Byte = 0x7
    const val RUSH: Byte = 0x8
    const val STAT_CHANGE: Byte = 0x9
    const val SIT_DOWN: Byte = 0xA
    const val START_FALL_DOWN: Byte = 0xB
    const val FALL_DOWN: Byte = 0xC
    const val START_WINGS: Byte = 0xD
    const val WINGS: Byte = 0xE
    const val ARAN_ADJUST: Byte = 0xF
    const val MOB_TOSS: Byte = 0x10
    const val FLYING_BLOCK: Byte = 0x11
    const val DASH_SLIDE: Byte = 0x12
    const val BMAGE_ADJUST: Byte = 0x13
    const val FLASH_JUMP: Byte = 0x14
    const val ROCKET_BOOSTER: Byte = 0x15
    const val BACK_STEP_SHOT: Byte = 0x16
    const val MOB_POWER_KNOCK_BACK: Byte = 0x17
    const val VERTICAL_JUMP: Byte = 0x18
    const val CUSTOM_IMPACT: Byte = 0x19
    const val COMBAT_STEP: Byte = 0x1A
    const val HIT: Byte = 0x1B
    const val TIME_BOMB_ATTACK: Byte = 0x1C
    const val SNOWBALL_TOUCH: Byte = 0x1D
    const val BUFF_ZONE_EFFECT: Byte = 0x1E
    const val MOB_LADDER: Byte = 0x1F
    const val MOB_RIGHT_ANGLE: Byte = 0x20
    const val MOB_STOP_NODE_START: Byte = 0x21
    const val MOB_BEFORE_NODE: Byte = 0x22
    const val MOB_ATTACK_RUSH: Byte = 0x23
    const val MOB_ATTACK_RUSH_STOP: Byte = 0x24
}