package field.movement.fragment;

import field.life.FieldLife;
import lombok.Getter;
import lombok.Setter;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class ActionMoveFragment extends AbstractMovementFragment {

    @Getter @Setter byte moveAction;
    @Getter @Setter short elapse;

    public ActionMoveFragment(byte movePathAttribute, PacketReader packetReader) {
        super(movePathAttribute, packetReader);
    }

    @Override
    public void apply(FieldLife life) {
        life.setMoveAction(moveAction);
    }

    @Override
    public void decodeData(PacketReader packet) {
        moveAction = packet.readByte();
        elapse = packet.readShort();
    }

    @Override
    public void encodeData(PacketWriter packet) {
        packet.write(moveAction);
        packet.writeShort(elapse);
    }
}
