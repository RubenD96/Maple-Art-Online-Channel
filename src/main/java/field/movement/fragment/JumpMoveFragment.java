package field.movement.fragment;

import lombok.Getter;
import lombok.Setter;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import java.awt.*;

public class JumpMoveFragment extends ActionMoveFragment {

    @Getter @Setter Point vposition;

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
