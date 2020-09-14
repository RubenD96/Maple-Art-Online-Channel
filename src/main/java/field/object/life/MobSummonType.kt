package field.`object`.life

enum class MobSummonType(val type: Int) {
    NORMAL(-0x01),
    REGEN(-0x02),
    REVIVED(-0x03),
    SUSPENDED(-0x04),
    DELAY(-0x05),
    EFFECT(-0x00);
}