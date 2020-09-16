package field.obj.life

enum class MoveAbilityType(val type: Byte) {
    STOP(0x00),
    WALK(0x01),
    JUMP(0x02),
    FLY(0x03);
}