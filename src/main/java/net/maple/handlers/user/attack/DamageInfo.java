package net.maple.handlers.user.attack;

import client.Character;
import field.object.FieldObjectType;
import field.object.life.FieldMob;
import util.packet.PacketReader;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.IntStream;

public class DamageInfo {

    private final AttackType type;
    private final Character chr;

    private int mobId, foreAction, calcDamageStatIndex;
    private byte hitAction, frameIdx;
    private boolean left, doomed;
    private short delay;
    private Point hitPosition, prevPosition;

    private int[] damage;

    public DamageInfo(AttackType type, Character chr) {
        this.type = type;
        this.chr = chr;
    }

    public AttackType getType() {
        return type;
    }

    public Character getChr() {
        return chr;
    }

    public int getMobId() {
        return mobId;
    }

    public int getForeAction() {
        return foreAction;
    }

    public int getCalcDamageStatIndex() {
        return calcDamageStatIndex;
    }

    public byte getHitAction() {
        return hitAction;
    }

    public byte getFrameIdx() {
        return frameIdx;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isDoomed() {
        return doomed;
    }

    public short getDelay() {
        return delay;
    }

    public Point getHitPosition() {
        return hitPosition;
    }

    public Point getPrevPosition() {
        return prevPosition;
    }

    public int[] getDamage() {
        return damage;
    }

    public void decode(PacketReader r, int damagePerMob) {
        mobId = r.readInteger();
        hitAction = r.readByte();
        byte v37 = r.readByte();
        foreAction = v37 & 0x7F;
        left = ((v37 >> 7) & 1) != 0;
        frameIdx = r.readByte();
        byte v38 = r.readByte();
        calcDamageStatIndex = v38 & 0x7F;
        doomed = ((v37 >> 7) & 1) != 0;
        hitPosition = r.readPoint();
        prevPosition = r.readPoint();

        delay = r.readShort();
        damage = new int[damagePerMob];
        IntStream.range(0, damagePerMob).forEach(i -> damage[i] = r.readInteger());
        r.readInteger();
    }

    public void apply() {
        FieldMob mob = (FieldMob) chr.getField().getObject(FieldObjectType.MOB, mobId);
        int totalDamage = Arrays.stream(damage).sum();

        if (mob != null) {
            mob.setController(chr);
            mob.damage(chr, totalDamage);
        }
    }
}
