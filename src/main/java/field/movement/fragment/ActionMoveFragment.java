package field.movement.fragment;

import field.object.life.FieldLife;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class ActionMoveFragment extends AbstractMovementFragment {

    byte moveAction;
    short elapse;

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
