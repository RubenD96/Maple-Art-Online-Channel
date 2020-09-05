package field.object.life;

public enum MoveAbilityType {

    STOP((byte) 0x00),
    WALK((byte) 0x01),
    JUMP((byte) 0x02),
    FLY((byte) 0x03);

    private final byte type;

    MoveAbilityType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}
