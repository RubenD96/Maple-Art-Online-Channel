package field.object.life;

import field.object.FieldObjectType;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.maple.SendOpcode;
import util.packet.Packet;
import util.packet.PacketWriter;

@RequiredArgsConstructor
@Getter
public class FieldNPC extends AbstractFieldControlledLife {

    @NonNull final private int npcId;
    @Setter protected boolean move;

    public FieldNPC(FieldNPC npc) {
        this.npcId = npc.npcId;
        this.name = npc.name;
        this.move = npc.move;
    }

    @Override
    public FieldObjectType getFieldObjectType() {
        return FieldObjectType.NPC;
    }

    @Override
    public Packet getEnterFieldPacket() {
        PacketWriter pw = new PacketWriter(22);

        pw.writeHeader(SendOpcode.NPC_ENTER_FIELD);
        pw.writeInt(id); // obj id
        pw.writeInt(npcId);

        pw.writePosition(position);
        pw.write(f ? 1 : 0);
        pw.writeShort(foothold);

        pw.writeShort(rx0);
        pw.writeShort(rx1);
        pw.writeBool(!hide);

        return pw.createPacket();
    }

    @Override
    public Packet getLeaveFieldPacket() {
        PacketWriter pw = new PacketWriter(6);

        pw.writeHeader(SendOpcode.NPC_LEAVE_FIELD);
        pw.writeInt(getId()); // obj id

        return pw.createPacket();
    }

    @Override
    protected Packet getChangeControllerPacket(boolean setAsController) {
        PacketWriter pw = new PacketWriter(7);

        pw.writeHeader(SendOpcode.NPC_CHANGE_CONTROLLER);
        pw.writeBool(setAsController);
        pw.writeInt(getId()); // obj id

        return pw.createPacket();
    }

    @Override
    public String toString() {
        return "FieldNPC{" +
                "npcId=" + npcId +
                ", move=" + move +
                ", rx0=" + rx0 +
                ", rx1=" + rx1 +
                ", cy=" + cy +
                ", name='" + name + '\'' +
                ", hide=" + hide +
                ", f=" + f +
                ", foothold=" + foothold +
                ", position=" + position +
                ", id=" + id +
                '}';
    }
}
