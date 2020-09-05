package client.effects;

public enum FieldEffectType {

    SUMMON(0x00),
    TREMBLE(0x01),
    OBJECT(0x02),
    SCREEN(0x03),
    SOUND(0x04),
    MOB_HP_TAG(0x05),
    CHANGE_BGM(0x06),
    REWORD_BULLET(0x07);

    private final int value;

    FieldEffectType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
