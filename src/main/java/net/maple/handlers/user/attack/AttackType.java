package net.maple.handlers.user.attack;

public enum AttackType {

    MELEE(0x00),
    SHOOT(0x01),
    MAGIC(0x02),
    BODY(0x03);

    private final int type;

    AttackType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
