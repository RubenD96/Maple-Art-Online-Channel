package client.effects;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FieldEffectType {

    SUMMON(0x00),
    TREMBLE(0x01),
    OBJECT(0x02),
    SCREEN(0x03),
    SOUND(0x04),
    MOB_HP_TAG(0x05),
    CHANGE_BGM(0x06),
    REWORD_BULLET(0x07);

    @NonNull @Getter final int value;
}
