package net.maple.handlers.user.attack

enum class AttackType(val type: Int) {
    MELEE(0x00),
    SHOOT(0x01),
    MAGIC(0x02),
    BODY(0x03);
}
