package field.object.life;

import lombok.Getter;

public enum MobSummonType {

    NORMAL(-0x01),
    REGEN(-0x02),
    REVIVED(-0x03),
    SUSPENDED(-0x04),
    DELAY(-0x05),
    EFFECT(-0x00);

    @Getter private final int type;

    MobSummonType(int type) {
        this.type = type;
    }
}
