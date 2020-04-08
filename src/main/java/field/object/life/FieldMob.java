package field.object.life;

import field.object.FieldObjectType;
import lombok.Getter;
import lombok.Setter;
import net.maple.SendOpcode;
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
