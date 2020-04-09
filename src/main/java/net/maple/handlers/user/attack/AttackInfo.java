package net.maple.handlers.user.attack;

import client.Character;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import util.packet.PacketReader;

import java.util.Arrays;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Getter
public class AttackInfo {

    private final AttackType type;
    private final Character chr;
    private final PacketReader r;

    private int damagePerMob, mobCount, skillId, keyDown, action, attackTime;
    private byte option, attackActionType, attackSpeed;
    private boolean left;

    private DamageInfo[] damageInfo;

    public void decode() {
        r.readByte();
        r.readInteger();
        r.readInteger();

        byte v6 = r.readByte();
        damagePerMob = v6 & 0x0F;
        mobCount = v6 >> 4;

        r.readInteger();
        r.readInteger();

        skillId = r.readInteger();
        r.readByte();

        if (type == AttackType.MAGIC) {
            IntStream.range(0, 6).forEachOrdered(i -> r.readInteger());
        }

        r.readInteger();
        r.readInteger();
        r.readInteger();
        r.readInteger();
        // r.readInteger(); // keydown
        option = r.readByte();

        if (type == AttackType.SHOOT) {
            r.readByte();
        }

        short v17 = r.readShort();
        left = ((v17 >> 15) & 1) != 0;
        action = v17 & 0xFFF;

        r.readInteger();

        attackActionType = r.readByte();
        attackSpeed = r.readByte();
        attackTime = r.readInteger();

        r.readInteger();

        if (type == AttackType.SHOOT) {
            r.readShort();
            r.readShort();
            r.readByte();
            // shadow stars: readint
        }

        damageInfo = new DamageInfo[mobCount];
        IntStream.range(0, mobCount).forEach(i -> {
            DamageInfo info = new DamageInfo(type, chr);
            info.decode(r, damagePerMob);
            damageInfo[i] = info;
        });
    }

    public void apply() {
        Arrays.stream(damageInfo).forEach(DamageInfo::apply);
    }
}