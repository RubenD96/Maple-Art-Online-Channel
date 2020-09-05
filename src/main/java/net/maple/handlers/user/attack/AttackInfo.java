package net.maple.handlers.user.attack;

import client.Character;
import util.packet.PacketReader;

import java.util.Arrays;
import java.util.stream.IntStream;

public class AttackInfo {

    private final AttackType type;
    private final Character chr;
    private final PacketReader r;

    private int damagePerMob, mobCount, skillId, keyDown, action, attackTime;
    private byte option, attackActionType, attackSpeed;
    private boolean left;

    private DamageInfo[] damageInfo;

    public AttackInfo(AttackType type, Character chr, PacketReader r) {
        this.type = type;
        this.chr = chr;
        this.r = r;
    }

    public AttackType getType() {
        return type;
    }

    public Character getChr() {
        return chr;
    }

    public PacketReader getR() {
        return r;
    }

    public int getDamagePerMob() {
        return damagePerMob;
    }

    public int getMobCount() {
        return mobCount;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getKeyDown() {
        return keyDown;
    }

    public int getAction() {
        return action;
    }

    public int getAttackTime() {
        return attackTime;
    }

    public byte getOption() {
        return option;
    }

    public byte getAttackActionType() {
        return attackActionType;
    }

    public byte getAttackSpeed() {
        return attackSpeed;
    }

    public boolean isLeft() {
        return left;
    }

    public DamageInfo[] getDamageInfo() {
        return damageInfo;
    }

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