package field.movement.fragment;

import field.object.life.FieldLife;
import lombok.Getter;
import lombok.Setter;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import java.awt.*;

public class FlyingBlockMoveFragment extends ActionMoveFragment {

    @Getter @Setter Point position;
    @Getter @Setter Point vposition;

    public FlyingBlockMoveFragment(byte movePathAttribute, PacketReader packetReader) {
        super(movePathAttribute, packetReader);
    }

    @Override
    public void apply(FieldLife life) {
        super.apply(life);

        life.setPosition(position);
    }

    @Override
    public void decodeData(PacketReader packet) {
        position = packet.readPoint();
        vposition = packet.readPoint();

        super.decodeData(packet);
    }

    @Override
    public void encodeData(PacketWriter packet) {
        packet.writePosition(position);
        packet.writePosition(vposition);

        super.encodeData(packet);
    }
}
