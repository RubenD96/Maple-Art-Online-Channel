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
    @Setter private int rx0, rx1, cy;
    @Setter private String name;
    @Setter private boolean move, hide;
    @Setter private boolean f;

    @Override
    public FieldObjectType getFieldObjectType() {
        return FieldObjectType.NPC;
    }

    @Override
    public Packet getEnterFieldPacket() {
        PacketWriter pw = new PacketWriter(22);

        pw.writeHeader(SendOpcode.NPC_ENTER_FIELD);
        pw.writeInt(getId()); // obj id
        pw.writeInt(npcId);

        pw.writePosition(getPosition());
        pw.write(f ? 1 : 0);
        pw.writeShort(getFoothold());

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
}
