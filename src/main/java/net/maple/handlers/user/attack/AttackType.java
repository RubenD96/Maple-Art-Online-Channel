package net.maple.handlers.user.attack;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AttackType {

    MELEE(0x00),
    SHOOT(0x01),
    MAGIC(0x02),
    BODY(0x03);

    @Getter @NonNull private final int type;
}
