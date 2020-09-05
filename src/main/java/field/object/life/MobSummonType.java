package field.object.life;

public enum MobSummonType {

    NORMAL(-0x01),
    REGEN(-0x02),
    REVIVED(-0x03),
    SUSPENDED(-0x04),
    DELAY(-0x05),
    EFFECT(-0x00);

    private final int type;

    MobSummonType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
