package field.movement.fragment;

import util.packet.PacketReader;
import util.packet.PacketWriter;

import java.awt.*;

public class JumpMoveFragment extends ActionMoveFragment {

    Point vposition;

    public JumpMoveFragment(byte movePathAttribute, PacketReader packetReader) {
        super(movePathAttribute, packetReader);
    }

    @Override
    public void decodeData(PacketReader packet) {
        vposition = packet.readPoint();

        super.decodeData(packet);
    }

    @Override
    public void encodeData(PacketWriter packet) {
        packet.writePosition(vposition);

        super.encodeData(packet);
    }
}
