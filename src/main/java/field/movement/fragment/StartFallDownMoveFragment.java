package field.movement.fragment;

import lombok.Getter;
import lombok.Setter;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import java.awt.*;

public class StartFallDownMoveFragment extends ActionMoveFragment {

    @Getter @Setter Point vposition;
    @Getter @Setter short fallStartFoothold;

    public StartFallDownMoveFragment(byte movePathAttribute, PacketReader packetReader) {
        super(movePathAttribute, packetReader);
    }

    @Override
    public void decodeData(PacketReader packet) {
        vposition = packet.readPoint();
        fallStartFoothold = packet.readShort();

        super.decodeData(packet);
    }

    @Override
    public void encodeData(PacketWriter packet) {
        packet.writePosition(vposition);
        packet.writeShort(fallStartFoothold);

        super.encodeData(packet);
    }
}
