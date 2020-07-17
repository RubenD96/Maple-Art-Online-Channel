package field.object.life;

import client.Character;
import client.messages.IncEXPMessage;
import client.player.quest.QuestState;
import field.object.FieldObjectType;
import lombok.Getter;
import lombok.Setter;
import net.maple.SendOpcode;
import net.maple.packets.CharacterPackets;
import util.packet.Packet;
import util.packet.PacketWriter;

@Getter
public class FieldMob extends AbstractFieldControlledLife {

    private FieldMobTemplate template;
    @Setter private int hp, mp;
    @Setter private short home;

    public FieldMob(FieldMobTemplate template, boolean left) {
        this.template = template;
        moveAction = 3;
    }

    public void damage(Character chr, int damage) {
        synchronized (this) {
            hp -= damage;
        }

        float indicator = hp / (float) template.getMaxHP() * 100f;

        indicator = Math.min(100, indicator);
        indicator = Math.max(0, indicator);

        chr.write(showHpBar(indicator));

        if (hp <= 0) {
            kill(chr);
        }
    }

    public void kill(Character chr) {
        field.leave(this);
        chr.gainExp(template.getExp()); // todo share

        IncEXPMessage msg = new IncEXPMessage();
        msg.setLastHit(true);
        msg.setExp(template.getExp());
        chr.write(CharacterPackets.message(msg));

        if (chr.getRegisteredQuestMobs().contains(template.getId())) {
            chr.getQuests().values().stream()
                    .filter(quest -> quest.getState() == QuestState.PERFORM)
                    .filter(quest -> quest.getMobs().containsKey(template.getId()))
                    .forEach(quest -> quest.progress(template.getId()));
        }

        // todo drops
    }

    private Packet showHpBar(float indicator) {
        PacketWriter pw = new PacketWriter(7);

        pw.writeHeader(SendOpcode.MOB_HP_INDICATOR);
        pw.writeInt(id);
        pw.write((byte) indicator);

        return pw.createPacket();
    }

    @Override
    protected Packet getChangeControllerPacket(boolean setAsController) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.MOB_CHANGE_CONTROLLER);
        pw.writeBool(setAsController);
        pw.writeInt(id);
        if (setAsController) encode(pw, MobSummonType.REGEN);

        return pw.createPacket();
    }

    private void encode(PacketWriter pw, MobSummonType type) {
        pw.write(1);
        pw.writeInt(template.getId());
        pw.writeLong(0);
        pw.writeLong(0);

        pw.writePosition(position);
        pw.write(moveAction);
        pw.writeShort(foothold);
        pw.writeShort(home);

        pw.write(type.getType());
        if (type == MobSummonType.REVIVED || type.getType() >= 0) {
            pw.writeInt(0); // summon option
        }

        pw.write(0);
        pw.writeInt(0);
        pw.writeInt(0);
    }

    @Override
    public FieldObjectType getFieldObjectType() {
        return FieldObjectType.MOB;
    }

    public Packet getEnterFieldPacket(MobSummonType type) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.MOB_ENTER_FIELD);
        pw.writeInt(id);
        encode(pw, type);

        return pw.createPacket();
    }

    @Override
    public Packet getEnterFieldPacket() {
        return getEnterFieldPacket(MobSummonType.REGEN);
    }

    @Override
    public Packet getLeaveFieldPacket() {
        PacketWriter pw = new PacketWriter(7);

        pw.writeHeader(SendOpcode.MOB_LEAVE_FIELD);
        pw.writeInt(id);
        pw.write(1);

        return pw.createPacket();
    }
}
